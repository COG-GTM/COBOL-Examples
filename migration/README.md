# COBOL to Java/Spring Boot Migration

This directory contains the Java/Spring Boot migration of the COBOL example programs from the [COBOL-Examples](../) repository.

## Migration Plan Phases

| Phase | Description | Status |
|-------|-------------|--------|
| Phase 0 | Project setup and build configuration | Complete |
| Phase 1a | Data models (Account, Customer, Item) | Complete |
| Phase 1b | String & numeric utilities (StringUtils, NumericUtils) | Complete |
| Phase 1c | Search service (SearchService) | Complete |
| Phase 2 | Database layer (AccountRepository with JPA) | Complete |
| Phase 3 | Serialization (JsonService, XmlService) | Complete |
| Phase 4 | File I/O and report generation | Planned |
| Phase 5 | REST controller (AccountController) | Complete |
| Phase 6 | Integration testing and end-to-end validation | Planned |

## COBOL to Java Mapping

| COBOL Source File | Java Equivalent | Description |
|-------------------|-----------------|-------------|
| `sql/sql_example.cbl` (lines 44-52) | `model/Account.java` | Account data model with JPA annotations |
| `sql/sql_example.cbl` (lines 86-87) | `model/Account.java` (88-level conditions) | Enabled/disabled flag as boolean |
| `sql/sql_example.cbl` (lines 119-153) | `repository/AccountRepository.java` | SQL cursor declarations as JPA queries |
| `sql/sql_example.cbl` (lines 158-185) | `controller/AccountController.java` | Menu-driven UI as REST endpoints |
| `redifines/redefines.cbl` (lines 17-31) | `model/Customer.java` | REDEFINES as polymorphic name resolution |
| `search/search.cbl` (lines 17-33) | `model/Item.java` | Item table with Comparable for sort keys |
| `search/search.cbl` (lines 61-66, 99-109) | `service/SearchService.java` | SEARCH ALL and SEARCH as binary/linear search |
| `trim/trim.cbl` | `util/StringUtils.java` (trim) | FUNCTION TRIM with LEADING/TRAILING modes |
| `unstring/unstring.cbl` | `util/StringUtils.java` (unstring) | UNSTRING with POINTER and TALLYING |
| `is_numeric/is_numeric.cbl` | `util/NumericUtils.java` (isNumeric) | IS NUMERIC test |
| `numval_test/numval_test.cbl` | `util/NumericUtils.java` (numval) | FUNCTION NUMVAL |
| `json_generate/json_generate.cbl` | `serialization/JsonService.java` | JSON GENERATE with NAME OF |
| `xml_generate/xml_generate.cbl` | `serialization/XmlService.java` | XML GENERATE with ATTRIBUTE and SUPPRESS |

## Project Structure

```
migration/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/cobolmigration/
│   │   │   ├── Application.java
│   │   │   ├── model/
│   │   │   │   ├── Account.java
│   │   │   │   ├── Customer.java
│   │   │   │   └── Item.java
│   │   │   ├── util/
│   │   │   │   ├── StringUtils.java
│   │   │   │   └── NumericUtils.java
│   │   │   ├── service/
│   │   │   │   └── SearchService.java
│   │   │   ├── repository/
│   │   │   │   └── AccountRepository.java
│   │   │   ├── serialization/
│   │   │   │   ├── JsonService.java
│   │   │   │   └── XmlService.java
│   │   │   └── controller/
│   │   │       └── AccountController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/cobolmigration/
│           ├── model/
│           │   ├── AccountTest.java
│           │   ├── CustomerTest.java
│           │   └── ItemTest.java
│           ├── util/
│           │   ├── StringUtilsTest.java
│           │   └── NumericUtilsTest.java
│           └── service/
│               └── SearchServiceTest.java
```

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL (for running the full application with database)

## Build and Run

### Build the project

```bash
cd migration
mvn clean compile
```

### Run tests

```bash
cd migration
mvn test
```

### Run the application

The application requires a PostgreSQL database. Set up the database using the SQL script:

```bash
psql -U postgres -f ../sql/create_test_db.sql
```

Then run the Spring Boot application:

```bash
cd migration
mvn spring-boot:run
```

Or with custom database credentials:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--DB_USER=myuser --DB_PASSWORD=mypass"
```

### Database Setup

The application connects to a PostgreSQL database. The schema is defined in [`../sql/create_test_db.sql`](../sql/create_test_db.sql).

**Database configuration** is in `src/main/resources/application.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/cobol_db_example` | Database URL |
| `spring.datasource.username` | `${DB_USER:postgres}` | Database username (env var or default) |
| `spring.datasource.password` | `${DB_PASSWORD:password}` | Database password (env var or default) |

## REST API Endpoints

These endpoints replace the COBOL menu-driven interface from `sql/sql_example.cbl`:

| Endpoint | Method | COBOL Equivalent | Description |
|----------|--------|------------------|-------------|
| `/api/accounts` | GET | Menu option 1 ("Display all accounts") | List all accounts ordered by ID |
| `/api/accounts/disabled` | GET | Menu option 2 ("Display disabled accounts") | List disabled accounts |
| `/api/accounts/search?q=term` | GET | Menu option 3 ("Query accounts") | Search accounts by term |

## Key Migration Decisions

### REDEFINES -> Polymorphism
COBOL `REDEFINES` (shared memory) is modeled as separate fields with a `getDisplayName()` method that returns the appropriate name based on `CustomerType`.

### 88-Level Conditions -> Enums/Booleans
COBOL 88-level conditions are mapped to Java enums (`CustomerType`) or boolean fields with helper methods (`getEnabledFlag()`).

### SEARCH ALL -> Collections.binarySearch()
COBOL `SEARCH ALL` (binary search on indexed tables) uses `Collections.binarySearch()` with `Comparable<Item>`.

### Cursors -> JPA Repository Methods
COBOL cursor declarations are replaced with Spring Data JPA repository method naming conventions and `@Query` annotations.

### JSON/XML GENERATE -> Jackson/JAXB
COBOL `JSON GENERATE` and `XML GENERATE` statements are replaced with Jackson `ObjectMapper` and JAXB `Marshaller` respectively, using annotations for field name mapping.
