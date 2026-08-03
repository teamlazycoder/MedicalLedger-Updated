CREATE TABLE IF NOT EXISTS blockchain_transactions (
                                                       id BIGSERIAL PRIMARY KEY,
                                                       tx_id VARCHAR(100) UNIQUE,
    chaincode_function VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'FAILED')),
    block_number BIGINT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP
    );

CREATE INDEX idx_blockchain_tx_id ON blockchain_transactions(tx_id);
CREATE INDEX idx_blockchain_status ON blockchain_transactions(status);
CREATE INDEX idx_blockchain_function ON blockchain_transactions(chaincode_function);
