DROP VIEW IF EXISTS project_manpower_view CASCADE;

CREATE OR REPLACE VIEW project_manpower_view AS
 SELECT p.project_code,
    p.active,
    p.status,
    s.project_stream_id,
    s.stream_name,
    t.project_task_id,
  m.manpower_touched,
    m.project_skill_id,
    t.task_status,
    t.task_start_date,
    t.task_end_date,
    t.actual_start_date,
    t.actual_end_date,
    m.work_date,
    f.staff_id,
    t.task_name,
    f.staff_name,
    m.loading
   FROM project_manpower m,
    project_skill k,
    project_task t,
    project_stream s,
    project p,
    staff f
  WHERE p.project_code::text = s.project_code::text
  AND t.project_stream_id = s.project_stream_id
  AND m.project_skill_id = k.project_skill_id
  AND k.project_task_id = t.project_task_id
  AND m.staff_id::text = f.staff_id::text
  ORDER BY p.project_code, s.project_stream_id, t.project_task_id, f.staff_name;