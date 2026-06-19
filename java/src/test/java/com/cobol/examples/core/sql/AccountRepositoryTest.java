package com.cobol.examples.core.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountRepositoryTest {

    private Connection connection;
    private AccountRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test_" + java.util.UUID.randomUUID());
        repository = new AccountRepository(connection);
        repository.createSchema();
        repository.insert(new Account(1, "Ada", "Lovelace", "5550000001", "1 Way", true));
        repository.insert(new Account(2, "Grace", "Hopper", "5550000002", "2 Rd", true));
        repository.insert(new Account(3, "Alan", "Turing", "5550000003", "3 St", false));
    }

    @AfterEach
    void tearDown() throws SQLException {
        repository.close();
    }

    @Test
    void findAllReturnsRowsOrderedById() throws SQLException {
        List<Account> all = repository.findAll();
        assertEquals(3, all.size());
        assertEquals("Lovelace", all.get(0).lastName());
        assertFalse(all.get(2).enabled());
    }

    @Test
    void searchByLastNameUsesParameterBinding() throws SQLException {
        List<Account> hits = repository.searchByLastName("Hopper");
        assertEquals(1, hits.size());
        assertEquals("Grace", hits.get(0).firstName());

        assertTrue(repository.searchByLastName("Nobody").isEmpty());
    }
}
