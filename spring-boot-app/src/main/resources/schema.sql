-- =============================================================================
-- Schema initialization script
-- Adapted from: sql/create_test_db.sql
--
-- Creates the ACCOUNTS table used by the migrated COBOL SQL application.
-- This script is executed on application startup via spring.sql.init.
-- =============================================================================

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
