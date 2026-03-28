# Employee Performance Tracker API

Spring Boot backend for tracking employee performance reviews across review cycles.

## Run

```bash
./gradlew bootRun                                              # H2 in-memory
./gradlew bootRun --args='--spring.profiles.active=postgres'   # PostgreSQL
./gradlew test
```

H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:performancedb`, user: `sa`, no password)

## Endpoints

```
POST   /employees                                      Create employee
GET    /employees?department=ENGINEERING&minRating=3.5&ratingStrategy=MEDIAN
POST   /reviews                                        Submit review
GET    /employees/{id}/reviews                         Reviews for employee
GET    /cycles/{id}/summary?ratingStrategy=WEIGHTED_RECENT
GET    /cycles/{id}/export?format=CSV&ratingStrategy=MEDIAN
```

## Design Patterns

**Observer** — `ReviewService.submit()` publishes `ReviewSubmittedEvent`. Three listeners react independently: `AuditLogListener` (logs), `NotificationListener` (sends alerts via Factory), `LowRatingAlertListener` (flags scores ≤ 2). Adding a new side-effect = one new `@EventListener`, zero changes to the service.

**Strategy** — `RatingCalculationStrategy` with three implementations: `SimpleAverageStrategy` (DB-level AVG, fast path), `WeightedRecentStrategy` (linear weights toward recent reviews), `MedianStrategy` (ignores outliers). Selected at runtime via `?ratingStrategy=` param.

**Factory** — `NotificationFactory` routes events to `SlackNotification` (low ratings), `InAppNotification` (normal), or `EmailNotification` (new hires). `ReportExporterFactory` resolves JSON/CSV/TEXT exporters by `ExportFormat` enum. Both auto-discover implementations via Spring injection.

## Schema Decisions

- `department` column has a CHECK constraint against the `Department` enum values.
- `rating` stored as integer 1–5 via JPA `AttributeConverter`, not as an ordinal — safe against enum reordering and keeps `AVG(rating)` queries returning values on the 1–5 scale directly.
- No unique constraint on `(employee_id, cycle_id)` in `performance_review` — allows self-review, manager review, and re-reviews in the same cycle.
- `reviewer_id` FK on reviews points to `employee` with `ON DELETE SET NULL` — the reviewer is an employee too; if they leave, the review data survives.
- `ON DELETE RESTRICT` on employee→reviews and employee→goals — prevents accidental deletion of employees who have review history.
- `chk_reviewer_not_self` CHECK constraint prevents a reviewer_id from equaling the employee_id (self-reviews use `reviewer_id = NULL` instead).
- Composite index `(employee_id, cycle_id)` on reviews and `(cycle_id, status)` on goals target the two hottest query patterns.
- `active` soft-delete flag on employee instead of hard deletes — preserves all historical data.

---

## System Design

### 500 concurrent managers running reports during performance season

The slow queries are `GET /cycles/{id}/summary` and `GET /employees?department=&minRating=`. These are read-heavy aggregations. Writes are rare during report season — reviews are already submitted.

**What I'd actually do, in order:**

1. **Put a Redis cache in front of the summary endpoint.** Use `@Cacheable("cycleSummary")` keyed on `(cycleId, strategyKey)`. Set TTL to 5 minutes for open cycles. For closed cycles (end_date in the past), cache indefinitely — the data is immutable. This alone handles 500 concurrent managers because 500 people hitting the same cycle summary means 1 DB query + 499 cache hits.

2. **Add a read replica and route read-only traffic to it.** Spring's `AbstractRoutingDataSource` + `@Transactional(readOnly = true)` makes this a config change, not a code change. The primary handles the few writes; the replica handles the aggregation storm.

3. **Connection pool tuning.** HikariCP with `maximumPoolSize=15` per instance, `connectionTimeout=5000ms`, `statement_timeout=10s` in PostgreSQL. If one slow report hogs a connection, it times out instead of starving the pool.

4. **Run 3–4 app instances behind an ALB.** Spring Boot is stateless here — no sessions, no sticky routing needed. Auto-scale group set to add instances if average CPU > 60% for 3 minutes.

This setup handles 500 concurrent managers comfortably. I wouldn't go beyond this unless monitoring showed an actual bottleneck.

### `/cycles/{id}/summary` getting slow at 100k+ reviews

The current implementation runs 3 aggregate queries per request: `AVG(rating)`, `GROUP BY employee ORDER BY AVG(rating)` for top performer, and `GROUP BY status` for goals. At 100k reviews, the middle query is the bottleneck — it scans every review row in the cycle to compute per-employee averages.

**What I'd actually do:**

1. **First check: is the index being used?** Run `EXPLAIN ANALYZE` on the top-performer query. If the `idx_review_cycle` index isn't being picked up (e.g., the table stats are stale), `ANALYZE performance_review` fixes it for free.

2. **Materialized summary table.** Create a `cycle_employee_rating` table with columns `(cycle_id, employee_id, avg_rating, review_count)`. Populate it via an async listener on `ReviewSubmittedEvent` — the Observer pattern already fires this event, so I'd add a `SummaryMaterializationListener` that upserts the row. The summary endpoint then reads from this table instead of aggregating 100k rows. One index scan on `cycle_id` returns ~200 rows (one per employee), not 100k.

3. **If materialization lag is unacceptable**, use a PostgreSQL materialized view with `REFRESH MATERIALIZED VIEW CONCURRENTLY` triggered by a cron job every 2 minutes. Same effect, but managed by the database.

I would not partition the reviews table, add columnar storage, or reach for Elasticsearch at 100k rows. That's still a small table. The materialized summary solves it cleanly.

### Where I'd add caching and what I'd cache

| Cached data | Key | TTL | Invalidation |
|---|---|---|---|
| Closed cycle summary | `cycle:{id}:summary:{strategy}` | Infinite | Manual bust on data correction |
| Open cycle summary | `cycle:{id}:summary:{strategy}` | 5 min | `ReviewSubmittedEvent` listener evicts |
| Employee filter results | `employees:filter:{dept}:{minRating}:{strategy}` | 2 min | Any POST /reviews or POST /employees evicts |
| Single employee lookup | `employee:{id}` | 10 min | POST /employees evicts |

**What I would not cache:** the export endpoint (low traffic, byte[] payloads bloat Redis), individual review lists (high cardinality, low reuse).

**Implementation:** Spring `@Cacheable` backed by Redis. Cache eviction via `@CacheEvict` in a dedicated `CacheInvalidationListener` subscribed to `ReviewSubmittedEvent` and `EmployeeCreatedEvent` — the Observer pattern makes this a clean addition.
