-- Customers table
CREATE TABLE IF NOT EXISTS customers (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

COMMENT ON TABLE customers IS 'CRM customers';
COMMENT ON COLUMN customers.id IS 'Primary key';
COMMENT ON COLUMN customers.name IS 'Full name';
COMMENT ON COLUMN customers.email IS 'Unique email';
COMMENT ON COLUMN customers.phone IS 'Contact phone';
COMMENT ON COLUMN customers.created_at IS 'Creation timestamp';
COMMENT ON COLUMN customers.updated_at IS 'Update timestamp';