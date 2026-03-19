package com.cobolmigration.controller;

import com.cobolmigration.model.Account;
import com.cobolmigration.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer tests for {@link AccountController}.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getAllAccounts_returnsOkWithAccounts() throws Exception {
        Account account = createTestAccount(1, "John", "Tester", true);
        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Tester")))
                .andExpect(jsonPath("$[0].enabled", is(true)));
    }

    @Test
    void getAllAccounts_emptyList_returnsOk() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(List.of());

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getDisabledAccounts_returnsOnlyDisabled() throws Exception {
        Account account = createTestAccount(2, "Bob", "Tester4", false);
        when(accountService.getDisabledAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/disabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Bob")))
                .andExpect(jsonPath("$[0].enabled", is(false)));
    }

    @Test
    void searchAccounts_returnsMatchingAccounts() throws Exception {
        Account account = createTestAccount(1, "John", "Tester", true);
        when(accountService.searchAccounts("John")).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/search").param("q", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    void searchAccounts_noResults_returnsEmptyList() throws Exception {
        when(accountService.searchAccounts("NONEXISTENT")).thenReturn(List.of());

        mockMvc.perform(get("/api/accounts/search").param("q", "NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchAccounts_missingQueryParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/accounts/search"))
                .andExpect(status().isBadRequest());
    }

    private Account createTestAccount(int id, String firstName, String lastName,
                                      boolean enabled) {
        Account account = new Account(firstName, lastName, "1555550100",
                "123 Fake St", enabled);
        account.setId(id);
        account.setCreateDt(LocalDateTime.now());
        account.setModDt(LocalDateTime.now());
        return account;
    }
}
