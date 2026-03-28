INSERT INTO employee (name, department, role, joining_date) VALUES
    ('Alice Johnson',   'ENGINEERING',  'Senior Engineer',      '2021-03-15'),
    ('Bob Smith',       'ENGINEERING',  'Staff Engineer',       '2019-07-01'),
    ('Carol Lee',       'PRODUCT',      'Product Manager',      '2022-01-10'),
    ('David Kim',       'ENGINEERING',  'Junior Engineer',      '2023-06-20'),
    ('Eva Martinez',    'DESIGN',       'UX Designer',          '2020-11-05');

INSERT INTO review_cycle (name, start_date, end_date) VALUES
    ('Q1 2025', '2025-01-01', '2025-03-31'),
    ('Q2 2025', '2025-04-01', '2025-06-30');

INSERT INTO performance_review (employee_id, cycle_id, reviewer_id, rating, reviewer_notes) VALUES
    (1, 1, 2,    5, 'Exceptional quarter. Led the migration project ahead of schedule.'),
    (2, 1, 1,    4, 'Solid contributions to platform stability.'),
    (2, 1, NULL, 3, 'Self-review: Could have communicated blockers earlier.'),
    (3, 1, 1,    4, 'Drove roadmap alignment across three teams.'),
    (4, 1, 2,    3, 'Growing steadily. Needs mentoring on system design.'),
    (5, 1, 3,    5, 'Redesigned onboarding flow — NPS jumped 20 points.');


INSERT INTO goal (employee_id, cycle_id, title, status) VALUES
    (1, 1, 'Complete database migration',          'COMPLETED'),
    (1, 1, 'Mentor two junior engineers',           'COMPLETED'),
    (2, 1, 'Reduce P99 latency by 30%',            'COMPLETED'),
    (2, 1, 'Write architecture RFC for new service', 'MISSED'),
    (3, 1, 'Ship Q1 roadmap on time',              'COMPLETED'),
    (3, 1, 'Run three user research sessions',      'PENDING'),
    (4, 1, 'Pass system design module',             'COMPLETED'),
    (4, 1, 'Contribute to on-call rotation',        'MISSED'),
    (5, 1, 'Redesign onboarding flow',              'COMPLETED'),
    (5, 1, 'Create component library docs',         'PENDING');
