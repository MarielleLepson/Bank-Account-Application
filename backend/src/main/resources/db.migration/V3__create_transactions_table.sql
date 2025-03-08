CREATE TABLE IF NOT EXISTS transactions
(
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL UNIQUE,
    account_id INTEGER NOT NULL,
    transaction_type VARCHAR(10) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    last_modified_at TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(100) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions (account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_type ON transactions (transaction_type);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_date ON transactions (transaction_date);
