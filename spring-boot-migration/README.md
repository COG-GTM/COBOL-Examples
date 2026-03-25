# COBOL to Spring Boot Migration

This project is a Java Spring Boot application that migrates the functionality of the [COBOL-Examples](../) repository. Each COBOL program has been translated into equivalent Java classes following modern Spring Boot patterns and best practices.

## Prerequisites

- Java 17 or later
- Maven 3.8+
- PostgreSQL 12+ (for production)

## Building the Application

```bash
cd spring-boot-migration
mvn clean package
```

To skip tests:

```bash
mvn clean package -DskipTests
```

## Running the Application

### 1. Database Setup

Create the PostgreSQL database using the original SQL script:

```bash
psql -U postgres -f ../sql/create_test_db.sql
```

Or manually create the database and run the schema:

```bash
psql -U postgres -c "CREATE DATABASE cobol_db_example;"
psql -U postgres -d cobol_db_example -f src/main/resources/schema.sql
psql -U postgres -d cobol_db_example -f src/main/resources/data.sql
```

### 2. Configure Database Connection

Edit `src/main/resources/application.yml` to match your PostgreSQL setup:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cobol_db_example
    username: postgres
    password: password
```

### 3. Start the Application

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

## Running Tests

```bash
mvn test
```

Integration tests use H2 in PostgreSQL compatibility mode, so no external database is needed for testing.

## API Endpoints

### Accounts (from `sql/sql_example.cbl`)

| Endpoint | Description | COBOL Equivalent |
|----------|-------------|-----------------|
| `GET /api/accounts` | List all accounts | Menu option 1 (display-all-accounts) |
| `GET /api/accounts?enabled=false` | List disabled accounts | Menu option 2 (display-disabled-accounts) |
| `GET /api/accounts?search={term}` | Search accounts | Menu option 3 (query-accounts) |

### Reports (from `report_writer/report_test.cbl`)

| Endpoint | Description | COBOL Equivalent |
|----------|-------------|-----------------|
| `GET /api/reports/customers` | Customer report (HTML) | Report Writer output |
| `GET /api/reports/customers?format=text` | Customer report (text) | Report Writer output |

### Serialization (from `xml_generate/`, `json_generate/`)

| Endpoint | Description | COBOL Equivalent |
|----------|-------------|-----------------|
| `GET /api/serialize/xml` | XML generation demo | `xml_generate/xml_generate.cbl` |
| `GET /api/serialize/json` | JSON generation demo | `json_generate/json_generate.cbl` |

## COBOL to Java Mapping Reference

### Phase 1: Data Layer (`sql/`)

| COBOL Source | Java Class | Description |
|-------------|-----------|-------------|
| `sql/create_test_db.sql` (lines 8-18) | `entity/Account.java` | Table → JPA Entity |
| `sql/sql_example.cbl` (lines 44-52) | `entity/Account.java` | `ws-sql-account-record` → Account fields |
| `sql/sql_example.cbl` (lines 119-125) | `repository/AccountRepository.java` | CURSOR declarations → JPA queries |
| `sql/sql_example.cbl` (lines 65-67) | `repository/AccountRepository.java` | `ws-search-value` → `@Query` with JPQL |
| `sql/sql_example.cbl` (lines 443-472) | `exception/DatabaseException.java` | `check-sql-state` → Exception handling |
| `sql/sql_example.cbl` (lines 158-185) | `controller/AccountController.java` | Menu options → REST endpoints |

### Phase 2: Business Logic

| COBOL Source | Java Class | Description |
|-------------|-----------|-------------|
| `search/search.cbl` (lines 17-33) | `model/SearchItem.java` | `ws-item-table` → SearchItem model |
| `search/search.cbl` (lines 61-66) | `service/SearchService.java` | `SEARCH ALL` → `Collections.binarySearch()` |
| `merge_sort/merge_sort_test.cbl` (lines 47-53) | `model/CustomerRecord.java` | File record → CustomerRecord model |
| `merge_sort/merge_sort_test.cbl` (lines 107-110) | `service/FileSortService.java` | `MERGE` → `mergeFiles()` |
| `merge_sort/merge_sort_test.cbl` (lines 142-145) | `service/FileSortService.java` | `SORT` → `sortByContractIdDesc()` |
| `trim/trim.cbl` | `util/StringUtils.java` | `FUNCTION TRIM` → `trim()` |
| `unstring/unstring.cbl` | `util/StringUtils.java` | `UNSTRING` → `unstring()` |
| `is_numeric/is_numeric.cbl` | `util/StringUtils.java` | `IS NUMERIC` → `isNumeric()` |
| `numval_test/numval_test.cbl` | `util/StringUtils.java` | `FUNCTION NUMVAL` → `numericValue()` |
| `report_writer/report_test.cbl` (lines 41-63) | `service/ReportService.java` | Report Writer → HTML/Text report |

### Phase 3: Serialization

| COBOL Source | Java Class | Description |
|-------------|-----------|-------------|
| `xml_generate/xml_generate.cbl` (lines 41-56) | `service/XmlSerializationService.java` | `XML GENERATE` → Jackson XmlMapper |
| `json_generate/json_generate.cbl` (lines 42-54) | `service/JsonSerializationService.java` | `JSON GENERATE` → Jackson ObjectMapper |
| Both | `model/SerializationRecord.java` | `ws-record` → annotated model |

### Phase 4: Architecture

| COBOL Source | Java Class | Description |
|-------------|-----------|-------------|
| `sub_program/main_app.cbl` (lines 34-38) | `service/SubProgramService.java` | `CALL BY CONTENT` → `callByContent()` |
| `sub_program/main_app.cbl` (lines 47-49) | `service/SubProgramService.java` | `CALL BY REFERENCE` → `callByReference()` |
| `sub_program/main_app.cbl` (lines 54-55) | `service/SubProgramService.java` | `CANCEL` → `reset()` |
| `sub_program/sub.cbl` WORKING-STORAGE | `service/SubProgramService.java` | Singleton bean fields |
| `sub_program/sub.cbl` LOCAL-STORAGE | `service/SubProgramService.java` | Method-local variables |
| `sub_program/sub.cbl` LINKAGE SECTION | `service/SubProgramService.java` | Method parameters |

### Phase 5: Testing

| Test Class | What It Tests |
|-----------|--------------|
| `AccountServiceTest.java` | Account queries with mock repository |
| `SearchServiceTest.java` | Binary search with exact COBOL test data |
| `FileSortServiceTest.java` | Merge and sort ordering matches COBOL |
| `StringUtilsTest.java` | Trim, unstring, isNumeric, numval edge cases |
| `XmlSerializationServiceTest.java` | XML output matches COBOL XML GENERATE |
| `JsonSerializationServiceTest.java` | JSON output matches COBOL JSON GENERATE |
| `AccountControllerIntegrationTest.java` | REST endpoints with H2 (PostgreSQL mode) |

## Project Structure

```
spring-boot-migration/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/cobolmigration/
    │   │   ├── CobolMigrationApplication.java
    │   │   ├── controller/
    │   │   │   ├── AccountController.java
    │   │   │   ├── ReportController.java
    │   │   │   └── SerializationController.java
    │   │   ├── entity/
    │   │   │   └── Account.java
    │   │   ├── exception/
    │   │   │   ├── DatabaseException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── ResourceNotFoundException.java
    │   │   ├── model/
    │   │   │   ├── CustomerRecord.java
    │   │   │   ├── ReportRecord.java
    │   │   │   ├── SearchItem.java
    │   │   │   └── SerializationRecord.java
    │   │   ├── repository/
    │   │   │   └── AccountRepository.java
    │   │   ├── service/
    │   │   │   ├── AccountService.java
    │   │   │   ├── FileSortService.java
    │   │   │   ├── JsonSerializationService.java
    │   │   │   ├── ReportService.java
    │   │   │   ├── SearchService.java
    │   │   │   ├── SubProgramService.java
    │   │   │   └── XmlSerializationService.java
    │   │   └── util/
    │   │       └── StringUtils.java
    │   └── resources/
    │       ├── application.yml
    │       ├── data.sql
    │       └── schema.sql
    └── test/
        ├── java/com/cobolmigration/
        │   ├── controller/
        │   │   └── AccountControllerIntegrationTest.java
        │   ├── service/
        │   │   ├── AccountServiceTest.java
        │   │   ├── FileSortServiceTest.java
        │   │   ├── JsonSerializationServiceTest.java
        │   │   ├── SearchServiceTest.java
        │   │   └── XmlSerializationServiceTest.java
        │   └── util/
        │       └── StringUtilsTest.java
        └── resources/
            └── application-test.yml
```
