-- Adapted from sql/create_test_db.sql for Flyway migration
-- Removed \c command (Flyway connects to the target database directly)

DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts (
    id bigserial not null,
    first_name varchar not null,
    last_name varchar not null,
    phone varchar not null,
    address varchar not null,
    is_enabled varchar(1) not null default 'N',
    create_dt timestamp default now(),
    mod_dt timestamp default now(),
    primary key (id)
);
