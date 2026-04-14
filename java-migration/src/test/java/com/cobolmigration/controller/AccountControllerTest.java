package com.cobolmigration.controller;

import com.cobolmigration.model.Account;
import com.cobolmigration.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for AccountController - validates the REST API replacing
 * the COBOL terminal menu from sql/sql_example.cbl.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getAllAccounts_returns200() throws Exception {
        Account account = new Account("John", "Tester", "1555555010", "123 Fake St", "Y");
        account.setId(1);
        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Tester"));
    }

    @Test
    void getDisabledAccounts_returns200() throws Exception {
        Account account = new Account("Bob", "Tester4", "1555555015", "119 Truck St", "N");
        account.setId(5);
        when(accountService.getDisabledAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/disabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isEnabled").value("N"));
    }

    @Test
    void searchAccounts_returns200() throws Exception {
        Account account = new Account("John", "Tester", "1555555010", "123 Fake St", "Y");
        account.setId(1);
        when(accountService.searchAccounts("John")).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/search").param("q", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void searchAccounts_emptyResult() throws Exception {
        when(accountService.searchAccounts("ZZZZ")).thenReturn(List.of());

        mockMvc.perform(get("/api/accounts/search").param("q", "ZZZZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllAccounts_emptyResult() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(List.of());

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
