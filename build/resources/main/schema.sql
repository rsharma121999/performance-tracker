CREATE TABLE IF NOT EXISTS employee (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(200)    NOT NULL,
    department      VARCHAR(50)     NOT NULL,
    role            VARCHAR(150)    NOT NULL,
    joining_date    DATE            NOT NULL,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_department CHECK (department IN (
        'ENGINEERING','PRODUCT','DESIGN','MARKETING','SALES',
        'HR','FINANCE','OPERATIONS','LEGAL','QA'
    ))
);

CREATE INDEX IF NOT EXISTS idx_employee_department ON employee (department);
CREATE INDEX IF NOT EXISTS idx_employee_active     ON employee (active);


CREATE TABLE IF NOT EXISTS review_cycle (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL UNIQUE,
    start_date  DATE            NOT NULL,
    end_date    DATE            NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_cycle_dates CHECK (end_date >= start_date)
);


CREATE TABLE IF NOT EXISTS performance_review (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id     BIGINT      NOT NULL,
    cycle_id        BIGINT      NOT NULL,
    reviewer_id     BIGINT,
    rating          SMALLINT    NOT NULL,
    reviewer_notes  VARCHAR(4000),
    submitted_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_employee FOREIGN KEY (employee_id)
        REFERENCES employee (id) ON DELETE RESTRICT,
    CONSTRAINT fk_review_cycle FOREIGN KEY (cycle_id)
        REFERENCES review_cycle (id) ON DELETE RESTRICT,
    CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id)
        REFERENCES employee (id) ON DELETE SET NULL,

    CONSTRAINT chk_rating_range CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviewer_not_self CHECK (reviewer_id IS NULL OR reviewer_id <> employee_id)
);

CREATE INDEX IF NOT EXISTS idx_review_employee  ON performance_review (employee_id);
CREATE INDEX IF NOT EXISTS idx_review_cycle     ON performance_review (cycle_id);
CREATE INDEX IF NOT EXISTS idx_review_emp_cycle ON performance_review (employee_id, cycle_id);


CREATE TABLE IF NOT EXISTS goal (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id BIGINT          NOT NULL,
    cycle_id    BIGINT          NOT NULL,
    title       VARCHAR(500)    NOT NULL,
    status      VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_goal_employee FOREIGN KEY (employee_id)
        REFERENCES employee (id) ON DELETE RESTRICT,
    CONSTRAINT fk_goal_cycle FOREIGN KEY (cycle_id)
        REFERENCES review_cycle (id) ON DELETE RESTRICT,

    CONSTRAINT chk_goal_status CHECK (status IN ('PENDING', 'COMPLETED', 'MISSED'))
);

CREATE INDEX IF NOT EXISTS idx_goal_employee ON goal (employee_id);
CREATE INDEX IF NOT EXISTS idx_goal_cycle    ON goal (cycle_id);
CREATE INDEX IF NOT EXISTS idx_goal_status   ON goal (cycle_id, status);
