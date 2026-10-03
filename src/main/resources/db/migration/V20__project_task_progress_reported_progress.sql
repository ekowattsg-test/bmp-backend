ALTER TABLE project_task_progress
    ADD COLUMN IF NOT EXISTS reported_progress INTEGER;
