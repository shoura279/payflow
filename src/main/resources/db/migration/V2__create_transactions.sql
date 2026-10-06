CREATE TABLE transactions
(
    id                 UUID PRIMARY KEY,

    sender_wallet_id   UUID           NOT NULL,
    receiver_wallet_id UUID           NOT NULL,

    amount             NUMERIC(19, 4) NOT NULL,
    currency           VARCHAR(3)     NOT NULL,

    status             VARCHAR(20)    NOT NULL,
    reference          VARCHAR(100)   NOT NULL UNIQUE,

    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transaction_sender_wallet
        FOREIGN KEY (sender_wallet_id)
            REFERENCES wallets (id),

    CONSTRAINT fk_transaction_receiver_wallet
        FOREIGN KEY (receiver_wallet_id)
            REFERENCES wallets (id),

    CONSTRAINT chk_transaction_amount
        CHECK (amount > 0),

    CONSTRAINT chk_transaction_different_wallets
        CHECK (sender_wallet_id <> receiver_wallet_id)
);