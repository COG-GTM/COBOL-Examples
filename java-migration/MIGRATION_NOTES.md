# Migration Notes: COBOL to Java Spring Boot

This document records the key translation decisions made during the migration from GnuCOBOL to Java Spring Boot.

## Data Type Translations

### COBOL PIC Clauses to Java Types

| COBOL Type | Example | Java Type | Notes |
|---|---|---|---|
| `PIC X(n)` | `PIC X(8)` | `String` | `.trim()` applied at data boundaries to handle COBOL space-padding |
| `PIC 9(n)` | `PIC 9(5)` | `int` or `long` | Width mapped to appropriate integer type |
| `PIC 9(n) COMP` | `PIC 999 COMP` | `BigDecimal` (via `NumericUtils.fromComp`) | Binary integer storage → BigDecimal for precision |
| `PIC COMP-2` | `ws-total COMP-2` | `BigDecimal` (via `NumericUtils.fromComp2`) | Double-precision float → BigDecimal to avoid FP errors |
| `PIC COMP-3` | Packed decimal | `BigDecimal` (via `NumericUtils.fromComp3`) | BCD packed → BigDecimal with explicit scale |
| `PIC COMP-5` | `PIC S9(4) COMP-5` | `int` | Native binary → primitive int |
| `PIC X` (single char) | `ws-sql-account-is-enabled` | `Character` | Single character fields map to Character wrapper |
| `PIC X(20)` (timestamp) | `ws-sql-account-create-dt` | `LocalDateTime` | COBOL string timestamps → Java temporal types |
| `PIC ZZ9` | Dynamic display | `String` via `NumericUtils.toDynamicDisplay` | Leading zero suppression |
| 88-level conditions | `88 ws-account-enabled VALUE 'Y'` | Boolean methods or Character comparison | Condition names → explicit comparisons |

### REDEFINES → Inheritance / Conversion Methods

COBOL `REDEFINES` allows multiple interpretations of the same memory area. In Java:
- **Simple redefines**: Use type conversion methods (e.g., `Integer.parseInt(stringField)`)
- **Complex redefines**: Use separate classes with conversion constructors
- **Union-type redefines**: Use Java records or sealed classes with pattern matching

Example from `redifines/redefines.cbl`: A COMP-2 field redefining a PIC X field becomes a conversion method that parses the string representation into a BigDecimal.

## Database Layer Translations

### Cursor Fetch → JPA Queries

| COBOL Pattern | Java Equivalent |
|---|---|
| `DECLARE cursor CURSOR FOR SELECT...` | Spring Data JPA method or `@Query` annotation |
| `OPEN cursor` | Implicit (JPA manages connection lifecycle) |
| `FETCH cursor INTO :host-vars` | Return value from repository method |
| `CLOSE cursor` | Implicit (JPA manages connection lifecycle) |
| `CONNECT TO :connection-string` | `application.properties` + Spring auto-configuration |
| `CONNECT RESET` | Implicit (connection pool management) |
| `SQLSTATE` / `SQLCODE` checking | Exception handling (try/catch or `@Transactional`) |

### Specific Cursor Mappings

| COBOL Cursor | SQL | Java Method |
|---|---|---|
| `ACCOUNT-ALL-CUR` | `SELECT ... FROM ACCOUNTS ORDER BY ID` | `findAllByOrderByIdAsc()` |
| `ACCOUNT-DISABLED-CUR` | `SELECT ... WHERE IS_ENABLED = 'N' ORDER BY ID` | `findByIsEnabledOrderByIdAsc('N')` |
| `ACCOUNT-QUERY-CUR` | `SELECT ... WHERE FIRST_NAME LIKE :val OR ...` | `searchAccounts(String)` with `@Query` |

### Variable-Length Strings in SQL

COBOL uses `PIC S9(4) COMP-5` + `PIC X(50)` pair for variable-length strings in WHERE clauses (sql_example.cbl lines 65-67). In Java, JPA/JDBC handles string length automatically — no special handling needed.

## Subprogram Call Translations

### BY CONTENT vs BY REFERENCE

| COBOL Pattern | Java Equivalent | Semantics |
|---|---|---|
| `CALL "sub" USING BY CONTENT ws-var` | `callByContent(String value)` | Pass immutable copy — caller's variable unchanged |
| `CALL "sub" USING ws-var` (BY REFERENCE) | `callByReference(MutableString ref)` | Pass mutable wrapper — caller's variable can be modified |
| `CANCEL "sub-app"` | `cancel()` | Reset service instance state |

### Storage Section Mapping

| COBOL Section | Java Equivalent | Lifetime |
|---|---|---|
| WORKING-STORAGE SECTION | Instance fields | Persists between method calls until `cancel()` |
| LOCAL-STORAGE SECTION | Method-local variables | Fresh on each invocation |
| LINKAGE SECTION | Method parameters | Passed by caller |

## File Processing Translations

### SORT/MERGE → Java Collections

| COBOL Statement | Java Equivalent |
|---|---|
| `MERGE fd ON ASCENDING KEY f-id USING f1 f2 GIVING f-out` | Read both files → `ArrayList.addAll()` → `Collections.sort()` → write output |
| `SORT fd ON DESCENDING KEY f-contract-id USING f-in GIVING f-out` | Read file → `sort(Comparator.reversed())` → write output |
| `SD fd-sorting-file` (sort description) | Not needed — sorting is in-memory |
| File status checking (`ws-fs-status`) | IOException handling |

### Fixed-Width Records

COBOL FD records use fixed-width fields defined by PIC clauses. In Java:
- Parsing: `String.substring()` with calculated offsets
- Writing: `String.format()` with width specifiers
- The `CustomerRecord.fromFixedWidth()` and `toString()` methods handle this conversion

## Serialization Translations

### JSON GENERATE → Jackson

| COBOL Feature | Jackson Equivalent |
|---|---|
| `JSON GENERATE ws-output FROM ws-record` | `objectMapper.writeValueAsString(dto)` |
| `NAME OF ws-field IS "jsonName"` | `@JsonProperty("jsonName")` |
| `COUNT IN ws-count` | `json.length()` |
| Suppress blank fields | `@JsonInclude(JsonInclude.Include.NON_EMPTY)` |

### XML GENERATE → JAXB

| COBOL Feature | JAXB Equivalent |
|---|---|
| `XML GENERATE ws-output FROM ws-record` | `marshaller.marshal(dto, writer)` |
| `WITH XML-DECLARATION` | `Marshaller.JAXB_FRAGMENT = false` |
| `NAME OF ws-field IS "xmlName"` | `@XmlElement(name = "xmlName")` |
| `TYPE OF ws-field IS ATTRIBUTE` | `@XmlAttribute(name = "attrName")` |
| `SUPPRESS WHEN SPACES` | Set field to `null` in constructor when blank |
| `COUNT IN ws-count` | `xml.length()` |

## Report Writer Translation

### RD (Report Description) → ReportService

| COBOL Feature | Java Equivalent |
|---|---|
| `RD r-report PAGE LIMIT IS 66` | `PAGE_LIMIT = 66` constant |
| `HEADING IS 1` | Header generated at start of each page |
| `FIRST DETAIL 6` | Detail lines start after 5 header lines |
| `LAST DETAIL 42` | `maxDetailsPerPage = 42 - 6 + 1 = 37` |
| `TYPE REPORT HEADING` | `generatePageHeader()` method |
| `TYPE DETAIL LINE PLUS 1` | `formatDetailLine()` method |
| `SOURCE page-counter` | `pageNumber` local variable |
| `INITIATE r-report` | Constructor / method start |
| `GENERATE report-line` | Add formatted line to list |
| `TERMINATE r-report` | Return the complete list |

## String Operation Translations

| COBOL Operation | Java Equivalent | Source File |
|---|---|---|
| `FUNCTION TRIM(val)` | `String.trim()` / `StringUtils.trim()` | `trim/trim.cbl` |
| `FUNCTION TRIM(val LEADING)` | `String.stripLeading()` | `trim/trim.cbl` |
| `FUNCTION TRIM(val TRAILING)` | `String.stripTrailing()` | `trim/trim.cbl` |
| `UNSTRING src DELIMITED BY delim INTO dest1 dest2` | `StringUtils.unstring()` / `String.split()` | `unstring/unstring.cbl` |
| `val IS NUMERIC` | `StringUtils.isNumeric()` (regex) | `is_numeric/is_numeric.cbl` |
| `FUNCTION NUMVAL(val)` | `StringUtils.numval()` → `BigDecimal` | `numval_test/numval_test.cbl` |
| `ACCEPT val FROM COMMAND-LINE` | `ApplicationArguments.getSourceArgs()` | `read_command_args/` |
| `INSPECT ... TALLYING ... FOR ALL` | `String.contains()` / stream count | `read_command_args/` |
| `FUNCTION UPPER-CASE(val)` | `String.toUpperCase()` | `sql_example.cbl` |
| `FUNCTION LOWER-CASE(val)` | `String.toLowerCase()` | `read_cmd_line_args.cbl` |
| `FUNCTION STORED-CHAR-LENGTH(val)` | `String.length()` (after trim) | `sql_example.cbl` |

## Design Decisions

1. **BigDecimal over primitives**: All financial/numeric-precision fields use `BigDecimal` to prevent floating-point errors that COBOL's packed decimal types naturally avoid.

2. **`.trim()` at boundaries**: COBOL PIC X fields are right-padded with spaces. We apply `.trim()` when reading from database, files, or user input to normalize data.

3. **Spring profiles for CLI vs REST**: The `cli` profile activates the interactive Scanner-based menu. The default profile runs only the REST API, providing a modern HTTP interface.

4. **H2 for testing**: Tests use an in-memory H2 database in PostgreSQL compatibility mode, avoiding the need for a running PostgreSQL instance during CI/CD.

5. **No ORM for file processing**: The `FileMergeService` uses plain file I/O (`java.nio.file`) rather than JPA, since the COBOL SORT/MERGE operates on sequential files, not database tables.

6. **Mutable wrapper for by-reference**: Java strings are immutable, so we introduced `SubProgramService.MutableString` to faithfully model COBOL's CALL BY REFERENCE semantics where the called program can modify the caller's variables.

7. **Null for SUPPRESS WHEN SPACES**: COBOL's `SUPPRESS WHEN SPACES` in XML GENERATE is implemented by setting blank fields to `null` in the DTO constructor, which causes JAXB to omit them from output.
