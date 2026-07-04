DROP VIEW IF EXISTS project_skill_view CASCADE;

CREATE OR REPLACE VIEW project_skill_view AS
 SELECT p.project_code,
  p.active,
  p.status,
  s.project_stream_id,
  s.stream_name,
  t.project_task_id,
  COALESCE(MAX(pm.manpower_touched), 0) AS manpower_touched,
  t.task_status,
  t.task_start_date,
  t.task_end_date,
  t.actual_start_date,
  t.actual_end_date,
  f.staff_skill_id,
  t.task_name,
  f.skill_name,
  f.skill_category,
  f.skill_description,
  m.unit
   FROM project_skill m
   JOIN project_task t ON m.project_task_id = t.project_task_id
   JOIN project_stream s ON t.project_stream_id = s.project_stream_id
   JOIN project p ON p.project_code::text = s.project_code::text
   JOIN staff_skill f ON COALESCE((to_jsonb(m) ->> 'skill_id')::bigint, (to_jsonb(m) ->> 'project_skill_id')::bigint) = f.staff_skill_id
   LEFT JOIN project_manpower pm ON pm.project_skill_id = m.project_skill_id
  GROUP BY p.project_code,
   p.active,
   p.status,
   s.project_stream_id,
   s.stream_name,
   t.project_task_id,
   t.task_status,
   t.task_start_date,
   t.task_end_date,
   t.actual_start_date,
   t.actual_end_date,
   f.staff_skill_id,
   t.task_name,
   f.skill_name,
   f.skill_category,
   f.skill_description,
   m.unit
  ORDER BY p.project_code, s.project_stream_id, t.project_task_id, f.skill_name;