package com.example.cobolmigration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.cobolmigration.model.Account;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test using @Testcontainers with PostgreSQL to verify
 * all repository query methods return correct results.
 * The Flyway migration (V1__create_accounts.sql) populates test data.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryIT {

    private static final String TEST_DB_NAME = "cobol_db_example";
    private static final String TEST_DB_USER = "postgres";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName(TEST_DB_NAME)
            .withUsername(TEST_DB_USER);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private AccountRepository repository;

    @Test
    void findAllByOrderByIdAsc_returnsAllAccountsOrdered() {
        List<Account> accounts = repository.findAllByOrderByIdAsc();

        // Flyway migration inserts 11 accounts
        assertEquals(11, accounts.size());

        // Verify order
        for (int i = 1; i < accounts.size(); i++) {
            assertTrue(accounts.get(i).getId() > accounts.get(i - 1).getId(),
                    "Accounts should be ordered by ID ascending");
        }

        // Verify first and last records
        assertEquals("John", accounts.get(0).getFirstName());
        assertEquals("Richard", accounts.get(accounts.size() - 1).getFirstName());
    }

    @Test
    void findByIsEnabledOrderByIdAsc_returnsDisabledAccounts() {
        List<Account> disabled = repository.findByIsEnabledOrderByIdAsc("N");

        // 3 disabled accounts: Bob (id=5), Paula (id=6), Bill (id=9)
        assertEquals(3, disabled.size());
        for (Account a : disabled) {
            assertEquals("N", a.getIsEnabled());
        }

        // Verify order
        for (int i = 1; i < disabled.size(); i++) {
            assertTrue(disabled.get(i).getId() > disabled.get(i - 1).getId());
        }
    }

    @Test
    void findByIsEnabledOrderByIdAsc_returnsEnabledAccounts() {
        List<Account> enabled = repository.findByIsEnabledOrderByIdAsc("Y");

        // 8 enabled accounts
        assertEquals(8, enabled.size());
        for (Account a : enabled) {
            assertEquals("Y", a.getIsEnabled());
        }
    }

    @Test
    void searchAccounts_byFirstName() {
        List<Account> results = repository.searchAccounts("%John%");
        assertFalse(results.isEmpty());
        assertEquals("John", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_byLastName() {
        List<Account> results = repository.searchAccounts("%Tester5%");
        assertFalse(results.isEmpty());
        assertEquals("Paula", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_byAddress() {
        List<Account> results = repository.searchAccounts("%Fake St%");
        assertFalse(results.isEmpty());
        assertEquals("John", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_byPhone() {
        List<Account> results = repository.searchAccounts("%15555550100%");
        assertFalse(results.isEmpty());
        assertEquals("John", results.get(0).getFirstName());
    }

    @Test
    void searchAccounts_noMatch() {
        List<Account> results = repository.searchAccounts("%ZZZZZZZ%");
        assertTrue(results.isEmpty());
    }

    @Test
    void searchAccounts_partialMatch() {
        // Search for "Tester" should match multiple records
        List<Account> results = repository.searchAccounts("%Tester%");
        assertTrue(results.size() > 1, "Should match multiple Tester accounts");

        // Verify results are ordered by ID
        for (int i = 1; i < results.size(); i++) {
            assertTrue(results.get(i).getId() > results.get(i - 1).getId());
        }
    }
}
