package com.example.cobolmigration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.repository.AccountRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void seed() {
        accountRepository.deleteAll();
        accountRepository.save(account("John", "Tester", "15555550100", "123 Fake St, Nowhere", true));
        accountRepository.save(account("Bob", "Tester4", "15555550154", "119 Truck St, Nowhere", false));
        accountRepository.save(account("Paula", "Tester5", "1555550165", "118 Car St, Nowhere", false));
    }

    private Account account(String first, String last, String phone, String address, boolean enabled) {
        return new Account(null, first, last, phone, address, enabled,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void findAllByOrderByIdAsc_returnsEveryAccount() {
        assertThat(accountRepository.findAllByOrderByIdAsc()).hasSize(3);
    }

    @Test
    void findByIsEnabledFalse_returnsOnlyDisabled() {
        List<Account> disabled = accountRepository.findByIsEnabledFalseOrderByIdAsc();
        assertThat(disabled).hasSize(2);
        assertThat(disabled).allMatch(a -> !a.getIsEnabled());
    }

    @Test
    void search_matchesAcrossNamePhoneAndAddress() {
        assertThat(accountRepository.search("%john%")).hasSize(1);
        assertThat(accountRepository.search("%Truck%")).hasSize(1);
        assertThat(accountRepository.search("%Tester%")).hasSize(3);
        assertThat(accountRepository.search("%nomatch%")).isEmpty();
    }
}
