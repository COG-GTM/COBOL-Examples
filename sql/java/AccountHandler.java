import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages PostgreSQL JDBC connection and provides query methods
 * for the accounts table. Mirrors the COBOL program's SQL operations.
 */
public class AccountHandler {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/cobol_db_example";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "password";

    private Connection connection;

    /**
     * Establishes a JDBC connection to the PostgreSQL database.
     * On failure, prints SQL error details and exits.
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            System.out.println("SQL Error:");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Closes the database connection if it is open.
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error:");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getMessage());
        }
    }

    /**
     * Returns all accounts ordered by ID.
     */
    public List<Account> getAllAccounts() {
        String sql = "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT " +
                     "FROM ACCOUNTS ORDER BY ID";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            System.out.println("SQL Error:");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getMessage());
        }
        return accounts;
    }

    /**
     * Returns all disabled accounts (IS_ENABLED = 'N') ordered by ID.
     */
    public List<Account> getDisabledAccounts() {
        String sql = "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT " +
                     "FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            System.out.println("SQL Error:");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getMessage());
        }
        return accounts;
    }

    /**
     * Searches accounts by matching the search term against
     * FIRST_NAME, LAST_NAME, PHONE, or ADDRESS using LIKE wildcards.
     */
    public List<Account> searchAccounts(String searchTerm) {
        String sql = "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT " +
                     "FROM ACCOUNTS " +
                     "WHERE FIRST_NAME LIKE ? OR LAST_NAME LIKE ? OR PHONE LIKE ? OR ADDRESS LIKE ? " +
                     "ORDER BY ID";
        List<Account> accounts = new ArrayList<>();
        String wrappedTerm = "%" + searchTerm + "%";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, wrappedTerm);
            stmt.setString(2, wrappedTerm);
            stmt.setString(3, wrappedTerm);
            stmt.setString(4, wrappedTerm);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRowToAccount(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error:");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getMessage());
        }
        return accounts;
    }

    /**
     * Displays accounts in a tabular format matching the COBOL output.
     */
    public void displayAccounts(List<Account> accounts) {
        System.out.println();
        System.out.println("ACCOUNTS:");
        System.out.println();
        System.out.printf(" %-5s | %-8s | %-8s | %-10s | %-22s | %-7s%n",
                "ID", "First", "Last", "Phone", "Address", "Enabled");
        System.out.println("------|----------|----------|------------|------------------------|---------");

        for (Account account : accounts) {
            System.out.printf(" %-5d | %-8s | %-8s | %-10s | %-22s | %s%n",
                    account.getId(),
                    account.getFirstName(),
                    account.getLastName(),
                    account.getPhone(),
                    account.getAddress(),
                    account.getIsEnabled());
        }
    }

    /**
     * Maps a ResultSet row to an Account object.
     */
    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("ID"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getString("PHONE"),
                rs.getString("ADDRESS"),
                rs.getString("IS_ENABLED"),
                rs.getString("CREATE_DT"),
                rs.getString("MOD_DT")
        );
    }
}
