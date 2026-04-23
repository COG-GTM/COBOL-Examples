## Java SQL Example - PostgreSQL Account Handler

This is a Java equivalent of the COBOL SQL example program (`sql/sql_example.cbl`) that demonstrates connecting to and querying a PostgreSQL database. It provides the same functionality as the COBOL version: displaying all accounts, displaying disabled accounts, and searching accounts by name, phone, or address.



**Prerequisites:**
* JDK 17 or higher
* PostgreSQL database instance (matching the COBOL example setup)
* PostgreSQL JDBC driver (`postgresql-<version>.jar`) — can be downloaded from https://jdbc.postgresql.org/download/
* The `create_test_db.sql` script must be run first to create the database and test data



**Files included:**
* `Account.java` — POJO representing a row in the `accounts` table (mirrors the COBOL `ws-account-record` structure)
* `AccountHandler.java` — JDBC connection management and query methods (mirrors the COBOL SQL cursor operations and `check-sql-state` error handling)
* `Main.java` — Interactive console menu replicating the COBOL program's menu loop



**How to compile:**
```bash
javac -cp .:postgresql-<version>.jar Account.java AccountHandler.java Main.java
```



**How to run:**
```bash
java -cp .:postgresql-<version>.jar Main
```



**Connection configuration:**

By default, the program connects to:
* Host: `localhost`
* Port: `5432`
* Database: `cobol_db_example`
* User: `postgres`
* Password: `password`

These match the COBOL program's connection string and can be modified in `AccountHandler.java`.



**Menu options:**
1. Display all accounts
2. Display disabled accounts
3. Query/search accounts (with search-again sub-loop)
4. Exit

The menu and output format match the COBOL program's behavior.
