CREATE TABLE idempotency_keys
(
    id              UUID PRIMARY KEY,
    user_id         UUID         NOT NULL,
    idempotency_key VARCHAR(100) NOT NULL,
    transaction_id  UUID,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_idempotency_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT fk_idempotency_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES transactions (id),

    CONSTRAINT uq_idempotency_user_key
        UNIQUE (user_id, idempotency_key)
);