-- Flyway migration V1: Create accounts table
-- Migrated from: sql/create_test_db.sql
-- Original COBOL program: sql/sql_example.cbl

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

-- Seed data from original COBOL test database

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('John', 'Tester', '15555550100', '123 Fake St, Nowhere', 'Y', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Mike', 'Tester1', '15555550121', '122 Real St, Nowhere', 'Y', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Mary', 'Tester2', '15555550132', '121 ABC St, Nowhere', 'Y', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Jack', 'Tester3', '15555550143', '120 Rock St, Nowhere', 'Y', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Bob', 'Tester4', '15555550154', '119 Truck St, Nowhere', 'N', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Paula', 'Tester5', '1555550165', '118 Car St, Nowhere', 'N', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('James', 'Tester6', '1555550176', '117 Land St, Nowhere', 'Y', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Jane', 'Tester7', '1555550187', '116 Sea St, Nowhere', 'Y', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Bill', 'Tester8', '1555550198', '115 Dock St, Nowhere', 'N', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Lucy', 'Tester9', '1555550209', '114 Beach St, Nowhere', 'Y', NOW(), NOW());

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Richard', 'Tester10', '1555550210', '113 Water St, Nowhere', 'Y', NOW(), NOW());
