# COBOL → Spring Boot Migration

A Java Spring Boot port of the GnuCOBOL `sql/sql_example.cbl` program from this
repository. The original COBOL console app connected to PostgreSQL through the
esqlOC precompiler / unixODBC driver, declared cursors, and ran a text menu for
CRUD-style reads against an `ACCOUNTS` table. This module replaces all of that
with a standard Spring Boot REST service backed by Spring Data JPA.

## Prerequisites

- Java 17+
- Maven 3.6+ (or use the bundled `mvnw` if generated)
- PostgreSQL (for running against a real database; the test suite uses in-memory H2)

## Build & Run

```bash
cd springboot-app

# Build and run the test suite (uses in-memory H2, no DB required)
mvn clean verify

# Run against a local PostgreSQL (defaults shown; override via env vars)
DB_HOST=localhost DB_PORT=5432 DB_NAME=cobol_db_example \
DB_USER=postgres DB_PASSWORD=password \
mvn spring-boot:run
```

On startup, Flyway applies the migrations in `src/main/resources/db/migration`,
creating the `accounts` table and seeding the same sample rows as the original
`sql/create_test_db.sql`.

## API Endpoints

| Method | Path                         | Replaces COBOL menu option        |
|--------|------------------------------|-----------------------------------|
| GET    | `/api/accounts`              | 1) Display all accounts           |
| GET    | `/api/accounts/disabled`     | 2) Display disabled accounts      |
| GET    | `/api/accounts/search?q=...` | 3) Query accounts (LIKE search)   |

Example:

```bash
curl http://localhost:8080/api/accounts
curl http://localhost:8080/api/accounts/disabled
curl "http://localhost:8080/api/accounts/search?q=Tester"
```

Jackson serializes responses to JSON automatically, replacing the manual
`JSON GENERATE` usage shown in `json_generate/json_generate.cbl`.

## Mapping Notes (COBOL → Spring Boot)

| COBOL construct (`sql/sql_example.cbl`)                          | Spring Boot equivalent                              |
|------------------------------------------------------------------|----------------------------------------------------|
| `01 ws-sql-account-record` declare section (lines 45-53)         | `model/Account.java` JPA entity                    |
| `id serial` PK (`sql/create_test_db.sql`)                        | `@Id @GeneratedValue(IDENTITY) Long id`            |
| `is_enabled pic x` + level-88 `'Y'`/`'N'`                        | `Boolean isEnabled` via `YesNoBooleanConverter`    |
| `DECLARE ACCOUNT-ALL-CUR` (lines 118-124)                        | `AccountRepository.findAllByOrderByIdAsc()`        |
| `DECLARE ACCOUNT-DISABLED-CUR` (lines 129-137)                   | `findByIsEnabledFalseOrderByIdAsc()`               |
| `DECLARE ACCOUNT-QUERY-CUR` LIKE search (lines 141-153)          | `@Query search(...)` with `%term%` wildcards       |
| `display-all/disabled/query-accounts` paragraphs                 | `service/AccountService.java` methods              |
| Console menu (lines 158-185)                                     | `controller/AccountController.java` REST endpoints |
| ODBC connection string (lines 36-43)                             | `application.yml` `spring.datasource.*`            |
| `sql/create_test_db.sql`                                         | Flyway migrations `V1__`/`V2__`                    |
| `CALL "sub-app" USING ...` (`sub_program/main_app.cbl`)          | `@Service` bean injected via constructor           |
| `JSON GENERATE` (`json_generate/json_generate.cbl`)              | Jackson auto-serialization                         |

## What this eliminates

- esqlOC precompiler step (`EXEC SQL` → generated COBOL)
- manual cursor open/fetch/close lifecycle
- fixed-width `PIC X(n)` declarations and length bookkeeping for LIKE searches
- unixODBC driver / ODBC connection string configuration
