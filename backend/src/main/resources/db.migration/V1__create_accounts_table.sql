CREATE TABLE IF NOT EXISTS accounts
(
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL UNIQUE,
    account_number VARCHAR(255) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    CONSTRAINT unique_account_number UNIQUE (account_number)
);

CREATE INDEX idx_account_number ON accounts (account_number);
CREATE INDEX idx_account_name ON accounts (account_name);