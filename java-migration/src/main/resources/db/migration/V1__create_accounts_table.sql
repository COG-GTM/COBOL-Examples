-- Flyway migration: Create ACCOUNTS table
-- Source: sql/create_test_db.sql from COBOL-Examples

CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL NOT NULL,
    first_name VARCHAR(8) NOT NULL,
    last_name VARCHAR(8) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    address VARCHAR(22) NOT NULL,
    is_enabled VARCHAR(1) NOT NULL DEFAULT 'N',
    create_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- Populate fake account data (from create_test_db.sql)
INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('John', 'Tester', '1555555010', '123 Fake St, Nowhere', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Mike', 'Tester1', '1555555012', '122 Real St, Nowhere', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Mary', 'Tester2', '1555555013', '121 ABC St, Nowhere', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Jack', 'Tester3', '1555555014', '120 Rock St, Nowhere', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Bob', 'Tester4', '1555555015', '119 Truck St, Nowhere', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Paula', 'Tester5', '1555550165', '118 Car St, Nowhere', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('James', 'Tester6', '1555550176', '117 Land St, Nowhere', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Jane', 'Tester7', '1555550187', '116 Sea St, Nowhere', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Bill', 'Tester8', '1555550198', '115 Dock St, Nowhere', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Lucy', 'Tester9', '1555550209', '114 Beach St, Nowhere', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO accounts (first_name, last_name, phone, address, is_enabled, create_dt, mod_dt)
VALUES ('Richard', 'Tester10', '1555550210', '113 Water St, Nowhere', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
