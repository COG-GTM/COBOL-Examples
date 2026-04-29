package com.example.cobolmigration.controller;

import com.example.cobolmigration.entity.Account;
import com.example.cobolmigration.service.AccountService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link AccountController}.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getAllAccounts_returnsOkWithList() throws Exception {
        Account account = new Account("John", "Tester", "1555550100",
                "123 Fake St", "Y");
        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void getDisabledAccounts_returnsOnlyDisabled() throws Exception {
        Account disabled = new Account("Bob", "Tester4", "1555550154",
                "119 Truck St", "N");
        when(accountService.getDisabledAccounts())
                .thenReturn(List.of(disabled));

        mockMvc.perform(get("/api/accounts/disabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isEnabled").value("N"));
    }

    @Test
    void searchAccounts_returnsMatchingResults() throws Exception {
        Account account = new Account("John", "Tester", "1555550100",
                "123 Fake St", "Y");
        when(accountService.searchAccounts("John"))
                .thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/search").param("q", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void searchAccounts_returnsEmptyListWhenNoMatch() throws Exception {
        when(accountService.searchAccounts("ZZZZZ"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/accounts/search").param("q", "ZZZZZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
