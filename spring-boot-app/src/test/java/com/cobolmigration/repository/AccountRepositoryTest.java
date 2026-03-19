package com.cobolmigration.repository;

import com.cobolmigration.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link AccountRepository} using an embedded H2 database.
 *
 * <p>Test data mirrors the ACCOUNTS table structure from
 * {@code sql/create_test_db.sql}.</p>
 */
@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        Account account1 = new Account("John", "Tester", "1555550100", "123 Fake St, Nowhere", true);
        account1.setCreateDt(now);
        account1.setModDt(now);

        Account account2 = new Account("Bob", "Tester4", "1555550154", "119 Truck St, Nowhere", false);
        account2.setCreateDt(now);
        account2.setModDt(now);

        Account account3 = new Account("Paula", "Tester5", "1555550165", "118 Car St, Nowhere", false);
        account3.setCreateDt(now);
        account3.setModDt(now);

        Account account4 = new Account("Jane", "Tester7", "1555550187", "116 Sea St, Nowhere", true);
        account4.setCreateDt(now);
        account4.setModDt(now);

        accountRepository.saveAll(List.of(account1, account2, account3, account4));
    }

    @Test
    void findAllByOrderByIdAsc_returnsAllAccountsOrdered() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();

        assertThat(accounts).hasSize(4);
        assertThat(accounts.get(0).getId()).isLessThan(accounts.get(1).getId());
    }

    @Test
    void findByEnabledFalseOrderByIdAsc_returnsOnlyDisabledAccounts() {
        List<Account> accounts = accountRepository.findByEnabledFalseOrderByIdAsc();

        assertThat(accounts).hasSize(2);
        assertThat(accounts).allMatch(a -> !a.isEnabled());
        assertThat(accounts.get(0).getFirstName()).isEqualTo("Bob");
        assertThat(accounts.get(1).getFirstName()).isEqualTo("Paula");
    }

    @Test
    void searchAccounts_byFirstName_returnsMatchingAccounts() {
        List<Account> accounts = accountRepository.searchAccounts("%John%");

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void searchAccounts_byLastName_returnsMatchingAccounts() {
        List<Account> accounts = accountRepository.searchAccounts("%Tester5%");

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getFirstName()).isEqualTo("Paula");
    }

    @Test
    void searchAccounts_byAddress_returnsMatchingAccounts() {
        List<Account> accounts = accountRepository.searchAccounts("%Truck%");

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void searchAccounts_byPhone_returnsMatchingAccounts() {
        List<Account> accounts = accountRepository.searchAccounts("%0187%");

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getFirstName()).isEqualTo("Jane");
    }

    @Test
    void searchAccounts_partialMatch_returnsMultipleAccounts() {
        List<Account> accounts = accountRepository.searchAccounts("%Tester%");

        assertThat(accounts).hasSize(4);
    }

    @Test
    void searchAccounts_noMatch_returnsEmptyList() {
        List<Account> accounts = accountRepository.searchAccounts("%NONEXISTENT%");

        assertThat(accounts).isEmpty();
    }
}
