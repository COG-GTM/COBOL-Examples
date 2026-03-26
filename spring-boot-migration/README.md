# COBOL to Spring Boot Migration

This project is a full migration of the [COBOL-Examples](https://github.com/COG-GTM/COBOL-Examples) repository from GnuCOBOL to a Java Spring Boot application. Every COBOL program has been translated into idiomatic Java, replacing terminal-based I/O with REST APIs, embedded SQL with JPA/Flyway, and fixed-width file processing with Java Streams.

## Prerequisites

- **Java 17** or later
- **Maven 3.8+**
- **PostgreSQL 12+** (for production use)

## Quick Start

### 1. Database Setup

Create the PostgreSQL database (matching the original COBOL setup):

```bash
# Create the database
createdb cobol_db_example

# Or via psql:
psql -U postgres -c "CREATE DATABASE cobol_db_example;"
```

Flyway will automatically create the `accounts` table and seed data on first run.

### 2. Configure Database Connection

Edit `src/main/resources/application.yml` to match your PostgreSQL setup:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cobol_db_example
    username: postgres
    password: password
```

### 3. Build and Run

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR directly
java -jar target/cobol-migration-1.0.0-SNAPSHOT.jar
```

The application starts on `http://localhost:8080`.

### 4. Run Tests

```bash
# Run all tests (uses H2 in-memory database)
mvn test
```

## API Endpoints

### Account Management (replaces `sql/sql_example.cbl`)

| Method | Endpoint | Description | COBOL Equivalent |
|--------|----------|-------------|------------------|
| GET | `/api/accounts` | List all accounts | Menu option 1: `display-all-accounts` |
| GET | `/api/accounts?enabled=N` | List disabled accounts | Menu option 2: `display-disabled-accounts` |
| GET | `/api/accounts/{id}` | Get account by ID | Direct SQL SELECT by ID |
| GET | `/api/accounts/search?q=term` | Search accounts | Menu option 3: `query-accounts` |
| POST | `/api/accounts` | Create new account | `add-account` paragraph |
| PUT | `/api/accounts/{id}` | Update account | `update-account` paragraph |
| DELETE | `/api/accounts/{id}` | Delete account | `delete-account` paragraph |

### Data Serialization (replaces `json_generate/` and `xml_generate/`)

| Method | Endpoint | Description | COBOL Equivalent |
|--------|----------|-------------|------------------|
| POST | `/api/serialize/json` | Serialize record to JSON | `JSON GENERATE` statement |
| POST | `/api/serialize/xml` | Serialize record to XML | `XML GENERATE` statement |

### System Information (replaces `accept/`, `display_test/`, `screen_size/`)

| Method | Endpoint | Description | COBOL Equivalent |
|--------|----------|-------------|------------------|
| GET | `/api/system/date` | Get current date (multiple formats) | `ACCEPT FROM DATE/DAY` |
| GET | `/api/system/time` | Get current time | `ACCEPT FROM TIME` |
| GET | `/api/system/env/{varName}` | Get environment variable | `ACCEPT FROM ENVIRONMENT` |
| GET | `/api/system/user` | Get user information | `ACCEPT FROM USER NAME` |

### Web Frontend

Visit `http://localhost:8080/` for the web-based account manager UI that replaces the COBOL terminal menu system.

## COBOL to Java Mapping Table

| COBOL Source File | Java Equivalent | Description |
|-------------------|-----------------|-------------|
| `sql/create_test_db.sql` | `db/migration/V1__create_accounts_table.sql` | Database schema and seed data (Flyway migration) |
| `sql/sql_example.cbl` | `entity/Account.java`, `repository/AccountRepository.java`, `service/AccountService.java`, `controller/AccountController.java` | Full SQL CRUD operations with REST API |
| `json_generate/json_generate.cbl` | `dto/RecordDto.java`, `service/SerializationService.toJson()` | JSON generation using Jackson |
| `xml_generate/xml_generate.cbl` | `dto/RecordXmlDto.java`, `service/SerializationService.toXml()` | XML generation using JAXB |
| `merge_sort/merge_sort_test.cbl` | `model/CustomerRecord.java`, `service/MergeSortService.java` | File merge and sort using Java Streams |
| `report_writer/report_test.cbl` | `model/StudentRecord.java`, `service/ReportService.java` | Report generation replicating RD layout |
| `trim/trim.cbl` | `util/StringUtils.cobolTrim()` | String trimming (leading/trailing/both) |
| `unstring/unstring.cbl` | `util/StringUtils.cobolUnstring()`, `cobolUnstringMultiple()` | String splitting with delimiter tracking |
| `is_numeric/is_numeric.cbl` | `util/StringUtils.isNumeric()` | Numeric class condition check |
| `redifines/redefines.cbl` | `util/DataRedefines.java` | Memory reinterpretation using ByteBuffer |
| `search/search.cbl` | `util/SearchUtils.linearSearch()`, `binarySearch()` | SEARCH and SEARCH ALL equivalents |
| `comp_test/comp_test.cbl` | `util/NumericUtils.java` | COMP/COMP-2/COMP-3/COMP-5 type mappings |
| `numval_test/numval_test.cbl` | `util/NumericUtils.numval()` | FUNCTION NUMVAL equivalent |
| `read_command_args/read_cmd_line_args.cbl` | `cli/CommandLineApp.java` | CLI argument parsing with Picocli |
| `sub_program/main_app.cbl` | `service/MainAppService.java` | Main program calling subprogram |
| `sub_program/sub.cbl` | `service/SubProgramService.java` | Subprogram with BY CONTENT/BY REFERENCE |
| `accept/accept_from.cbl` | `controller/SystemInfoController.java` | System date/time/env endpoints |
| `accept/accept.cbl` | `controller/SystemInfoController.java` | User input replaced by REST API |
| `accept/accept-secure.cbl` | (Spring Security - future) | Secure input replaced by auth layer |
| `display_test/display-test.cbl` | `static/index.html` | Terminal display replaced by web UI |
| `mouse/mouse_example.cbl` | `static/index.html` | Mouse input replaced by web UI |
| `screen_size/get_screen_size.cbl` | `controller/SystemInfoController.java` | Screen size N/A; system info endpoint |

## COBOL Concept Mapping

| COBOL Concept | Java Equivalent | Notes |
|---------------|-----------------|-------|
| `WORKING-STORAGE SECTION` | Class instance fields (Spring singleton) | Persists for the lifetime of the service |
| `LOCAL-STORAGE SECTION` | Method local variables | Re-initialized on each method call |
| `LINKAGE SECTION` | Method parameters | Passed from caller |
| `CALL BY CONTENT` | Pass immutable values (String, int) | Caller's data cannot be modified |
| `CALL BY REFERENCE` | Pass `MutableHolder<T>` wrapper | Caller's data can be modified |
| `CANCEL` | `SubProgramService.cancel()` | Resets working-storage to initial values |
| `EXEC SQL ... END-EXEC` | JPA Repository methods | Spring Data handles SQL generation |
| `DECLARE CURSOR` / `FETCH` | `JpaRepository.findAll()` / custom queries | Cursors replaced by JPA query methods |
| `SQLSTATE` / `SQLCODE` | `@ControllerAdvice` exception handling | SQL errors caught as Java exceptions |
| `PIC 9(n) COMP` | `int` / `long` | Binary integer |
| `PIC 9(n) COMP-2` | `double` | IEEE 754 double-precision |
| `PIC 9(n) COMP-3` | `BigDecimal` | Packed decimal |
| `PIC X(n)` | `String` | Alphanumeric field |
| `REDEFINES` | `DataRedefines` (ByteBuffer) | Same memory, different type interpretation |
| `SEARCH` (serial) | `SearchUtils.linearSearch()` | Sequential scan, no sorting required |
| `SEARCH ALL` (binary) | `SearchUtils.binarySearch()` | Binary search, requires sorted data |
| `MERGE ... ON ASCENDING KEY` | `MergeSortService.mergeFiles()` with `Comparator` | Java Streams merge and sort |
| `SORT ... ON DESCENDING KEY` | `MergeSortService.sortFile()` with `Comparator.reversed()` | Java Streams sort |
| `JSON GENERATE` | `ObjectMapper.writeValueAsString()` | Jackson JSON serialization |
| `XML GENERATE` | JAXB `Marshaller.marshal()` | JAXB XML serialization |
| `FUNCTION TRIM` | `StringUtils.cobolTrim()` | `String.strip()` variants |
| `UNSTRING ... DELIMITED BY` | `StringUtils.cobolUnstring()` | String splitting with stats |
| `IS NUMERIC` | `StringUtils.isNumeric()` | Character-by-character digit check |
| `FUNCTION NUMVAL` | `NumericUtils.numval()` | String to BigDecimal conversion |
| `ACCEPT FROM DATE/TIME` | `LocalDate.now()` / `LocalTime.now()` | Java time API |
| `ACCEPT FROM ENVIRONMENT` | `System.getenv()` | Environment variable access |
| `ACCEPT FROM COMMAND-LINE` | Picocli `@Command` / `@Option` | CLI argument parsing |
| Report Writer (`RD`) | `ReportService.generateReport()` | Formatted text with page headers |

## Behavioral Differences

1. **Character Encoding**: COBOL uses EBCDIC/ASCII fixed-width fields padded with spaces. Java uses Unicode Strings. The `CustomerRecord.toFixedWidth()` method preserves COBOL-style fixed-width formatting.

2. **Numeric Precision**: COBOL COMP-3 (packed decimal) has exact decimal arithmetic. Java's `BigDecimal` provides equivalent precision. Standard `int`/`double` types map to COMP/COMP-2.

3. **File I/O**: COBOL uses record-level file operations (`READ`, `WRITE`) with `FD` declarations. Java uses `BufferedReader`/`BufferedWriter` with line-oriented I/O. The `CustomerRecord.fromFixedWidth()` parser handles the translation.

4. **Error Handling**: COBOL checks `SQLSTATE`/`SQLCODE` after each SQL operation via the `check-sql-state` paragraph. Spring Boot uses `@ControllerAdvice` for centralized exception handling with structured JSON error responses.

5. **State Management**: COBOL `WORKING-STORAGE` persists for the program's lifetime. Spring Boot services are singletons by default, providing equivalent persistence. The `CANCEL` statement equivalent is an explicit `cancel()` method.

6. **UI**: COBOL terminal-based `ACCEPT`/`DISPLAY` with screen positioning is replaced by REST API endpoints and a web frontend. The `AT line col` positioning syntax has no direct equivalent but the web UI provides equivalent functionality.

7. **Subprogram Calls**: COBOL `CALL BY REFERENCE` modifies caller's variables directly through shared memory. Java uses `MutableHolder<T>` wrappers to simulate this behavior since Java is pass-by-value.

## Project Structure

```
spring-boot-migration/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/cobolmigration/
    │   │   ├── CobolMigrationApplication.java
    │   │   ├── cli/
    │   │   │   └── CommandLineApp.java
    │   │   ├── controller/
    │   │   │   ├── AccountController.java
    │   │   │   ├── SerializationController.java
    │   │   │   └── SystemInfoController.java
    │   │   ├── dto/
    │   │   │   ├── RecordDto.java
    │   │   │   └── RecordXmlDto.java
    │   │   ├── entity/
    │   │   │   └── Account.java
    │   │   ├── exception/
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── model/
    │   │   │   ├── CustomerRecord.java
    │   │   │   ├── MutableHolder.java
    │   │   │   └── StudentRecord.java
    │   │   ├── repository/
    │   │   │   └── AccountRepository.java
    │   │   ├── service/
    │   │   │   ├── AccountService.java
    │   │   │   ├── MainAppService.java
    │   │   │   ├── MergeSortService.java
    │   │   │   ├── ReportService.java
    │   │   │   ├── SerializationService.java
    │   │   │   └── SubProgramService.java
    │   │   └── util/
    │   │       ├── DataRedefines.java
    │   │       ├── NumericUtils.java
    │   │       ├── SearchUtils.java
    │   │       └── StringUtils.java
    │   └── resources/
    │       ├── application.yml
    │       ├── db/migration/
    │       │   └── V1__create_accounts_table.sql
    │       └── static/
    │           └── index.html
    └── test/
        ├── java/com/cobolmigration/
        │   ├── controller/
        │   │   ├── AccountControllerTest.java
        │   │   └── SystemInfoControllerTest.java
        │   ├── repository/
        │   │   └── AccountRepositoryTest.java
        │   ├── service/
        │   │   ├── AccountServiceTest.java
        │   │   ├── MergeSortServiceTest.java
        │   │   ├── ReportServiceTest.java
        │   │   ├── SerializationServiceTest.java
        │   │   └── SubProgramServiceTest.java
        │   └── util/
        │       ├── DataRedefinesTest.java
        │       ├── NumericUtilsTest.java
        │       ├── SearchUtilsTest.java
        │       └── StringUtilsTest.java
        └── resources/
            └── application.yml
```
