package com.example.cobolmigration.controller;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getAllAccounts_shouldReturnAllAccounts() throws Exception {
        Account account = new Account("John", "Tester", "15555550100", "123 Fake St, Nowhere", "Y");
        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[0].lastName").value("Tester"));
    }

    @Test
    void getDisabledAccounts_shouldReturnDisabledOnly() throws Exception {
        Account disabled = new Account("Bob", "Tester4", "15555550154", "119 Truck St, Nowhere", "N");
        when(accountService.getDisabledAccounts()).thenReturn(List.of(disabled));

        mockMvc.perform(get("/api/accounts/disabled"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("Bob"))
            .andExpect(jsonPath("$[0].isEnabled").value("N"));
    }

    @Test
    void searchAccounts_shouldPassSearchTermToService() throws Exception {
        Account account = new Account("John", "Tester", "15555550100", "123 Fake St, Nowhere", "Y");
        when(accountService.searchAccounts("John")).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/search").param("q", "John"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void searchAccounts_shouldReturnEmptyListForNoMatches() throws Exception {
        when(accountService.searchAccounts("nonexistent")).thenReturn(List.of());

        mockMvc.perform(get("/api/accounts/search").param("q", "nonexistent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
