-- Schema initialization for the COBOL migration Spring Boot application.
-- Derived from sql/create_test_db.sql (lines 8-18).

CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    phone VARCHAR NOT NULL,
    address VARCHAR NOT NULL,
    is_enabled VARCHAR(1) NOT NULL DEFAULT 'N',
    create_dt TIMESTAMP DEFAULT NOW(),
    mod_dt TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (id)
);
