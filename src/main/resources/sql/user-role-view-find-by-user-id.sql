SELECT userrole_id, user_id, level, role_id, Role, company_id
FROM userrole_view
WHERE user_id = :userId
  AND company_id = :companyId;
