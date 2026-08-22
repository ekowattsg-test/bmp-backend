CREATE TABLE IF NOT EXISTS message (
    message_id BIGSERIAL PRIMARY KEY,
    sender_staff_id VARCHAR(50) NOT NULL,
    recipient_type VARCHAR(20) NOT NULL,
    recipient_staff_id VARCHAR(50),
    project_code VARCHAR(50),
    content VARCHAR(2000) NOT NULL,
    source VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    reference_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    read_at TIMESTAMP,
    project_group_scope VARCHAR(20)
);

CREATE INDEX IF NOT EXISTS idx_message_sender_staff_id ON message(sender_staff_id);
CREATE INDEX IF NOT EXISTS idx_message_recipient_staff_id ON message(recipient_staff_id);
CREATE INDEX IF NOT EXISTS idx_message_project_code ON message(project_code);
CREATE INDEX IF NOT EXISTS idx_message_recipient_type ON message(recipient_type);
CREATE INDEX IF NOT EXISTS idx_message_created_at ON message(created_at);

CREATE TABLE IF NOT EXISTS message_read_receipt (
    receipt_id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    staff_id VARCHAR(50) NOT NULL,
    read_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_message_read_receipt_message_staff UNIQUE (message_id, staff_id),
    CONSTRAINT fk_message_read_receipt_message FOREIGN KEY (message_id) REFERENCES message(message_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_message_read_receipt_message_id ON message_read_receipt(message_id);
CREATE INDEX IF NOT EXISTS idx_message_read_receipt_staff_id ON message_read_receipt(staff_id);
