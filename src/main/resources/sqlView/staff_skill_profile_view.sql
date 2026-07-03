DROP VIEW IF EXISTS staff_skill_profile_view CASCADE;

CREATE OR REPLACE VIEW staff_skill_profile_view AS
SELECT
    p.staff_skill_profile_id,
    p.acquired_date,
    p.certification_link,
    p.expiry_date,
    p.issued_by,
    p.no_expiry,
    p.staff_skill_id,
    p.staff_id,
    s.staff_name,
    k.skill_category,
    k.skill_description,
    k.skill_name
FROM public.staff_skill_profile p,
     public.staff s,
     public.staff_skill k
WHERE p.staff_id = s.staff_id
  AND k.staff_skill_id = p.staff_skill_id
ORDER BY s.staff_name, k.skill_name ASC;
