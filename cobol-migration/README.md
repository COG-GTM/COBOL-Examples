# COBOL to Spring Boot Migration

This Spring Boot 3.x application replaces the GnuCOBOL programs in the [COBOL-Examples](../) repository with modern Java 17+ equivalents. The original COBOL files are retained in their directories for reference.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 12+ (for database features)

### Build & Test
```bash
cd cobol-migration
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```
The application starts on `http://localhost:8080`.

### Database Setup
1. Create a PostgreSQL database named `cobol_db_example`
2. Flyway migrations run automatically on startup (creates the `accounts` table and seeds test data)
3. Configure connection in `src/main/resources/application.properties` if defaults differ

## API Endpoints

| Method | Endpoint | Replaces | Description |
|--------|----------|----------|-------------|
| GET | `/api/accounts` | sql_example.cbl menu option 1 | List all accounts |
| GET | `/api/accounts/disabled` | sql_example.cbl menu option 2 | List disabled accounts |
| GET | `/api/accounts/search?q={term}` | sql_example.cbl menu option 3 | Search accounts (LIKE wildcard) |
| GET | `/api/serialize/json` | json_generate.cbl | JSON serialization demo |
| GET | `/api/serialize/xml` | xml_generate.cbl | XML serialization demo |
| POST | `/api/customers/merge-sort` | merge_sort_test.cbl merge | Merge and sort two customer lists |
| GET | `/api/customers/demo` | merge_sort_test.cbl full flow | Run complete merge/sort demo |
| POST | `/api/reports/student` | report_test.cbl | Generate formatted student report |

## Migration Mapping

### COBOL File to Java Equivalent

| COBOL Source | Java Equivalent | Notes |
|---|---|---|
| `sql/sql_example.cbl` | `AccountController`, `AccountService`, `AccountRepository` | Terminal menu replaced with REST API; embedded SQL replaced with Spring Data JPA |
| `sql/create_test_db.sql` | `V1__create_accounts.sql`, `V2__seed_data.sql` | Flyway migration files; `\c` command removed |
| `json_generate/json_generate.cbl` | `SerializationController.getJson()`, `SerializableRecord` | `JSON GENERATE` replaced with Jackson JSON |
| `xml_generate/xml_generate.cbl` | `SerializationController.getXml()`, `SerializableRecord` | `XML GENERATE` replaced with Jackson XML; attribute/suppress behavior preserved |
| `merge_sort/merge_sort_test.cbl` | `CustomerFileController`, `CustomerFileService`, `CustomerFileRecord` | File-based SORT/MERGE replaced with `Comparator` and in-memory lists |
| `report_writer/report_test.cbl` | `ReportController`, `ReportService`, `StudentRecord` | COBOL Report Writer replaced with formatted String output |
| `redifines/redefines.cbl` | `Customer` (abstract), `PersonCustomer`, `CorpCustomer` | `REDEFINES` pattern modeled with Java inheritance and polymorphism |
| `is_numeric/is_numeric.cbl` | `ValidationService.isNumeric()` | Three COBOL approaches (plain, zero-fill, trim) replicated |
| `numval_test/numval_test.cbl` | `ValidationService.parseNumericValue()` | `FUNCTION NUMVAL` replaced with `NumberUtils.isCreatable()` |
| `trim/trim.cbl` | `StringUtilService.trimVariants()` | `FUNCTION TRIM` (full/leading/trailing) mapped to `String.strip*()` |
| `unstring/unstring.cbl` | `StringUtilService.splitByDelimiter()`, `splitByMultipleDelimiters()`, `splitFormattedNumber()` | `UNSTRING` with `DELIMITER IN`, `COUNT IN`, `TALLYING IN` |
| `search/search.cbl` | `SearchDemoService` | `SEARCH ALL` (binary) -> `Collections.binarySearch()`; `SEARCH` (sequential) -> `Stream.filter()` |
| `sub_program/main_app.cbl`, `sub.cbl` | Service layer pattern (documented in `AppConfig.java`) | `CALL BY CONTENT` = immutable params; `CALL BY REFERENCE` = mutable objects |

### Terminal UI Programs (Dropped or Replaced)

| COBOL Source | Disposition | Notes |
|---|---|---|
| `accept/accept.cbl` | Replaced | Input validation concepts applied in controllers |
| `accept/accept_from.cbl` | Replaced | Environment vars -> `@Value`; dates -> `java.time`; args -> `ApplicationArguments` |
| `screen_size/get_screen_size.cbl` | Dropped | Terminal dimensions not applicable to web |
| `display_test/display-test.cbl` | Dropped | Positioned display with colors not applicable |
| `mouse/mouse_example.cbl` | Dropped | Mouse paint program not applicable |
| `display_timing/display_timing.cbl` | Dropped | Screen write benchmarking not applicable |
| `read_command_args/` | Replaced | Command-line args -> Spring Boot `ApplicationArguments` |
| `comp_test/comp_test.cbl` | Dropped | Java handles numeric types natively |

## Behavioral Differences

| Aspect | COBOL | Java (Spring Boot) |
|---|---|---|
| **String handling** | Fixed-width `PIC X(n)` fields padded with spaces | Dynamic-length `String`; no implicit padding |
| **Numeric types** | `PIC 9(n)`, `COMP-2`, `COMP-5` with explicit sizes | `int`, `long`, `double`; automatic type promotion |
| **Null handling** | No null concept; fields always have default values | Null is possible; use validation annotations |
| **Database access** | Embedded SQL with ODBC precompiler (esqlOC) | Spring Data JPA with HQL/JPQL queries |
| **File I/O** | `SELECT ... ASSIGN TO`, `READ`, `WRITE` with FD | In-memory lists; file I/O via `java.nio` if needed |
| **Memory layout** | `REDEFINES` shares memory between fields | Inheritance/polymorphism; no shared memory |
| **Search** | `SEARCH ALL` (binary) requires sorted indexed table | `Collections.binarySearch()` requires sorted list |
| **Sorting** | `SORT`/`MERGE` with sort work file (SD) | `List.sort()` with `Comparator` |
| **Report generation** | Report Writer (RD, PAGE LIMIT, GENERATE) | String formatting with pagination logic |
| **Subprograms** | `CALL BY CONTENT` / `CALL BY REFERENCE` | Method params (immutable vs mutable objects) |
| **Terminal I/O** | `ACCEPT`/`DISPLAY` with screen coordinates | REST API with JSON request/response |

## Project Structure

```
cobol-migration/
  pom.xml
  src/main/java/com/example/cobolmigration/
    CobolMigrationApplication.java
    config/
      AppConfig.java
    controller/
      AccountController.java
      CustomerFileController.java
      ReportController.java
      SerializationController.java
    model/
      Account.java               (JPA entity)
      Customer.java              (abstract base - REDEFINES)
      PersonCustomer.java        (extends Customer)
      CorpCustomer.java          (extends Customer)
      CustomerFileRecord.java    (file-based model)
      StudentRecord.java         (report model)
      SerializableRecord.java    (JSON/XML model)
    repository/
      AccountRepository.java
    service/
      AccountService.java
      CustomerFileService.java
      ReportService.java
      SearchDemoService.java
      SerializationService.java
      StringUtilService.java
      ValidationService.java
  src/main/resources/
    application.properties
    db/migration/
      V1__create_accounts.sql
      V2__seed_data.sql
  src/test/java/com/example/cobolmigration/
    controller/
      AccountControllerTest.java
    service/
      AccountServiceTest.java
      CustomerFileServiceTest.java
      ReportServiceTest.java
      SearchDemoServiceTest.java
      SerializationServiceTest.java
      StringUtilServiceTest.java
      ValidationServiceTest.java
```

## Technology Stack

- **Java 17** — minimum version
- **Spring Boot 3.2** — web framework
- **Spring Data JPA** — database access (replaces embedded SQL/ODBC)
- **PostgreSQL** — database (same as COBOL original)
- **Flyway** — database migrations
- **Jackson** — JSON/XML serialization (replaces libjson-c and libxml2)
- **Apache Commons Lang** — utility functions
- **JUnit 5 + Mockito** — testing
- **Testcontainers** — PostgreSQL integration tests
