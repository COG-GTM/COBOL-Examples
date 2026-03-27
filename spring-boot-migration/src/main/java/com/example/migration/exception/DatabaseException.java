package com.example.migration.exception;

/**
 * Custom runtime exception replacing the "check-sql-state" paragraph
 * from sql/sql_example.cbl (lines 443-472).
 *
 * In the COBOL program, check-sql-state examined SQLSTATE and SQLCODE
 * after each SQL operation. On error, it displayed the error message,
 * disconnected from the database, and terminated.
 *
 * In Spring, DataAccessException hierarchy handles most of this automatically.
 * This exception provides a domain-specific wrapper for additional context.
 */
public class DatabaseException extends RuntimeException {

    private final String sqlState;
    private final int sqlCode;

    public DatabaseException(String message) {
        super(message);
        this.sqlState = null;
        this.sqlCode = 0;
    }

    public DatabaseException(String message, String sqlState, int sqlCode) {
        super(message);
        this.sqlState = sqlState;
        this.sqlCode = sqlCode;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.sqlState = null;
        this.sqlCode = 0;
    }

    public String getSqlState() {
        return sqlState;
    }

    public int getSqlCode() {
        return sqlCode;
    }
}
