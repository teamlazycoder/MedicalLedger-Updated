CREATE TABLE IF NOT EXISTS medical_records (
                                               id BIGSERIAL PRIMARY KEY,
                                               patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id BIGINT REFERENCES doctors(id),
    record_type VARCHAR(50) CHECK (record_type IN ('LAB_RESULT', 'IMAGING', 'PRESCRIPTION',
                                   'SURGERY_REPORT', 'DISCHARGE_SUMMARY', 'VACCINATION', 'ALLERGY', 'CONSULTATION', 'EMERGENCY', 'ALL')),
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT,
    ipfs_hash VARCHAR(100) NOT NULL,
    encryption_key TEXT NOT NULL,
    blockchain_tx_id VARCHAR(100),
    description TEXT,
    diagnosis TEXT,
    treatment TEXT,
    access_count INTEGER DEFAULT 0,
    is_deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_medical_records_patient ON medical_records(patient_id);
CREATE INDEX idx_medical_records_doctor ON medical_records(doctor_id);
CREATE INDEX idx_medical_records_ipfs ON medical_records(ipfs_hash);
CREATE INDEX idx_medical_records_blockchain ON medical_records(blockchain_tx_id);
