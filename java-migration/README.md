# COBOL to Java/Spring Boot Migration

This project migrates all COBOL example programs from the [COBOL-Examples](../) repository into Java/Spring Boot equivalents, organized into 8 phases.

## Technology Stack

- **Java 17+**
- **Spring Boot 3.2.x**
- **Maven** for build management
- **PostgreSQL** (matching the existing COBOL database)
- **H2** for testing
- **Jackson** for JSON/XML serialization
- **Spring Batch** for file processing
- **JUnit 5** for testing

## Project Structure

```
java-migration/
├── pom.xml
├── src/main/java/com/migration/
│   ├── MigrationApplication.java
│   ├── stringutils/        (Phase 1)
│   ├── datastructures/     (Phase 2)
│   ├── serialization/      (Phase 3)
│   ├── batch/              (Phase 4)
│   ├── database/           (Phase 5)
│   ├── services/           (Phase 6)
│   ├── web/                (Phase 7)
│   └── reports/            (Phase 8)
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/       (Flyway scripts)
└── src/test/java/com/migration/
    ├── stringutils/StringUtilsTest.java
    ├── datastructures/DataStructuresTest.java
    ├── serialization/SerializationTest.java
    ├── batch/FileMergeTest.java
    ├── database/AccountRepositoryTest.java
    ├── services/SubAppServiceTest.java
    ├── web/AccountControllerTest.java
    └── reports/ReportServiceTest.java
```

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL (for production; tests use H2)

### Build and Test
```bash
cd java-migration
mvn clean test
```

### Run
```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

### REST API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/accounts` | List all accounts |
| GET | `/api/accounts?search=term` | Search accounts |
| GET | `/api/accounts?enabled=N` | List disabled accounts |
| GET | `/api/accounts/{id}` | Get account by ID |
| POST | `/api/accounts` | Create account |
| PUT | `/api/accounts/{id}` | Update account |
| DELETE | `/api/accounts/{id}` | Delete account |
| GET | `/api/customers` | List customer records |
| GET | `/api/customers/{id}` | Search customer by ID |
| POST | `/api/customers/person` | Create person customer |
| POST | `/api/customers/corporate` | Create corporate customer |
| GET | `/api/reports/customers` | Generate account report (text) |
| GET | `/api/reports/customers?format=csv` | Generate account report (CSV) |

## Migration Phases

### Phase 1: String Utilities
**COBOL sources:** `trim/trim.cbl`, `unstring/unstring.cbl`, `is_numeric/is_numeric.cbl`, `numval_test/numval_test.cbl`

Migrates COBOL intrinsic string functions to Java equivalents. See [Phase 1 README](src/main/java/com/migration/stringutils/README.md).

### Phase 2: Data Structures
**COBOL sources:** `redifines/redefines.cbl`, `search/search.cbl`, `comp_test/comp_test.cbl`

Migrates COBOL REDEFINES to sealed interfaces, SEARCH to Collections, and COMP types. See [Phase 2 README](src/main/java/com/migration/datastructures/README.md).

### Phase 3: Serialization
**COBOL sources:** `json_generate/json_generate.cbl`, `xml_generate/xml_generate.cbl`

Migrates COBOL JSON/XML GENERATE to Jackson. See [Phase 3 README](src/main/java/com/migration/serialization/README.md).

### Phase 4: File Processing
**COBOL sources:** `merge_sort/merge_sort_test.cbl`

Migrates COBOL MERGE/SORT to Java streams and Spring Batch. See [Phase 4 README](src/main/java/com/migration/batch/README.md).

### Phase 5: Database Layer
**COBOL sources:** `sql/sql_example.cbl`

Migrates embedded SQL to Spring Data JPA. See [Phase 5 README](src/main/java/com/migration/database/README.md).

### Phase 6: Subprogram Architecture
**COBOL sources:** `sub_program/main_app.cbl`, `sub_program/sub.cbl`

Migrates CALL/CANCEL patterns to Spring services. See [Phase 6 README](src/main/java/com/migration/services/README.md).

### Phase 7: UI Layer
**COBOL sources:** `accept/accept.cbl`, `screen_size/screen_size.cbl`, `display_test/display_test.cbl`, `mouse/mouse_example.cbl`

Replaces terminal UI with REST API. See [Phase 7 README](src/main/java/com/migration/web/README.md).

### Phase 8: Report Generation
**COBOL sources:** `report_writer/report_test.cbl`

Migrates Report Writer to Java report service. See [Phase 8 README](src/main/java/com/migration/reports/README.md).

## Database Configuration

The application connects to the same PostgreSQL database used by the COBOL SQL example:
- Host: `localhost:5432`
- Database: `cobol_db_example`
- User: `postgres`

Configure in `src/main/resources/application.yml`.
