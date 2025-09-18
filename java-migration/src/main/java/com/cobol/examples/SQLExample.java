package com.cobol.examples;

import com.cobol.examples.database.DatabaseConnection;
import com.cobol.examples.model.AccountRecord;
import com.cobol.examples.sql.SQLTableOperations;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SQLExample {
    private SQLTableOperations sqlOps;
    private Scanner scanner;
    
    public SQLExample() {
        this.sqlOps = new SQLTableOperations();
        this.scanner = new Scanner(System.in);
    }
    
    public SQLExample(DatabaseConnection dbConnection) {
        this.sqlOps = new SQLTableOperations(dbConnection);
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        SQLExample example = new SQLExample();
        example.run();
    }
    
    public void run() {
        System.out.println();
        System.out.println("COBOL SQL DB Example Program");
        System.out.println("----------------------------");
        System.out.println();
        
        try {
            sqlOps.testConnection();
            System.out.println("Connected to database successfully.");
            
            boolean running = true;
            while (running) {
                System.out.println();
                System.out.println("1) Display all accounts");
                System.out.println("2) Display disabled accounts");
                System.out.println("3) Query accounts");
                System.out.println("4) Exit");
                System.out.print("Selection: ");
                
                String choice = scanner.nextLine().trim();
                
                switch (choice) {
                    case "1":
                        displayAllAccounts();
                        break;
                    case "2":
                        displayDisabledAccounts();
                        break;
                    case "3":
                        queryAccounts();
                        break;
                    case "4":
                        running = false;
                        break;
                    default:
                        System.out.println("Please make a selection between 1-4");
                        break;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database connection failed:");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please ensure PostgreSQL is running and the database 'cobol_db_example' exists.");
        }
        
        System.out.println("Disconnected.");
        System.out.println();
        scanner.close();
    }
    
    private void displayAllAccounts() {
        try {
            List<AccountRecord> accounts = sqlOps.getAllAccountsSorted();
            displayAccountResults(accounts);
        } catch (SQLException e) {
            handleSQLError(e);
        }
    }
    
    private void displayDisabledAccounts() {
        try {
            List<AccountRecord> accounts = sqlOps.getDisabledAccounts();
            displayAccountResults(accounts);
        } catch (SQLException e) {
            handleSQLError(e);
        }
    }
    
    private void queryAccounts() {
        boolean searchAgain = true;
        
        while (searchAgain) {
            System.out.println();
            System.out.print("Enter search value: ");
            String searchValue = scanner.nextLine();
            
            try {
                List<AccountRecord> accounts = sqlOps.queryAccountsLike(searchValue);
                displayAccountResults(accounts);
                
                System.out.println();
                System.out.print("Search again? (Y/[N]) ");
                String again = scanner.nextLine().trim().toUpperCase();
                searchAgain = "Y".equals(again);
                
            } catch (SQLException e) {
                handleSQLError(e);
                searchAgain = false;
            }
        }
    }
    
    private void displayAccountResults(List<AccountRecord> accounts) {
        System.out.println();
        System.out.println("ACCOUNTS:");
        System.out.println();
        System.out.println(" ID   | First    | Last     | Phone      | Address                | Enabled ");
        System.out.println("------|----------|----------|------------|------------------------|--------");
        
        for (AccountRecord account : accounts) {
            System.out.printf("%5d | %-8s | %-8s | %-10s | %-22s | %s%n",
                account.getId(),
                truncate(account.getFirstName(), 8),
                truncate(account.getLastName(), 8),
                truncate(account.getPhone(), 10),
                truncate(account.getAddress(), 22),
                account.getEnabledFlag());
        }
        
        if (accounts.isEmpty()) {
            System.out.println("No records found.");
        }
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength);
    }
    
    private void handleSQLError(SQLException e) {
        try {
            sqlOps.checkSQLState(e);
        } catch (SQLException ex) {
        }
    }
}
