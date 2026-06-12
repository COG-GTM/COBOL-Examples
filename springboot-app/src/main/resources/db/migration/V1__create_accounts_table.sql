-- Ported from sql/create_test_db.sql in the original COBOL project.
-- The CREATE DATABASE / \c statements are dropped because Flyway runs inside an
-- already-selected database (configured via spring.datasource.url).

CREATE TABLE IF NOT EXISTS accounts (
    id          bigserial   NOT NULL,
    first_name  varchar     NOT NULL,
    last_name   varchar     NOT NULL,
    phone       varchar     NOT NULL,
    address     varchar     NOT NULL,
    is_enabled  varchar(1)  NOT NULL DEFAULT 'N',
    create_dt   timestamp   DEFAULT now(),
    mod_dt      timestamp   DEFAULT now(),
    PRIMARY KEY (id)
);
