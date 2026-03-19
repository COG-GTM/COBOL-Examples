# COBOL-to-Java Spring Boot Migration

This directory contains the Java Spring Boot equivalent of the COBOL programs in the parent repository. The migration preserves the original business logic while modernizing the technology stack.

## Technology Stack

- **Java 17+**
- **Spring Boot 3.2.x**
- **Spring Data JPA** (replaces embedded SQL / ODBC)
- **PostgreSQL** (same database as COBOL version)
- **Jackson** (replaces COBOL JSON GENERATE)
- **JAXB** (replaces COBOL XML GENERATE)
- **JUnit 5 + AssertJ** (testing)
- **H2** (in-memory database for tests)
- **Gradle** (build system)

## Project Structure

```
java-migration/
├── build.gradle
├── settings.gradle
├── README.md
├── MIGRATION_NOTES.md
├── src/
│   ├── main/
│   │   ├── java/com/cobolmigration/
│   │   │   ├── CobolMigrationApplication.java    # Spring Boot entry point
│   │   │   ├── model/
│   │   │   │   ├── Account.java                   # JPA entity (sql_example.cbl)
│   │   │   │   ├── CustomerRecord.java            # File record (merge_sort_test.cbl)
│   │   │   │   └── StudentRecord.java             # Report record (report_test.cbl)
│   │   │   ├── repository/
│   │   │   │   └── AccountRepository.java         # JPA repository (3 COBOL cursors)
│   │   │   ├── service/
│   │   │   │   ├── AccountService.java            # Account CRUD (sql_example.cbl)
│   │   │   │   ├── FileMergeService.java          # SORT/MERGE (merge_sort_test.cbl)
│   │   │   │   ├── SubProgramService.java         # CALL patterns (sub_program/)
│   │   │   │   ├── JsonService.java               # JSON gen (json_generate.cbl)
│   │   │   │   ├── XmlService.java                # XML gen (xml_generate.cbl)
│   │   │   │   └── ReportService.java             # Report Writer (report_test.cbl)
│   │   │   ├── dto/
│   │   │   │   ├── RecordDto.java                 # JSON DTO with @JsonProperty
│   │   │   │   └── RecordXmlDto.java              # XML DTO with @XmlAttribute
│   │   │   ├── util/
│   │   │   │   ├── StringUtils.java               # TRIM, UNSTRING, IS NUMERIC, NUMVAL
│   │   │   │   └── NumericUtils.java              # COMP/COMP-3 handling
│   │   │   ├── cli/
│   │   │   │   ├── AccountCli.java                # Interactive menu (sql_example.cbl)
│   │   │   │   └── CommandLineArgsRunner.java     # CLI args (read_cmd_line_args.cbl)
│   │   │   └── controller/
│   │   │       └── AccountController.java         # REST API alternative
│   │   └── resources/
│   │       ├── application.properties             # DB config
│   │       └── db/migration/
│   │           └── V1__create_accounts_table.sql  # Schema + seed data
│   └── test/
│       ├── java/com/cobolmigration/
│       │   ├── repository/AccountRepositoryTest.java
│       │   ├── service/
│       │   │   ├── AccountServiceTest.java
│       │   │   ├── FileMergeServiceTest.java
│       │   │   ├── SubProgramServiceTest.java
│       │   │   ├── JsonServiceTest.java
│       │   │   ├── XmlServiceTest.java
│       │   │   └── ReportServiceTest.java
│       │   ├── util/
│       │   │   ├── StringUtilsTest.java
│       │   │   └── NumericUtilsTest.java
│       │   └── controller/AccountControllerTest.java
│       └── resources/
│           └── application-test.properties
```

## COBOL Module to Java Mapping

| COBOL Module | COBOL File(s) | Java Equivalent | Description |
|---|---|---|---|
| SQL Database Access | `sql/sql_example.cbl` | `Account.java`, `AccountRepository.java`, `AccountService.java` | PostgreSQL CRUD with cursors → JPA + Spring Data |
| SQL Schema | `sql/create_test_db.sql` | `V1__create_accounts_table.sql` | Database schema and seed data |
| JSON Generation | `json_generate/json_generate.cbl` | `RecordDto.java`, `JsonService.java` | JSON GENERATE → Jackson ObjectMapper |
| XML Generation | `xml_generate/xml_generate.cbl` | `RecordXmlDto.java`, `XmlService.java` | XML GENERATE → JAXB Marshaller |
| Sort/Merge | `merge_sort/merge_sort_test.cbl` | `CustomerRecord.java`, `FileMergeService.java` | SORT/MERGE → Collections.sort + Streams |
| Report Writer | `report_writer/report_test.cbl` | `StudentRecord.java`, `ReportService.java` | RD report → String.format() |
| Subprogram Calls | `sub_program/main_app.cbl`, `sub_program/sub.cbl` | `SubProgramService.java` | CALL BY CONTENT/REFERENCE → method params |
| String Trim | `trim/trim.cbl` | `StringUtils.trim/trimLeading/trimTrailing` | FUNCTION TRIM → String.strip* |
| Unstring | `unstring/unstring.cbl` | `StringUtils.unstring()` | UNSTRING → String.split with regex |
| Is Numeric | `is_numeric/is_numeric.cbl` | `StringUtils.isNumeric()` | IS NUMERIC → regex pattern |
| Numval | `numval_test/numval_test.cbl` | `StringUtils.numval()` | FUNCTION NUMVAL → BigDecimal parsing |
| COMP Types | `comp_test/comp_test.cbl` | `NumericUtils` | COMP/COMP-2/COMP-3 → BigDecimal |
| Command Args | `read_command_args/read_cmd_line_args.cbl` | `CommandLineArgsRunner.java` | ACCEPT FROM COMMAND-LINE → ApplicationArguments |
| Terminal Menu | `sql/sql_example.cbl` (menu loop) | `AccountCli.java` | ACCEPT/DISPLAY menu → Scanner-based CLI |
| REST API | N/A (new) | `AccountController.java` | Modern HTTP alternative to terminal UI |
| Accept Input | `accept/accept.cbl`, `accept/accept_from.cbl` | Covered by CLI + Spring Boot args | ACCEPT → Scanner / ApplicationArguments |
| Secure Accept | `accept/accept-secure.cbl` | Covered by CLI (Console.readPassword) | ACCEPT SECURE → Console.readPassword |
| Display | `display_test/display_test.cbl` | Standard System.out.println | DISPLAY → println |
| Display Timing | `display_timing/display_timing.cbl` | System.nanoTime() | ACCEPT FROM TIME → Instant.now() |
| Screen Size | `screen_size/get_screen_size.cbl` | N/A (terminal-specific) | CBL_GET_SCR_SIZE → not applicable in web context |
| Mouse Input | `mouse/mouse.cbl` | N/A (terminal-specific) | Mouse drawing → not applicable |
| Search/Search All | `search/search.cbl` | Java Collections / binary search | SEARCH/SEARCH ALL → List operations |
| Redefines | `redifines/redefines.cbl` | Java inheritance / conversion methods | REDEFINES → type conversion |

## Running the Application

### Prerequisites
- Java 17+
- PostgreSQL with `cobol_db_example` database (see `sql/create_test_db.sql`)

### Build
```bash
cd java-migration
./gradlew build
```

### Run (REST API mode)
```bash
./gradlew bootRun
```
The REST API will be available at `http://localhost:8080/accounts`.

### Run (CLI mode)
```bash
./gradlew bootRun --args='--spring.profiles.active=cli'
```

### Run Tests
```bash
./gradlew test
```

## REST API Endpoints

| Method | Endpoint | Description | COBOL Equivalent |
|---|---|---|---|
| GET | `/accounts` | List all accounts | display-all-accounts |
| GET | `/accounts/disabled` | List disabled accounts | display-disabled-accounts |
| GET | `/accounts/search?q={term}` | Search accounts | query-accounts |
| GET | `/accounts/{id}` | Get account by ID | N/A |
| POST | `/accounts` | Create new account | N/A (extended) |
| PUT | `/accounts/{id}` | Update account | N/A (extended) |
| DELETE | `/accounts/{id}` | Delete account | N/A (extended) |
| PUT | `/accounts/{id}/toggle` | Toggle enabled status | N/A (extended) |
