package com.cobolexamples.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class SqlExample {

    private static final String COLS =
            "ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT";

    static final String QUERY_ALL =
            "SELECT " + COLS + " FROM ACCOUNTS ORDER BY ID";
    static final String QUERY_DISABLED =
            "SELECT " + COLS + " FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID";
    static final String QUERY_SEARCH =
            "SELECT " + COLS + " FROM ACCOUNTS WHERE FIRST_NAME LIKE ?"
                    + " OR LAST_NAME LIKE ? OR PHONE LIKE ? OR ADDRESS LIKE ?"
                    + " ORDER BY ID";

    record Account(int id, String firstName, String lastName, String phone,
                   String address, String isEnabled, String createDt, String modDt) {
        static Account fromRow(ResultSet rs) throws SQLException {
            return new Account(rs.getInt(1), Pic.x(rs.getString(2), 8),
                    Pic.x(rs.getString(3), 8), Pic.x(rs.getString(4), 10),
                    Pic.x(rs.getString(5), 22), Pic.x(rs.getString(6), 1),
                    Pic.x(rs.getString(7), 20), Pic.x(rs.getString(8), 20));
        }
    }

    static String formatRow(Account a) {
        return Pic.nine(a.id(), 5) + " | " + a.firstName() + " | "
                + a.lastName() + " | " + a.phone() + " | " + a.address()
                + " | " + a.isEnabled();
    }

    private final String url;
    private final String user;
    private final String password;
    private Connection conn;

    public SqlExample() {
        this.url = System.getenv().getOrDefault("COBOL_DB_URL",
                "jdbc:postgresql://localhost:5432/cobol_db_example");
        this.user = System.getenv().getOrDefault("COBOL_DB_USER", "postgres");
        this.password = System.getenv().getOrDefault("COBOL_DB_PASSWORD", "password");
    }

    public static void main(String[] args) {
        new SqlExample().run();
    }

    private void checkSqlState(SQLException e) {
        Console.display(" ");
        Console.display("SQL Error:");
        Console.display("SQLSTATE: ", e.getSQLState());
        Console.display("ERROR MESSAGE: ", e.getMessage());
        Console.display(" ");
        disconnectQuietly();
        System.exit(1);
    }

    private void disconnectQuietly() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignored) {
        }
    }

    private List<Account> fetch(PreparedStatement ps) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) accounts.add(Account.fromRow(rs));
        }
        return accounts;
    }

    private List<Account> query(String sql, String... params) {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setString(i + 1, params[i]);
            return fetch(ps);
        } catch (SQLException e) {
            checkSqlState(e);
            return List.of();
        }
    }

    void displayAccountResults(List<Account> accounts) {
        Console.display(" ");
        Console.display("ACCOUNTS:");
        Console.display(" ");
        Console.display(" ID   | First    | Last     | Phone      |"
                + " Address                | Enabled ");
        Console.display("------|----------|----------|------------|"
                + "------------------------|---------");
        for (Account a : accounts) {
            Console.display(formatRow(a));
        }
    }

    private void queryAccounts() {
        boolean searchAgain = true;
        while (searchAgain) {
            Console.display(" ");
            String search = Console.accept("Enter search value: ");
            String searchValue = "%" + Pic.trim(search) + "%";
            Console.display("Search value: ", searchValue);
            Console.display("Length: ", searchValue.length());

            displayAccountResults(query(QUERY_SEARCH,
                    searchValue, searchValue, searchValue, searchValue));

            Console.display(" ");
            String again = Console.accept("Search again? (Y/[N]) ");
            searchAgain = Pic.trim(again).equalsIgnoreCase("Y");
        }
    }

    void run() {
        Console.display(" ");
        Console.display("COBOL SQL DB Example Program");
        Console.display("----------------------------");
        Console.display(" ");

        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            checkSqlState(e);
            return;
        }

        while (true) {
            Console.display(" ");
            Console.display("1) Display all accounts");
            Console.display("2) Display disabled accounts");
            Console.display("3) Query accounts");
            Console.display("4) Exit");
            String choice = Console.accept("Selection: ");

            switch (Pic.trim(choice)) {
                case "1" -> displayAccountResults(query(QUERY_ALL));
                case "2" -> displayAccountResults(query(QUERY_DISABLED));
                case "3" -> queryAccounts();
                case "4" -> {
                    disconnectQuietly();
                    Console.display("Disconnected.");
                    Console.display(" ");
                    return;
                }
                default -> Console.display("Please make a selection between 1-4");
            }
        }
    }
}
