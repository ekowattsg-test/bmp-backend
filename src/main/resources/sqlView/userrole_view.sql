CREATE OR REPLACE VIEW userrole_view AS
SELECT
    ur.id AS userrole_id,
    u.id AS user_id,
    u.level AS level,
    r.id AS role_id,
    r.role AS role,
    u.company_id AS company_id
FROM user_role ur
JOIN app_user u ON u.id = ur.user_id
JOIN role r ON r.id = ur.role_id;
