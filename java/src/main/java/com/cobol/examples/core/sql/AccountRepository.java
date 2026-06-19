package com.cobol.examples.core.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure-logic port of {@code sql/sql_example.cbl}.
 *
 * <p>The COBOL program used the {@code esqlOC} pre-compiler with embedded
 * {@code EXEC SQL} blocks against PostgreSQL. Here the same connect / query /
 * parameterised-search flow is expressed with plain JDBC against an in-memory
 * H2 database, so the example is self-contained and testable. Crucially, the
 * COBOL note about variable-length search strings (to avoid trailing-space
 * mismatches) is handled for free by JDBC parameter binding.
 */
public final class AccountRepository implements AutoCloseable {

    private final Connection connection;

    public AccountRepository(Connection connection) {
        this.connection = connection;
    }

    public void createSchema() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS account (
                    id          INTEGER PRIMARY KEY,
                    first_name  VARCHAR(8),
                    last_name   VARCHAR(8),
                    phone       VARCHAR(10),
                    address     VARCHAR(22),
                    is_enabled  CHAR(1)
                )
                """);
        }
    }

    public void insert(Account account) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO account (id, first_name, last_name, phone, address, is_enabled) "
                        + "VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, account.id());
            ps.setString(2, account.firstName());
            ps.setString(3, account.lastName());
            ps.setString(4, account.phone());
            ps.setString(5, account.address());
            ps.setString(6, account.enabled() ? "Y" : "N");
            ps.executeUpdate();
        }
    }

    public List<Account> findAll() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM account ORDER BY id")) {
            return readAll(ps);
        }
    }

    /** Equivalent of the parameterised {@code WHERE last_name LIKE :value} query. */
    public List<Account> searchByLastName(String pattern) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM account WHERE last_name LIKE ? ORDER BY id")) {
            ps.setString(1, pattern);
            return readAll(ps);
        }
    }

    private List<Account> readAll(PreparedStatement ps) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accounts.add(new Account(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        "Y".equalsIgnoreCase(rs.getString("is_enabled"))));
            }
        }
        return accounts;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
