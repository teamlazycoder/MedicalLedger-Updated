
CREATE TABLE IF NOT EXISTS consent_policies (
                                                id BIGSERIAL PRIMARY KEY,
                                                patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    record_type VARCHAR(50) DEFAULT 'ALL',
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'REVOKED', 'EXPIRED', 'PENDING')),
    purpose TEXT,
    blockchain_tx_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_consent_patient ON consent_policies(patient_id);
CREATE INDEX idx_consent_doctor ON consent_policies(doctor_id);
CREATE INDEX idx_consent_status ON consent_policies(status);
CREATE INDEX idx_consent_dates ON consent_policies(start_date, end_date);