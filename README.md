# COBOL Examples 
This is a collection of example and test COBOL programs I've written. I'm currently in the process of updating 
each folder with a README.md file and more comments so that the examples are easier to follow along with.


All program were written using [GnuCOBOL](https://gnucobol.sourceforge.io/) in Linux.  

## Java/Spring Boot Migration

The COBOL programs in this repository have been migrated to a modern Java/Spring Boot application. The migrated code is located in the `java-migration/` directory.

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL (for production) or H2 (for testing, included)

### Quick Start

```bash
cd java-migration

# Build the project
mvn clean package

# Run tests only
mvn test

# Run the application (requires PostgreSQL)
mvn spring-boot:run

# Run with H2 in-memory database (for testing)
mvn spring-boot:run -Dspring.profiles.active=test
```

### Project Structure

```
java-migration/
├── pom.xml                          # Maven configuration
├── MIGRATION_REPORT.md              # Detailed migration documentation
├── MouseExampleNote.md              # Mouse handling migration notes
├── src/main/java/com/cobolmigration/
│   ├── CobolMigrationApplication.java
│   ├── model/                       # Data models (POJOs, JPA Entities)
│   ├── repository/                  # Spring Data JPA repositories
│   ├── service/                     # Business logic services
│   ├── controller/                  # REST API controllers
│   └── util/                        # Utility classes
├── src/main/resources/
│   ├── application.properties       # Configuration
│   ├── db/migration/                # Flyway database migrations
│   └── static/                      # Web frontend
└── src/test/                        # JUnit 5 tests
```

### REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/accounts` | List all accounts |
| GET | `/api/accounts/disabled` | List disabled accounts |
| GET | `/api/accounts/search?q={value}` | Search accounts |

### Migration Details

See [MIGRATION_REPORT.md](java-migration/MIGRATION_REPORT.md) for a complete mapping of every COBOL file to its Java equivalent, behavioral differences, and limitations.    



