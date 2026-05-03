INSERT INTO users (id, employee_id, username, password, first_name, last_name, email, active, global_access_level, created_at, updated_at) VALUES
(1, 'OB-0001', 'alex', 'password', 'Alex', 'Adkins', 'alex.adkins@operationsboard.demo', true, 'SUPER_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'OB-0002', 'executive', 'password', 'Morgan', 'Reed', 'morgan.reed@operationsboard.demo', true, 'EXECUTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(3, 'ENG-0001', 'eng.manager', 'password', 'Erin', 'Carter', 'erin.carter@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'ENG-0002', 'eng.member1', 'password', 'Devon', 'Brooks', 'devon.brooks@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'ENG-0003', 'eng.member2', 'password', 'Taylor', 'Nguyen', 'taylor.nguyen@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'ENG-0004', 'eng.member3', 'password', 'Jordan', 'Patel', 'jordan.patel@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(7, 'OPS-0001', 'ops.manager', 'password', 'Riley', 'Morgan', 'riley.morgan@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'OPS-0002', 'ops.member1', 'password', 'Casey', 'Johnson', 'casey.johnson@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'OPS-0003', 'ops.member2', 'password', 'Avery', 'Smith', 'avery.smith@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'OPS-0004', 'ops.member3', 'password', 'Jamie', 'Williams', 'jamie.williams@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(11, 'CYB-0001', 'cyber.manager', 'password', 'Quinn', 'Davis', 'quinn.davis@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'CYB-0002', 'cyber.member1', 'password', 'Skyler', 'Brown', 'skyler.brown@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'CYB-0003', 'cyber.member2', 'password', 'Parker', 'Miller', 'parker.miller@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'CYB-0004', 'cyber.member3', 'password', 'Rowan', 'Wilson', 'rowan.wilson@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(15, 'DBA-0001', 'dba.manager', 'password', 'Harper', 'Moore', 'harper.moore@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 'DBA-0002', 'dba.member1', 'password', 'Logan', 'Taylor', 'logan.taylor@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 'DBA-0003', 'dba.member2', 'password', 'Emerson', 'Anderson', 'emerson.anderson@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'DBA-0004', 'dba.member3', 'password', 'Finley', 'Thomas', 'finley.thomas@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(19, 'NET-0001', 'net.manager', 'password', 'Cameron', 'Jackson', 'cameron.jackson@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 'NET-0002', 'net.member1', 'password', 'Reese', 'White', 'reese.white@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 'NET-0003', 'net.member2', 'password', 'Dakota', 'Harris', 'dakota.harris@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(22, 'NET-0004', 'net.member3', 'password', 'Sage', 'Martin', 'sage.martin@operationsboard.demo', true, 'NONE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO teams (id, name, description, active, created_at, updated_at) VALUES
(1, 'Engineering', 'Application engineering and release execution.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Operations', 'Operational coordination, deployment support, and readiness management.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Cyber Security', 'Security review, risk validation, and authorization support.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Database Administration', 'Database readiness, migration support, and performance validation.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Network Infrastructure', 'Network routing, firewall coordination, and infrastructure validation.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO memberships (id, user_id, team_id, role, active, created_at) VALUES
(1, 3, 1, 'MANAGER', true, CURRENT_TIMESTAMP),
(2, 4, 1, 'MEMBER', true, CURRENT_TIMESTAMP),
(3, 5, 1, 'MEMBER', true, CURRENT_TIMESTAMP),
(4, 6, 1, 'MEMBER', true, CURRENT_TIMESTAMP),

(5, 7, 2, 'MANAGER', true, CURRENT_TIMESTAMP),
(6, 8, 2, 'MEMBER', true, CURRENT_TIMESTAMP),
(7, 9, 2, 'MEMBER', true, CURRENT_TIMESTAMP),
(8, 10, 2, 'MEMBER', true, CURRENT_TIMESTAMP),

(9, 11, 3, 'MANAGER', true, CURRENT_TIMESTAMP),
(10, 12, 3, 'MEMBER', true, CURRENT_TIMESTAMP),
(11, 13, 3, 'MEMBER', true, CURRENT_TIMESTAMP),
(12, 14, 3, 'MEMBER', true, CURRENT_TIMESTAMP),

(13, 15, 4, 'MANAGER', true, CURRENT_TIMESTAMP),
(14, 16, 4, 'MEMBER', true, CURRENT_TIMESTAMP),
(15, 17, 4, 'MEMBER', true, CURRENT_TIMESTAMP),
(16, 18, 4, 'MEMBER', true, CURRENT_TIMESTAMP),

(17, 19, 5, 'MANAGER', true, CURRENT_TIMESTAMP),
(18, 20, 5, 'MEMBER', true, CURRENT_TIMESTAMP),
(19, 21, 5, 'MEMBER', true, CURRENT_TIMESTAMP),
(20, 22, 5, 'MEMBER', true, CURRENT_TIMESTAMP);

INSERT INTO tasks (id, team_id, parent_task_id, title, description, status, priority, due_date, created_by, assigned_user_id, blocker_reason, completed_at, hidden_after, created_at, updated_at) VALUES
(1, 1, NULL, 'Production Release 24.6 Readiness', 'Coordinate application readiness for the 24.6 production release.', 'IN_PROGRESS', 'CRITICAL', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), 3, 4, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 1, 'Finalize release branch', 'Lock the release branch and verify merge freeze.', 'COMPLETE', 'HIGH', DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 3, 5, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, 1, 'Resolve API regression in task audit', 'Fix regression causing delayed audit timeline rendering.', 'BLOCKED', 'CRITICAL', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 3, 4, 'Waiting on Cyber approval for test data replay.', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 1, 1, 'Validate Angular workflow tree build', 'Run final UI validation for the workflow tree and board navigation.', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), 3, 6, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 1, NULL, 'Executive dashboard polish', 'Clean dashboard copy, spacing, and chart labels before leadership review.', 'CLAIMED', 'NORMAL', DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), 3, 5, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 1, NULL, 'Report export smoke test', 'Confirm PDF and Excel exports generate clean executive reports.', 'OPEN', 'NORMAL', DATE_ADD(CURRENT_DATE, INTERVAL 6 DAY), 3, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 1, NULL, 'Board drag-and-drop validation', 'Verify board column counters and task movement behavior.', 'COMPLETE', 'NORMAL', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 3, 6, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 1, NULL, 'Route refresh fix verification', 'Confirm team switching reloads board, tree, and metrics views.', 'IN_PROGRESS', 'HIGH', CURRENT_DATE, 3, 4, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(9, 2, NULL, 'Deployment Operations Coordination', 'Coordinate the deployment window and readiness handoff.', 'IN_PROGRESS', 'CRITICAL', DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY), 7, 8, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 2, 9, 'Confirm change window', 'Confirm approved maintenance window and stakeholder notifications.', 'COMPLETE', 'HIGH', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 7, 9, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 2, 9, 'Prepare rollback checklist', 'Validate rollback checklist for production release.', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), 7, 10, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 2, 9, 'Coordinate go/no-go briefing', 'Prepare go/no-go briefing for release leadership.', 'CLAIMED', 'NORMAL', DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), 7, 8, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 2, NULL, 'Refresh deployment runbook', 'Update operational runbook with latest contacts and escalation path.', 'OPEN', 'NORMAL', DATE_ADD(CURRENT_DATE, INTERVAL 8 DAY), 7, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 2, NULL, 'Capture post-deployment lessons learned', 'Prepare post-deployment review template.', 'OPEN', 'LOW', DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), 7, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 2, NULL, 'Validate bridge line coverage', 'Confirm operational bridge coverage for deployment window.', 'COMPLETE', 'NORMAL', DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), 7, 9, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(16, 3, NULL, 'Cyber Authorization Review', 'Complete cyber review required before release can proceed.', 'BLOCKED', 'CRITICAL', DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 11, 12, 'Waiting on updated vulnerability scan package.', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 3, 16, 'Review vulnerability scan results', 'Review latest scanner output and identify release blockers.', 'BLOCKED', 'CRITICAL', DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), 11, 13, 'Scanner package is incomplete.', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 3, 16, 'Validate POA&M exceptions', 'Confirm exception language and mitigation owner.', 'IN_PROGRESS', 'HIGH', CURRENT_DATE, 11, 14, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 3, NULL, 'Security control evidence upload', 'Upload evidence artifacts for review.', 'CLAIMED', 'NORMAL', DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY), 11, 12, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 3, NULL, 'Firewall rule risk review', 'Review proposed network rule changes.', 'OPEN', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), 11, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 3, NULL, 'Audit response package', 'Prepare cyber response package for executive review.', 'COMPLETE', 'NORMAL', DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), 11, 13, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(22, 4, NULL, 'Database Migration Readiness', 'Prepare database migration tasks for production release.', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), 15, 16, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, 4, 22, 'Validate migration script', 'Review and test migration script against staging snapshot.', 'COMPLETE', 'HIGH', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 15, 17, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(24, 4, 22, 'Backup verification', 'Confirm backup and recovery procedure before cutover.', 'IN_PROGRESS', 'CRITICAL', CURRENT_DATE, 15, 18, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 4, 22, 'Performance baseline', 'Capture baseline query performance before release.', 'CLAIMED', 'NORMAL', DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), 15, 16, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(26, 4, NULL, 'Refresh DBA contact roster', 'Update support roster and escalation contacts.', 'OPEN', 'LOW', DATE_ADD(CURRENT_DATE, INTERVAL 9 DAY), 15, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(27, 4, NULL, 'Clean archived staging data', 'Remove stale staging data after export verification.', 'CANCELLED', 'LOW', DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 15, NULL, NULL, NULL, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 7 DAY), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(28, 5, NULL, 'Network Cutover Preparation', 'Prepare network infrastructure dependencies for release.', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY), 19, 20, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(29, 5, 28, 'Validate firewall change request', 'Confirm firewall rule request aligns with approved release scope.', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), 19, 21, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(30, 5, 28, 'Confirm load balancer target health', 'Verify target groups and health checks.', 'CLAIMED', 'NORMAL', DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), 19, 22, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(31, 5, 28, 'DNS rollback plan', 'Confirm DNS rollback plan and TTL expectations.', 'OPEN', 'NORMAL', DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), 19, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(32, 5, NULL, 'Network monitoring validation', 'Confirm dashboards and alerts for deployment window.', 'COMPLETE', 'NORMAL', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 19, 20, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(33, 5, NULL, 'VPN access review', 'Review deployment team VPN access before maintenance window.', 'OPEN', 'LOW', DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), 19, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO comments (id, task_id, user_id, type, message, created_at) VALUES
(1, 3, 4, 'BLOCKER', 'Regression fix is ready, but test replay requires Cyber approval before final validation.', CURRENT_TIMESTAMP),
(2, 16, 12, 'BLOCKER', 'Cyber package is missing the final scanner export. Release remains blocked until received.', CURRENT_TIMESTAMP),
(3, 24, 18, 'GENERAL', 'Backup validation is in progress. Recovery test is scheduled for this afternoon.', CURRENT_TIMESTAMP),
(4, 9, 8, 'GENERAL', 'Operations bridge staffing is confirmed for the proposed deployment window.', CURRENT_TIMESTAMP),
(5, 28, 21, 'GENERAL', 'Firewall review is underway. No network conflict identified so far.', CURRENT_TIMESTAMP);

INSERT INTO task_audit (id, task_id, changed_by, action, field_name, old_value, new_value, created_at) VALUES
(1, 1, 3, 'TASK_CREATED', NULL, NULL, 'Production Release 24.6 Readiness', CURRENT_TIMESTAMP),
(2, 3, 4, 'BLOCKER_ADDED', 'blocker_reason', NULL, 'Waiting on Cyber approval for test data replay.', CURRENT_TIMESTAMP),
(3, 16, 11, 'BLOCKER_ADDED', 'blocker_reason', NULL, 'Waiting on updated vulnerability scan package.', CURRENT_TIMESTAMP),
(4, 24, 15, 'STATUS_CHANGED', 'status', 'CLAIMED', 'IN_PROGRESS', CURRENT_TIMESTAMP),
(5, 28, 19, 'STATUS_CHANGED', 'status', 'CLAIMED', 'IN_PROGRESS', CURRENT_TIMESTAMP);