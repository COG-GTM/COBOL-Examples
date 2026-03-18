-- Flyway migration V1: Create ACCOUNTS table
-- Matches the existing PostgreSQL table structure from sql/create_test_db.sql
--
-- COBOL record mapping:
--   ws-sql-account-id          PIC 9(5)   -> id SERIAL
--   ws-sql-account-first-name  PIC X(8)   -> first_name VARCHAR
--   ws-sql-account-last-name   PIC X(8)   -> last_name VARCHAR
--   ws-sql-account-phone       PIC X(10)  -> phone VARCHAR
--   ws-sql-account-address     PIC X(22)  -> address VARCHAR
--   ws-sql-account-is-enabled  PIC X      -> is_enabled VARCHAR(1)
--   ws-sql-account-create-dt   PIC X(20)  -> create_dt TIMESTAMP
--   ws-sql-account-mod-dt      PIC X(20)  -> mod_dt TIMESTAMP

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
