# COBOL to Java Spring Boot Migration

This project is a migration of the [COBOL-Examples](../) codebase from GnuCOBOL to a Java Spring Boot 3.x application.

## Architecture Overview

The migration follows a phased approach, replacing COBOL programs with equivalent Java components organized into standard Spring Boot layers.

### COBOL Module → Java Package Mapping

| COBOL Module | Java Component | Description |
|---|---|---|
| `sql/sql_example.cbl` | `model/Account.java`, `repository/AccountRepository.java`, `service/AccountService.java` | Database access with JPA replacing embedded SQL/ODBC |
| `sql/create_test_db.sql` | `db/migration/V1__create_accounts_table.sql` | Flyway migration replacing manual SQL scripts |
| `json_generate/json_generate.cbl` | `model/RecordDto.java`, `service/SerializationService.java` | Jackson replacing COBOL JSON GENERATE |
| `xml_generate/xml_generate.cbl` | `model/RecordXmlDto.java`, `service/SerializationService.java` | JAXB replacing COBOL XML GENERATE |
| `trim/trim.cbl` | `util/StringUtils.java` | `String.strip()` replacing FUNCTION TRIM |
| `unstring/unstring.cbl` | `util/StringUtils.java` | `String.split()` + regex replacing UNSTRING |
| `is_numeric/is_numeric.cbl` | `util/StringUtils.java` | Regex replacing IS NUMERIC |
| `numval_test/numval_test.cbl` | `util/StringUtils.java` | `Double.parseDouble()` replacing FUNCTION NUMVAL |
| `search/search.cbl` | `util/SearchUtils.java` | `Collections.binarySearch()` replacing SEARCH ALL |
| `redifines/redefines.cbl` | `model/Customer.java` | Polymorphism replacing REDEFINES |
| `comp_test/comp_test.cbl` | N/A (native Java int/long) | COMP types map directly to Java primitives |
| `merge_sort/merge_sort_test.cbl` | `service/FileSortService.java` | Java streams/sort replacing MERGE/SORT |
| `report_writer/report_test.cbl` | `service/ReportService.java` | String formatting replacing Report Writer |
| `sub_program/main_app.cbl` + `sub.cbl` | `service/SubProgramService.java` | Spring @Service replacing CALL/CANCEL |
| `accept/` + `read_command_args/` | `cli/CliRunner.java` | CommandLineRunner replacing ACCEPT/DISPLAY |

### REST API Endpoints

| Endpoint | Method | COBOL Equivalent |
|---|---|---|
| `/api/accounts` | GET | Menu option 1: Display all accounts |
| `/api/accounts/disabled` | GET | Menu option 2: Display disabled accounts |
| `/api/accounts/search?q={query}` | GET | Menu option 3: Query accounts |
| `/api/serialize/json` | POST | json_generate.cbl |
| `/api/serialize/xml` | POST | xml_generate.cbl |
| `/api/utility/trim?input={text}` | GET | trim.cbl |
| `/api/utility/unstring?input={text}&delimiters={d}` | GET | unstring.cbl |
| `/api/utility/is-numeric?input={text}` | GET | is_numeric.cbl |
| `/api/utility/numval?input={text}` | GET | numval_test.cbl |
| `/api/utility/search` | POST | search.cbl |
| `/api/utility/merge-sort` | POST | merge_sort_test.cbl |
| `/api/utility/report` | POST | report_test.cbl |
| `/api/utility/subprogram` | POST | sub_program/ |

## Prerequisites

- Java 17+
- PostgreSQL 15+ (or use Docker Compose)

## Quick Start

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

### 2. Build the Application

```bash
./gradlew build
```

### 3. Run the Application (REST API mode)

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

### 4. Run in CLI Mode (mirrors original COBOL terminal UI)

```bash
./gradlew bootRun --args='--spring.profiles.active=cli'
```

## Testing

```bash
./gradlew test
```

Tests use an embedded H2 database, so no PostgreSQL is required for testing.

### Test Coverage

- `AccountRepositoryTest` — Integration tests with `@DataJpaTest` for all three query patterns
- `SerializationServiceTest` — JSON and XML output verification
- `StringUtilsTest` — All string processing functions (trim, unstring, isNumeric, numval)
- `SearchUtilsTest` — Binary and sequential search behavior
- `FileSortServiceTest` — Merge/sort output verification
- `ReportServiceTest` — Report format verification
- `AccountControllerTest` — `@WebMvcTest` for all REST endpoints

## Configuration

Database connection is configured via environment variables in `src/main/resources/application.yml`:

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/cobol_db_example` | JDBC connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | (none) | Database password |

Set these environment variables before running the application. The `docker-compose.yml` also uses `DB_USERNAME` and `DB_PASSWORD` for the PostgreSQL container.

This configuration mirrors the original COBOL connection from `sql/sql_example.cbl` which connected to `localhost:5432/cobol_db_example`.
