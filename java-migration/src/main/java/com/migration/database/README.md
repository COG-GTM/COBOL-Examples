# Phase 5: Database Layer

## COBOL Sources
- `sql/sql_example.cbl` - Embedded SQL with PostgreSQL via esqlOC/ODBC
- `sql/create_test_db.sql` - Database and test data setup
- `sql/README.md` - Variable-length string handling documentation

## COBOL-to-Java Mapping

### Connection
| COBOL | Java |
|-------|------|
| `EXEC SQL CONNECT TO :ws-db-connection-string END-EXEC` | Spring auto-configuration via `application.yml` |
| `EXEC SQL CONNECT RESET END-EXEC` | Managed by connection pool (HikariCP) |
| ODBC driver configuration | JDBC driver in `pom.xml` |

### Cursors -> Repository Methods
| COBOL Cursor | Java Repository Method |
|-------------|----------------------|
| `ACCOUNT-ALL-CUR` (SELECT ... ORDER BY ID) | `findAllByOrderByIdAsc()` |
| `ACCOUNT-DISABLED-CUR` (WHERE IS_ENABLED='N') | `findByIsEnabledOrderByIdAsc("N")` |
| `ACCOUNT-QUERY-CUR` (WHERE ... LIKE :value) | `searchAccounts(wildcardValue)` |
| `OPEN cursor / FETCH / CLOSE` | Automatic via Spring Data |

### Operations
| COBOL | Java |
|-------|------|
| `EXEC SQL INSERT INTO ...` | `accountRepository.save(account)` |
| `EXEC SQL SELECT ... INTO ...` | `accountRepository.findById(id)` |
| `EXEC SQL UPDATE ...` | `accountRepository.save(account)` |
| `EXEC SQL DELETE ...` | `accountRepository.deleteById(id)` |

### Error Handling
| COBOL | Java |
|-------|------|
| `SQLSTATE` / `SQLCODE` check | Spring `DataAccessException` |
| `check-sql-state` paragraph | `@Repository` exception translation |
| `SQL-SUCCESS` / `SQL-NODATA` | Return values / `Optional.empty()` |

## Fixed-Width String Padding

The COBOL `PIC X(n)` fields are always padded to `n` characters with spaces. When these padded values are stored in the database, queries using `=` or `LIKE` may fail to match because of trailing spaces.

**COBOL solution** (from sql/README.md): Use variable-length declarations with explicit length:
```cobol
01  ws-search-value.
    05  ws-search-value-len   PIC S9(4) COMP-5.
    05  ws-search-value-text  PIC X(50).
```

**Java solution**: The `Account` entity uses `@PrePersist`/`@PreUpdate` callbacks to `strip()` all string fields before saving, eliminating the padding issue entirely.

## Database Schema

Managed by Flyway migrations in `src/main/resources/db/migration/`:
- `V1__create_accounts_table.sql` - Table DDL
- `V2__insert_test_data.sql` - Sample data matching COBOL test script
