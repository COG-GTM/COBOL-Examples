package com.cobolmigration.exception;

/**
 * Custom exception replacing the check-sql-state paragraph in sql_example.cbl (lines 443-472).
 * Thrown when database operations encounter errors.
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
