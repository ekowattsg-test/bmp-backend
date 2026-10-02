ALTER TABLE project_task_progress
    ADD COLUMN IF NOT EXISTS inspection_date VARCHAR(255),
    ADD COLUMN IF NOT EXISTS inspected_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS verified_progress INTEGER,
    ADD COLUMN IF NOT EXISTS inspection_photos VARCHAR(2000);
