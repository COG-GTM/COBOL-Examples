# COBOL-Examples Java Spring Boot Migration

This project is a migration of [COG-GTM/COBOL-Examples](https://github.com/COG-GTM/COBOL-Examples) from GnuCOBOL to Java Spring Boot. Each COBOL program has been translated into idiomatic Java, preserving the original business logic while leveraging modern frameworks and patterns.

## Prerequisites

- **Java 17+** (JDK)
- **Maven 3.8+**
- **PostgreSQL** (for running the application) or **Docker** (for Testcontainers integration tests)

## Project Structure

```
java-migration/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/example/cobolmigration/
│   │   │   ├── Application.java              # Spring Boot entry point
│   │   │   ├── model/                         # Domain model classes
│   │   │   │   ├── Account.java               # JPA entity (sql/sql_example.cbl)
│   │   │   │   ├── Customer.java              # Abstract base (redefines/redefines.cbl)
│   │   │   │   ├── PersonCustomer.java        # Person variant (type=1)
│   │   │   │   ├── CorpCustomer.java          # Corporate variant (type=2)
│   │   │   │   ├── CustomerRecord.java        # File records (merge_sort/)
│   │   │   │   ├── StudentRecord.java         # Report records (report_writer/)
│   │   │   │   └── SerializableRecord.java    # JSON/XML (json_generate/, xml_generate/)
│   │   │   ├── repository/
│   │   │   │   └── AccountRepository.java     # JPA repository (replaces SQL cursors)
│   │   │   ├── service/
│   │   │   │   ├── StringUtilService.java     # trim, unstring, isNumeric
│   │   │   │   ├── SearchService.java         # linear & binary search
│   │   │   │   ├── NumericConversionService.java  # COMP/DISPLAY conversions
│   │   │   │   ├── AccountService.java        # Account business logic
│   │   │   │   ├── FileMergeService.java      # File merge & sort
│   │   │   │   ├── ReportService.java         # Report generation
│   │   │   │   └── SerializationService.java  # JSON & XML generation
│   │   │   └── cli/
│   │   │       ├── AccountCommands.java       # Account shell commands
│   │   │       ├── UtilityCommands.java       # Utility demo commands
│   │   │       └── SubProgramCommands.java    # Sub-program demo commands
│   │   └── resources/
│   │       ├── application.yml                # Spring Boot configuration
│   │       └── db/migration/
│   │           └── V1__create_accounts.sql    # Flyway migration
│   └── test/
│       └── java/com/example/cobolmigration/
│           ├── service/
│           │   ├── StringUtilServiceTest.java
│           │   ├── SearchServiceTest.java
│           │   ├── SerializationServiceTest.java
│           │   └── FileMergeServiceTest.java
│           └── repository/
│               └── AccountRepositoryIT.java   # Testcontainers integration test
```

## How to Build

```bash
cd java-migration
mvn clean install
```

To skip integration tests (which require Docker):

```bash
mvn clean install -DskipTests
```

To run only unit tests:

```bash
mvn test -Dtest='!*IT'
```

## How to Run

The application starts as a Spring Shell CLI:

```bash
mvn spring-boot:run
```

> **Note:** Requires a running PostgreSQL instance at `localhost:5432` with a database named `cobol_db_example`. See `application.yml` for connection settings.

## Available Shell Commands

| Command | COBOL Equivalent | Description |
|---|---|---|
| `display-all` | sql_example.cbl menu option 1 | Display all accounts |
| `display-disabled` | sql_example.cbl menu option 2 | Display disabled accounts |
| `search --term <text>` | sql_example.cbl menu option 3 | Search accounts by name/phone/address |
| `trim-demo --input <text>` | trim/trim.cbl | Demonstrate trim operations |
| `unstring-demo --input <text> --delimiter <delim>` | unstring/unstring.cbl | Split string by delimiter |
| `is-numeric --input <text>` | is_numeric/is_numeric.cbl | Check if string is numeric |
| `generate-json --name <n> --value <v> --enabled <e>` | json_generate/json_generate.cbl | Generate JSON from record |
| `generate-xml --name <n> --value <v> --enabled <e>` | xml_generate/xml_generate.cbl | Generate XML from record |
| `merge-sort-demo` | merge_sort/merge_sort_test.cbl | Run file merge and sort demo |
| `report-demo` | report_writer/report_test.cbl | Generate student report |
| `call-by-content --item1 <v> --item2 <v>` | sub_program/main_app.cbl (BY CONTENT) | Demo pass-by-value |
| `call-by-reference --item1 <v> --item2 <v>` | sub_program/main_app.cbl (BY REFERENCE) | Demo pass-by-reference |
| `cancel-sub-program` | sub_program/main_app.cbl (CANCEL) | Reset working-storage |
| `show-state` | — | Show current working-storage state |

## Migration Approach

- **COBOL REDEFINES** replaced by Java class inheritance (Customer hierarchy)
- **Embedded SQL / Cursors** replaced by Spring Data JPA repository methods
- **COBOL file I/O (FD)** replaced by `java.nio.file` operations
- **COBOL SORT/MERGE** replaced by `java.util.Collections.sort()`
- **COBOL SEARCH / SEARCH ALL** replaced by Stream filter and `Collections.binarySearch()`
- **COBOL CALL BY CONTENT/REFERENCE** demonstrated via Spring Shell commands
- **COBOL JSON/XML GENERATE** replaced by Jackson ObjectMapper and XmlMapper
- **COBOL Report Writer** replaced by String formatting
