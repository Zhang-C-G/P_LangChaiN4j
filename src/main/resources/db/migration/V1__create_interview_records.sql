CREATE TABLE IF NOT EXISTS interview_records (
    id BIGSERIAL PRIMARY KEY,
    action_type VARCHAR(32) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    request_payload TEXT NOT NULL,
    response_payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
