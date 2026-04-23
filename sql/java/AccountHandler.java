import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountHandler {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/cobol_db_example";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "password";

    private Connection connection;

    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            System.out.println();
            System.out.println("SQL Error:");
            System.out.println("SQLSTATE: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            System.out.println();
            System.exit(1);
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected.");
            }
        } catch (SQLException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }

    public List<Account> getAllAccounts() {
        String sql = "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT FROM ACCOUNTS ORDER BY ID";
        return executeQuery(sql);
    }

    public List<Account> getDisabledAccounts() {
        String sql = "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID";
        return executeQuery(sql);
    }

    public List<Account> searchAccounts(String searchTerm) {
        String sql = "SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT " +
                     "FROM ACCOUNTS " +
                     "WHERE FIRST_NAME LIKE ? OR LAST_NAME LIKE ? OR PHONE LIKE ? OR ADDRESS LIKE ? " +
                     "ORDER BY ID";
        String wrappedTerm = "%" + searchTerm + "%";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, wrappedTerm);
            stmt.setString(2, wrappedTerm);
            stmt.setString(3, wrappedTerm);
            stmt.setString(4, wrappedTerm);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println();
            System.out.println("SQL Error:");
            System.out.println("SQLSTATE: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            System.out.println();
        }
        return accounts;
    }

    public void displayAccounts(List<Account> accounts) {
        System.out.println();
        System.out.println("ACCOUNTS:");
        System.out.println();
        System.out.printf(" %-5s | %-8s | %-8s | %-10s | %-22s | %-7s%n",
                "ID", "First", "Last", "Phone", "Address", "Enabled");
        System.out.println("-------|----------|----------|------------|------------------------|---------");

        for (Account account : accounts) {
            System.out.printf(" %-5d | %-8s | %-8s | %-10s | %-22s | %-7s%n",
                    account.getId(),
                    account.getFirstName(),
                    account.getLastName(),
                    account.getPhone(),
                    account.getAddress(),
                    account.getIsEnabled());
        }
    }

    private List<Account> executeQuery(String sql) {
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println();
            System.out.println("SQL Error:");
            System.out.println("SQLSTATE: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            System.out.println();
        }
        return accounts;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("ID"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getString("PHONE"),
                rs.getString("ADDRESS"),
                rs.getString("IS_ENABLED"),
                rs.getString("CREATE_DT"),
                rs.getString("MOD_DT"));
    }
}
