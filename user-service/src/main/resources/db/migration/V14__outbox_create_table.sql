-- Create the Outbox table
CREATE TABLE outbox_events
(
    event_id          UUID PRIMARY KEY,
    correlation_id    VARCHAR(255),
    event_type        VARCHAR(255) NOT NULL,
    aggregate_id      VARCHAR(255) NOT NULL, -- userId
    payload           JSONB        NOT NULL, -- Efficient binary JSON
    status            VARCHAR(20)  NOT NULL, -- PENDING, PROCESSED, FAILED
    retry_count       INT DEFAULT 0,
    next_retry_at     TIMESTAMPTZ  NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL,
    processed_at      TIMESTAMPTZ,
    destination_topic VARCHAR(255) NOT NULL
);

-- 1. PARTIAL INDEX for the Relay Job (The "Speed" Index)
-- Only indexes PENDING work. Instant lookups for the background worker.
CREATE INDEX idx_outbox_active_work
    ON outbox_events (next_retry_at)
    WHERE status = 'PENDING';

-- 2. INDEX for the Cleanup Job
-- Helps find old PROCESSED messages without scanning the whole table.
CREATE INDEX idx_outbox_processed_at
    ON outbox_events (processed_at)
    WHERE status = 'PROCESSED';

-- 3. INDEX for Troubleshooting (Audit)
-- Fast lookups by User ID.
CREATE INDEX idx_outbox_aggregate_id
    ON outbox_events (aggregate_id);

-- 4. INDEX for Distributed Tracing
-- Fast lookups by Correlation ID across services.
CREATE INDEX idx_outbox_correlation_id
    ON outbox_events (correlation_id);