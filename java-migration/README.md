# COBOL to Java/Spring Boot Migration

This project is a Java/Spring Boot migration of the [COBOL-Examples](../) repository. Each COBOL program has been translated to idiomatic Java using Spring Boot 3.x, Spring Data JPA, Spring Batch, and Jackson.

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 12+ (for production; tests use H2 in-memory database)

## Build and Run

```bash
# Build the project
cd java-migration
mvn clean package

# Run tests only
mvn test

# Run the application (requires PostgreSQL)
mvn spring-boot:run
```

## Configuration

Edit `src/main/resources/application.yml` to configure the PostgreSQL connection:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cobol_db_example
    username: postgres
    password: password
```

The database schema is managed by Flyway. On first run, the migration in `src/main/resources/db/migration/V1__create_accounts_table.sql` creates the ACCOUNTS table and populates it with test data.

## COBOL to Java Mapping

| COBOL Source File | Java Equivalent | Description |
|---|---|---|
| `sql/sql_example.cbl` | `model/Account.java`, `repository/AccountRepository.java`, `service/AccountService.java` | Database operations with PostgreSQL |
| `sql/create_test_db.sql` | `db/migration/V1__create_accounts_table.sql` | Database schema (Flyway migration) |
| `json_generate/json_generate.cbl` | `model/JsonRecord.java`, `service/JsonGenerationService.java` | JSON serialization |
| `xml_generate/xml_generate.cbl` | `model/XmlRecord.java`, `service/XmlGenerationService.java` | XML serialization with declaration and attributes |
| `merge_sort/merge_sort_test.cbl` | `batch/CustomerRecord.java`, `batch/MergeSortBatchConfig.java` | File merge/sort with Spring Batch |
| `report_writer/report_test.cbl` | `service/ReportService.java` | Formatted text report generation |
| `sub_program/main_app.cbl` | `service/MainAppService.java` | Main program calling subprograms |
| `sub_program/sub.cbl` | `service/SubProgramService.java` | Subprogram with persistent state |
| `trim/trim.cbl` | `util/StringUtils.java` | String trimming (leading, trailing, both) |
| `unstring/unstring.cbl` | `util/StringUtils.java` | String splitting by delimiters |
| `is_numeric/is_numeric.cbl` | `util/NumericUtils.java` | Numeric validation |
| `numval_test/numval_test.cbl` | `util/NumericUtils.java` | String-to-number conversion (NUMVAL/NUMVAL-C) |
| `comp_test/comp_test.cbl` | `util/ComputationalTypes.java` | COMP/COMP-3/COMP-5 type conversions |
| `redifines/redefines.cbl` | `model/RedefinesExample.java` | REDEFINES via ByteBuffer/shared memory |
| `search/search.cbl` | `util/SearchUtils.java` | Linear search (SEARCH) and binary search (SEARCH ALL) |

## Behavioral Differences and Migration Notes

### Fixed-Length Strings (PIC X)
COBOL `PIC X(n)` fields are fixed-length, padded with trailing spaces. In Java, `String` is variable-length. The `StringUtils.padToFixedLength()` method provides COBOL-compatible fixed-length field behavior when needed. Most Java code uses standard `String` with `trim()` for cleaner semantics.

### Decimal Precision (COMP-3 / Packed Decimal)
COBOL `COMP-3` (packed BCD) provides exact decimal arithmetic. Java's `BigDecimal` is used as the equivalent, ensuring no floating-point precision loss. The `ComputationalTypes` class provides conversion utilities between packed decimal byte arrays and `BigDecimal`.

### Computational Types
| COBOL Type | Java Equivalent | Notes |
|---|---|---|
| `COMP` / `COMP-4` | `int` / `long` | Binary integer |
| `COMP-1` | `float` | Single-precision floating point |
| `COMP-2` | `double` | Double-precision floating point |
| `COMP-3` | `BigDecimal` | Packed decimal (BCD) - exact arithmetic |
| `COMP-5` | `int` / `long` | Native binary, full range of storage |
| `DISPLAY` (default) | `String` / `BigDecimal` | Zoned decimal |

### REDEFINES Pattern
COBOL `REDEFINES` allows the same memory area to be interpreted as different data types. In Java, this is simulated using:
- **`ByteBuffer`** for raw memory sharing between types (e.g., same bytes read as String or double)
- **Getter/setter overlay** for same-field-different-view patterns (e.g., first/last name vs. corp name)

### Subprogram State (WORKING-STORAGE vs LOCAL-STORAGE)
- COBOL `WORKING-STORAGE` persists between subprogram calls → Java singleton `@Service` with instance fields
- COBOL `LOCAL-STORAGE` is reinitialized on each call → Java local variables within methods
- COBOL `CANCEL` resets working-storage → Java `cancel()` method resets instance fields
- COBOL `CALL BY CONTENT` (immutable) → Java immutable `String` parameters
- COBOL `CALL BY REFERENCE` (mutable) → Java `MutableString` wrapper class

### SQL Cursors
COBOL embedded SQL cursors (`DECLARE CURSOR FOR ... FETCH ... INTO`) are replaced by Spring Data JPA repository methods:
- `ACCOUNT-ALL-CUR` → `findAllByOrderByIdAsc()`
- `ACCOUNT-DISABLED-CUR` → `findByIsEnabledOrderByIdAsc("N")`
- `ACCOUNT-QUERY-CUR` → `@Query` with LIKE search across multiple columns

### Report Writer
COBOL Report Writer (`RD`, `TYPE REPORT HEADING`, `TYPE DETAIL`) is replaced by a plain-text formatter in `ReportService`. The layout preserves:
- Page limit of 66 lines
- Header with title at column 44 and page counter at column 100
- Detail lines with column positions matching the COBOL report definition

### File Merge/Sort
COBOL `MERGE` and `SORT` verbs are replaced by in-memory operations in `MergeSortBatchConfig`:
- `MERGE ON ASCENDING KEY` → `mergeByCustomerId()` using two-pointer merge
- `SORT ON DESCENDING KEY` → `sortByContractIdDescending()` using `Comparator`
- Spring Batch infrastructure is available for file-based processing with `FlatFileItemReader`/`FlatFileItemWriter`

### JSON/XML Generation
- COBOL `JSON GENERATE ... NAME OF` → Jackson `@JsonProperty` annotations
- COBOL `XML GENERATE ... WITH XML-DECLARATION` → JAXB with `Marshaller.JAXB_FRAGMENT = false`
- COBOL `TYPE OF ... IS ATTRIBUTE` → JAXB `@XmlAttribute`
- COBOL `SUPPRESS WHEN SPACES` → `@JsonInclude(NON_EMPTY)` / null-checking before marshal

## Project Structure

```
java-migration/
├── pom.xml
├── README.md
├── src/main/java/com/cobolmigration/
│   ├── Application.java                    # Spring Boot entry point
│   ├── model/
│   │   ├── Account.java                    # JPA entity (sql_example.cbl)
│   │   ├── JsonRecord.java                 # JSON POJO (json_generate.cbl)
│   │   ├── XmlRecord.java                  # XML POJO (xml_generate.cbl)
│   │   └── RedefinesExample.java           # REDEFINES patterns (redefines.cbl)
│   ├── repository/
│   │   └── AccountRepository.java          # Spring Data JPA (sql_example.cbl cursors)
│   ├── service/
│   │   ├── AccountService.java             # Account operations (sql_example.cbl)
│   │   ├── JsonGenerationService.java      # JSON generation (json_generate.cbl)
│   │   ├── XmlGenerationService.java       # XML generation (xml_generate.cbl)
│   │   ├── ReportService.java              # Report writer (report_test.cbl)
│   │   ├── SubProgramService.java          # Subprogram with state (sub.cbl)
│   │   └── MainAppService.java             # Main program (main_app.cbl)
│   ├── batch/
│   │   ├── CustomerRecord.java             # Batch record model (merge_sort_test.cbl)
│   │   └── MergeSortBatchConfig.java       # Spring Batch config (merge_sort_test.cbl)
│   └── util/
│       ├── StringUtils.java                # TRIM + UNSTRING (trim.cbl, unstring.cbl)
│       ├── NumericUtils.java               # IS NUMERIC + NUMVAL (is_numeric.cbl, numval_test.cbl)
│       ├── ComputationalTypes.java         # COMP types (comp_test.cbl)
│       └── SearchUtils.java               # SEARCH / SEARCH ALL (search.cbl)
├── src/main/resources/
│   ├── application.yml                     # App configuration
│   └── db/migration/
│       └── V1__create_accounts_table.sql   # Flyway migration (create_test_db.sql)
└── src/test/java/com/cobolmigration/
    ├── service/
    │   ├── AccountServiceTest.java         # DB integration tests
    │   ├── JsonGenerationServiceTest.java  # JSON tests
    │   ├── XmlGenerationServiceTest.java   # XML tests
    │   ├── ReportServiceTest.java          # Report tests
    │   ├── SubProgramServiceTest.java      # Subprogram state tests
    │   └── MainAppServiceTest.java         # Main app demo tests
    ├── batch/
    │   └── MergeSortBatchConfigTest.java   # Merge/sort tests
    ├── model/
    │   └── RedefinesExampleTest.java       # REDEFINES pattern tests
    └── util/
        ├── StringUtilsTest.java            # String util tests
        ├── NumericUtilsTest.java           # Numeric util tests
        ├── ComputationalTypesTest.java     # COMP type tests
        └── SearchUtilsTest.java            # Search tests
```
