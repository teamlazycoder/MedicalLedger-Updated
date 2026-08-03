CREATE TABLE IF NOT EXISTS audit_logs (
                                          id BIGSERIAL PRIMARY KEY,
                                          record_id BIGINT REFERENCES medical_records(id),
    patient_id BIGINT REFERENCES patients(id),
    actor_id BIGINT REFERENCES users(id),
    action VARCHAR(50) NOT NULL CHECK (action IN ('VIEW', 'UPLOAD', 'UPDATE', 'DELETE',
                                       'DOWNLOAD', 'GRANT_ACCESS', 'REVOKE_ACCESS', 'LOGIN', 'LOGOUT')),
    ip_address VARCHAR(45),
    user_agent TEXT,
    blockchain_tx_id VARCHAR(100),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_audit_patient ON audit_logs(patient_id);
CREATE INDEX idx_audit_actor ON audit_logs(actor_id);
CREATE INDEX idx_audit_record ON audit_logs(record_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_created ON audit_logs(created_at);
