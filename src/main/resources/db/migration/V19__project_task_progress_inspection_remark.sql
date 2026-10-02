ALTER TABLE project_task_progress
    ADD COLUMN IF NOT EXISTS inspection_remark TEXT;
