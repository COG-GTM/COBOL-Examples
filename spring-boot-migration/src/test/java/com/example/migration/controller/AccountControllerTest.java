package com.example.migration.controller;

import com.example.migration.model.Account;
import com.example.migration.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMvcTest integration tests for AccountController.
 * Tests all REST endpoints that replace the COBOL terminal menu.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getAllAccounts_returnsOkWithAccounts() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Account account = new Account("John", "Tester", "15555550100",
                "123 Fake St", "Y", now, now);
        account.setId(1L);

        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Tester"))
                .andExpect(jsonPath("$[0].isEnabled").value("Y"));
    }

    @Test
    void getAllAccounts_returnsEmptyList() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(List.of());

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getDisabledAccounts_returnsOnlyDisabled() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Account account = new Account("Bob", "Tester4", "15555550154",
                "119 Truck St", "N", now, now);
        account.setId(5L);

        when(accountService.getDisabledAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/disabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Bob"))
                .andExpect(jsonPath("$[0].isEnabled").value("N"));
    }

    @Test
    void searchAccounts_returnsMatchingAccounts() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Account account = new Account("John", "Tester", "15555550100",
                "123 Fake St", "Y", now, now);
        account.setId(1L);

        when(accountService.queryAccounts("John")).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/search").param("q", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void searchAccounts_withNoMatch_returnsEmptyList() throws Exception {
        when(accountService.queryAccounts("NONEXISTENT")).thenReturn(List.of());

        mockMvc.perform(get("/api/accounts/search").param("q", "NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchAccounts_withoutQueryParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/accounts/search"))
                .andExpect(status().isBadRequest());
    }
}
