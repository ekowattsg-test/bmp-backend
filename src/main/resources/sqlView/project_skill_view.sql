DROP VIEW IF EXISTS project_skill_view CASCADE;

CREATE OR REPLACE VIEW project_skill_view AS
 SELECT p.project_code,
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
   FROM project_skill m,
  project_task t,
  project_stream s,
  project p,
  staff_skill f
  WHERE p.project_code::text = s.project_code::text
  AND t.project_stream_id = s.project_stream_id
  AND m.project_task_id = t.project_task_id
  AND COALESCE((to_jsonb(m) ->> 'skill_id')::bigint, (to_jsonb(m) ->> 'project_skill_id')::bigint) = f.staff_skill_id
  ORDER BY p.project_code, s.project_stream_id, t.project_task_id, f.skill_name;