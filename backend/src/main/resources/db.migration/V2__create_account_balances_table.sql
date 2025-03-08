CREATE TABLE IF NOT EXISTS account_balances
(
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL UNIQUE,
    account_id INTEGER NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(100),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
    );

CREATE INDEX IF NOT EXISTS idx_id ON account_balances (id);
CREATE INDEX IF NOT EXISTS idx_account_id ON account_balances (account_id);
CREATE INDEX IF NOT EXISTS idx_currency ON account_balances (currency);
