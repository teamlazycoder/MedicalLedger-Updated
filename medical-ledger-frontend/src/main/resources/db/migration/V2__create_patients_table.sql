
CREATE TABLE IF NOT EXISTS patients (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    blood_type VARCHAR(5),
    contact_number VARCHAR(20),
    address TEXT,
    allergies TEXT,
    chronic_conditions TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_patients_user_id ON patients(user_id);
CREATE INDEX idx_patients_name ON patients(last_name, first_name);