CREATE TABLE IF NOT EXISTS outbox (
    id BIGSERIAL PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    service VARCHAR (36) NOT NUll,
    payload JSONB NOT NULL,
    created_at timestamp NOT NULL DEFAULT NOW(),
    published_at timestamp NULL,
    retry_count INTEGER default 0
);

-- Индекс для быстрой выборки неотправленных событий
CREATE INDEX idx_outbox_unpublished ON outbox(id)
WHERE published_at IS NULL;

CREATE INDEX idx_outbox_created ON outbox(created_at);