package com.cobol.examples.sql;

import com.cobol.examples.database.DatabaseConnection;
import com.cobol.examples.model.AccountRecord;
import com.cobol.examples.util.VariableLengthString;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLTableOperations {
    private DatabaseConnection dbConnection;
    
    private static final String SELECT_ALL_ACCOUNTS = 
        "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT " +
        "FROM ACCOUNTS ORDER BY ID";
    
    private static final String SELECT_DISABLED_ACCOUNTS = 
        "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT " +
        "FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID";
    
    private static final String SELECT_ACCOUNTS_LIKE = 
        "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT " +
        "FROM ACCOUNTS WHERE " +
        "FIRST_NAME LIKE ? OR LAST_NAME LIKE ? OR PHONE LIKE ? OR ADDRESS LIKE ? " +
        "ORDER BY ID";
    
    public SQLTableOperations() {
        this.dbConnection = new DatabaseConnection();
    }
    
    public SQLTableOperations(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    public List<AccountRecord> getAllAccountsSorted() throws SQLException {
        List<AccountRecord> accounts = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ACCOUNTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }
        
        return accounts;
    }
    
    public List<AccountRecord> getDisabledAccounts() throws SQLException {
        List<AccountRecord> accounts = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_DISABLED_ACCOUNTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }
        
        return accounts;
    }
    
    public List<AccountRecord> queryAccountsLike(String searchValue) throws SQLException {
        List<AccountRecord> accounts = new ArrayList<>();
        String likePattern = VariableLengthString.prepareForLikeQuery(searchValue);
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACCOUNTS_LIKE)) {
            
            stmt.setString(1, likePattern);
            stmt.setString(2, likePattern);
            stmt.setString(3, likePattern);
            stmt.setString(4, likePattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        }
        
        return accounts;
    }
    
    private AccountRecord mapResultSetToAccount(ResultSet rs) throws SQLException {
        AccountRecord account = new AccountRecord();
        account.setId(rs.getInt("ID"));
        account.setFirstName(rs.getString("FIRST_NAME"));
        account.setLastName(rs.getString("LAST_NAME"));
        account.setPhone(rs.getString("PHONE"));
        account.setAddress(rs.getString("ADDRESS"));
        account.setEnabledFlag(rs.getString("IS_ENABLED"));
        
        Timestamp createTs = rs.getTimestamp("CREATE_DT");
        if (createTs != null) {
            account.setCreateDateTime(createTs.toLocalDateTime());
        }
        
        Timestamp modTs = rs.getTimestamp("MOD_DT");
        if (modTs != null) {
            account.setModDateTime(modTs.toLocalDateTime());
        }
        
        return account;
    }
    
    public void checkSQLState(SQLException e) throws SQLException {
        System.err.println("SQL Error:");
        System.err.println("SQLCODE: " + e.getErrorCode());
        System.err.println("SQLSTATE: " + e.getSQLState());
        System.err.println("ERROR MESSAGE: " + e.getMessage());
        throw e;
    }
    
    public void testConnection() throws SQLException {
        dbConnection.testConnection();
    }
}
