# Java Migration of COBOL Table Search and SQL Operations

This project provides a Java implementation of the table search and SQL functionality originally implemented in COBOL. It maintains the same performance characteristics and functionality as the original COBOL code.

## Features Migrated

### 1. Binary Search (SEARCH ALL equivalent)
- **Class**: `BinarySearchTable`
- **Original COBOL**: `search/search.cbl` lines 60-66
- **Functionality**: 
  - Requires sorted data with ascending/descending key definitions
  - Supports multiple key combinations for searching
  - Uses Java's `Collections.binarySearch()` for efficient O(log n) performance

### 2. Sequential Search (SEARCH equivalent)
- **Class**: `SequentialSearchTable`
- **Original COBOL**: `search/search.cbl` lines 99-109
- **Functionality**:
  - Works on unkeyed tables without requiring sorted data
  - Linear search through table data O(n) performance
  - Supports search by ID and value matching

### 3. SQL Table Operations
- **Class**: `SQLTableOperations`
- **Original COBOL**: `sql/sql_example.cbl` and `sql/generated_sql_ex.cbl`
- **Three Search Patterns**:
  1. **All records with sorting**: `SELECT ... ORDER BY ID`
  2. **Filtered search**: `SELECT ... WHERE IS_ENABLED = 'N' ORDER BY ID`
  3. **Dynamic search**: `SELECT ... WHERE ... LIKE ? ... ORDER BY ID`

### 4. Variable-Length String Handling
- **Class**: `VariableLengthString`
- **Original COBOL**: `sql/sql_example.cbl` lines 65-67
- **Functionality**:
  - Proper trimming for SQL LIKE operations
  - Length calculation to avoid trailing space issues
  - Wildcard character handling for flexible searching

## Project Structure

```
java-migration/
├── src/main/java/com/cobol/examples/
│   ├── model/                    # Data model classes
│   │   ├── TableItem.java        # Represents COBOL table records
│   │   ├── NoKeyItem.java        # Represents unkeyed table records
│   │   └── AccountRecord.java    # Represents SQL account records
│   ├── search/                   # Search implementations
│   │   ├── BinarySearchTable.java
│   │   └── SequentialSearchTable.java
│   ├── sql/                      # SQL operations
│   │   └── SQLTableOperations.java
│   ├── database/                 # Database utilities
│   │   └── DatabaseConnection.java
│   ├── util/                     # Utility classes
│   │   └── VariableLengthString.java
│   ├── SearchExample.java        # Runnable search example
│   └── SQLExample.java           # Runnable SQL example
├── src/main/resources/
│   └── schema.sql                # Database schema and sample data
└── src/test/java/                # Unit tests
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- PostgreSQL database (for SQL examples)

## Database Setup

1. Install PostgreSQL and create the database:
```sql
CREATE DATABASE cobol_db_example;
```

2. Run the schema script:
```bash
psql -d cobol_db_example -f src/main/resources/schema.sql
```

3. Update database connection settings in `DatabaseConnection.java` if needed.

## Building and Running

### Compile the project:
```bash
mvn clean compile
```

### Run tests:
```bash
mvn test
```

### Run the search example:
```bash
mvn exec:java -Dexec.mainClass="com.cobol.examples.SearchExample"
```

### Run the SQL example:
```bash
mvn exec:java -Dexec.mainClass="com.cobol.examples.SQLExample"
```

## Usage Examples

### Binary Search Example
```java
BinarySearchTable table = new BinarySearchTable();
table.addItem(new TableItem(1, 101, 500, "test item 1", LocalDate.of(2021, 1, 1)));

// Search by single key
TableItem result = table.searchByItemId1(1);

// Search by multiple keys
TableItem result = table.searchByAllKeys(1, 101, 500);
```

### Sequential Search Example
```java
SequentialSearchTable table = new SequentialSearchTable();
table.addItem(new NoKeyItem(2, "Value of id 2."));

// Search by ID
NoKeyItem result = table.searchById(2);
```

### SQL Operations Example
```java
SQLTableOperations sqlOps = new SQLTableOperations();

// Get all accounts sorted by ID
List<AccountRecord> allAccounts = sqlOps.getAllAccountsSorted();

// Get disabled accounts only
List<AccountRecord> disabledAccounts = sqlOps.getDisabledAccounts();

// Search with LIKE pattern
List<AccountRecord> searchResults = sqlOps.queryAccountsLike("John");
```

## Performance Characteristics

- **Binary Search**: O(log n) - Same as COBOL SEARCH ALL
- **Sequential Search**: O(n) - Same as COBOL SEARCH
- **SQL Operations**: Database-dependent - Uses proper indexing and prepared statements

## Key Java Patterns Used

- **Interfaces**: For search operations to allow different implementations
- **Generics**: For type-safe collections and search results
- **Builder Pattern**: For complex search criteria
- **Resource Management**: Try-with-resources for database connections
- **Proper Exception Handling**: SQLException handling equivalent to COBOL's check-sql-state

## Testing

The project includes comprehensive unit tests that verify:
- Binary search functionality with sample data matching COBOL setup
- Sequential search operations
- Variable-length string handling in LIKE operations
- All search patterns return expected results

Run tests with: `mvn test`

## Original COBOL Reference

This Java implementation is based on the following COBOL files:
- `search/search.cbl` - Binary and sequential search examples
- `sql/sql_example.cbl` - SQL operations with three search patterns
- `sql/generated_sql_ex.cbl` - Generated SQL code from esqlOC precompiler

The Java code maintains functional equivalence while following modern Java best practices and design patterns.
