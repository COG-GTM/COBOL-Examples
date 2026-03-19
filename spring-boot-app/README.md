# COBOL-to-Spring Boot Migration

This Spring Boot application is a Java migration of the COBOL example programs in this repository. It replaces the terminal-based, menu-driven COBOL programs with a REST API backed by Spring Data JPA and PostgreSQL.

The original COBOL source files remain in the repository for reference.

---

## Migration Mapping

### COBOL File → Java Class

| COBOL Source File | Java Class | Description |
|---|---|---|
| `sql/sql_example.cbl` (lines 44-52) | `model/Account.java` | WORKING-STORAGE record → JPA @Entity |
| `merge_sort/merge_sort_test.cbl` (lines 40-45) | `model/Customer.java` | File descriptor record → POJO |
| `sql/sql_example.cbl` (lines 118-153) | `repository/AccountRepository.java` | SQL CURSORs → Spring Data query methods |
| `sql/sql_example.cbl` (lines 201-394) | `service/AccountService.java` | PROCEDURE DIVISION paragraphs → service methods |
| `json_generate/json_generate.cbl` | `service/SerializationService.java` | JSON GENERATE → Jackson ObjectMapper |
| `xml_generate/xml_generate.cbl` | `service/SerializationService.java` | XML GENERATE → Jackson XmlMapper |
| `sub_program/main_app.cbl`, `sub_program/sub.cbl` | `service/SubProgramService.java` | CALL BY CONTENT/REFERENCE → Java method calls |
| `search/search.cbl` (lines 61-66) | `service/SearchService.java` | SEARCH ALL → Collections.binarySearch() |
| `sql/sql_example.cbl` (menu, lines 157-185) | `controller/AccountController.java` | ACCEPT/DISPLAY menu → REST endpoints |
| `json_generate/json_generate.cbl`, `xml_generate/xml_generate.cbl` | `controller/SerializationController.java` | Terminal output → REST endpoints |
| `json_generate/json_generate.cbl` (lines 27-33) | `dto/SerializationRecord.java` | ws-record with NAME OF → @JsonProperty DTO |
| `sql/create_test_db.sql` | `resources/schema.sql` | Database schema initialization |

### COBOL Construct → Java Construct

| COBOL Construct | Java Equivalent | Notes |
|---|---|---|
| `WORKING-STORAGE SECTION` | Instance fields / JPA @Entity | Persistent program state |
| `LOCAL-STORAGE SECTION` | Local method variables | Re-initialized on each call |
| `LINKAGE SECTION` | Method parameters | Data passed between programs |
| `PIC X(n)` | `String` (max length n) | Character data |
| `PIC 9(n)` | `int` / `Integer` | Numeric data |
| `PIC X VALUE 'Y'/'N'` | `boolean` | Flag fields |
| `EXEC SQL ... DECLARE CURSOR` | `JpaRepository` query methods | Database cursors → Spring Data |
| `EXEC SQL ... FETCH ... INTO` | JPA automatic mapping | Row-to-object mapping |
| `EXEC SQL CONNECT TO` | `application.properties` datasource | Connection configuration |
| `ACCEPT ... DISPLAY` | REST `@GetMapping` / `@PostMapping` | User I/O → HTTP API |
| `JSON GENERATE ... NAME OF` | `@JsonProperty` + `ObjectMapper` | Serialization with field renaming |
| `XML GENERATE ... NAME OF` | `@JacksonXmlProperty` + `XmlMapper` | XML serialization |
| `CALL BY CONTENT` | Pass primitives / immutable args | Callee cannot modify caller state |
| `CALL BY REFERENCE` | Pass mutable objects | Callee can modify shared state |
| `CANCEL` | Reset instance state | Clear working-storage equivalent |
| `SEARCH ALL` | `Collections.binarySearch()` | Binary search on sorted table |
| `SEARCH` (sequential) | `Stream.filter().findFirst()` | Linear search |
| `SORT ... MERGE` | `Collections.sort()` / `Comparator` | In-memory sorting |
| `PERFORM ... UNTIL` | `while` / `for` loops | Iteration |
| `EVALUATE ... WHEN` | `switch` / `if-else` | Conditional branching |
| `STRING ... INTO` | `String.format()` / `StringBuilder` | String concatenation |
| `FUNCTION TRIM` | `String.trim()` | Whitespace removal |
| `SQLSTATE` / `SQLCODE` | Exception handling | Error state → try/catch |

### SQL Cursor → Repository Method

| COBOL Cursor | Repository Method | Purpose |
|---|---|---|
| `ACCOUNT-ALL-CUR` | `findAllByOrderByIdAsc()` | All accounts, ordered by ID |
| `ACCOUNT-DISABLED-CUR` | `findByEnabledFalseOrderByIdAsc()` | Disabled accounts only |
| `ACCOUNT-QUERY-CUR` | `searchAccounts(searchValue)` | LIKE search across multiple fields |

### Menu Option → REST Endpoint

| Menu Option | HTTP Method | Endpoint | Description |
|---|---|---|---|
| 1) Display all accounts | `GET` | `/api/accounts` | List all accounts |
| 2) Display disabled accounts | `GET` | `/api/accounts/disabled` | List disabled accounts |
| 3) Query accounts | `GET` | `/api/accounts/search?q={query}` | Search accounts by keyword |
| — | `POST` | `/api/serialize/json` | Serialize record to JSON |
| — | `POST` | `/api/serialize/xml` | Serialize record to XML |

---

## Prerequisites

- **Java 17** or later
- **Maven 3.8+**
- **PostgreSQL** (for production; tests use an embedded H2 database)

## Build

```bash
cd spring-boot-app
mvn clean install
```

## Run

### With PostgreSQL (production)

Ensure PostgreSQL is running with the database configured in `application.properties`:

```
Database: cobol_db_example
User:     postgres
Password: password
Port:     5432
```

You can initialize the database with the original COBOL test data:

```bash
psql -U postgres -f ../sql/create_test_db.sql
```

Then start the application:

```bash
mvn spring-boot:run
```

### Run Tests Only

Tests use an embedded H2 database and do not require PostgreSQL:

```bash
mvn test
```

## API Usage Examples

```bash
# Get all accounts
curl http://localhost:8080/api/accounts

# Get disabled accounts
curl http://localhost:8080/api/accounts/disabled

# Search accounts
curl "http://localhost:8080/api/accounts/search?q=John"

# Serialize to JSON
curl -X POST http://localhost:8080/api/serialize/json \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Name","value":"Test Value","enabled":"true"}'

# Serialize to XML
curl -X POST http://localhost:8080/api/serialize/xml \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Name","value":"Test Value","enabled":"true"}'
```

## Project Structure

```
spring-boot-app/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/cobolmigration/
    │   │   ├── CobolMigrationApplication.java
    │   │   ├── controller/
    │   │   │   ├── AccountController.java
    │   │   │   └── SerializationController.java
    │   │   ├── dto/
    │   │   │   └── SerializationRecord.java
    │   │   ├── model/
    │   │   │   ├── Account.java
    │   │   │   └── Customer.java
    │   │   ├── repository/
    │   │   │   └── AccountRepository.java
    │   │   └── service/
    │   │       ├── AccountService.java
    │   │       ├── SearchService.java
    │   │       ├── SerializationService.java
    │   │       └── SubProgramService.java
    │   └── resources/
    │       ├── application.properties
    │       └── schema.sql
    └── test/
        ├── java/com/cobolmigration/
        │   ├── controller/
        │   │   └── AccountControllerTest.java
        │   ├── repository/
        │   │   └── AccountRepositoryTest.java
        │   └── service/
        │       ├── AccountServiceTest.java
        │       └── SearchServiceTest.java
        └── resources/
            └── application-test.properties
```
