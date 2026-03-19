package com.cobolmigration.service;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AccountService validating business logic from sql_example.cbl.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(AccountService.class)
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        accountRepository.save(new Account("John", "Tester", "15555550100", "123 Fake St", 'Y'));
        accountRepository.save(new Account("Bob", "Tester4", "15555550154", "119 Truck St", 'N'));
    }

    @Test
    @DisplayName("getAllAccounts returns all accounts ordered by ID")
    void testGetAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        assertThat(accounts).hasSize(2);
    }

    @Test
    @DisplayName("getDisabledAccounts returns only disabled accounts")
    void testGetDisabledAccounts() {
        List<Account> disabled = accountService.getDisabledAccounts();
        assertThat(disabled).hasSize(1);
        assertThat(disabled.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("searchAccounts wraps term with wildcards and finds matches")
    void testSearchAccounts() {
        List<Account> results = accountService.searchAccounts("Tester");
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("addAccount creates a new enabled account")
    void testAddAccount() {
        Account added = accountService.addAccount("Jane", "Doe", "5551234567", "456 New St");
        assertThat(added.getId()).isNotNull();
        assertThat(added.getIsEnabled()).isEqualTo('Y');
        assertThat(added.getCreateDt()).isNotNull();
    }

    @Test
    @DisplayName("updateAccount modifies existing account fields")
    void testUpdateAccount() {
        Account existing = accountRepository.findAllByOrderByIdAsc().get(0);
        Optional<Account> updated = accountService.updateAccount(
                existing.getId(), "Updated", "Name", "9999999999", "New Address");

        assertThat(updated).isPresent();
        assertThat(updated.get().getFirstName()).isEqualTo("Updated");
        assertThat(updated.get().getModDt()).isNotNull();
    }

    @Test
    @DisplayName("updateAccount returns empty for nonexistent ID")
    void testUpdateNonexistentAccount() {
        Optional<Account> result = accountService.updateAccount(
                99999L, "X", "Y", "Z", "W");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteAccount removes an existing account")
    void testDeleteAccount() {
        Account existing = accountRepository.findAllByOrderByIdAsc().get(0);
        boolean deleted = accountService.deleteAccount(existing.getId());
        assertThat(deleted).isTrue();
        assertThat(accountRepository.findById(existing.getId())).isEmpty();
    }

    @Test
    @DisplayName("deleteAccount returns false for nonexistent ID")
    void testDeleteNonexistentAccount() {
        boolean deleted = accountService.deleteAccount(99999L);
        assertThat(deleted).isFalse();
    }

    @Test
    @DisplayName("toggleAccountEnabled flips Y to N and vice versa")
    void testToggleAccountEnabled() {
        Account enabledAccount = accountRepository.findAllByOrderByIdAsc().get(0);
        assertThat(enabledAccount.getIsEnabled()).isEqualTo('Y');

        Optional<Account> toggled = accountService.toggleAccountEnabled(enabledAccount.getId());
        assertThat(toggled).isPresent();
        assertThat(toggled.get().getIsEnabled()).isEqualTo('N');

        Optional<Account> toggledBack = accountService.toggleAccountEnabled(enabledAccount.getId());
        assertThat(toggledBack).isPresent();
        assertThat(toggledBack.get().getIsEnabled()).isEqualTo('Y');
    }
}
