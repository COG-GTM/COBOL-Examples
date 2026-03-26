package com.cobolmigration.controller;

import com.cobolmigration.entity.Account;
import com.cobolmigration.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WebMvcTest for AccountController.
 * Tests REST endpoints that replace the COBOL terminal menu system.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account createTestAccount(Long id, String firstName, String lastName) {
        Account account = new Account(firstName, lastName, "1234567890", "123 Test St", "Y");
        account.setId(id);
        return account;
    }

    @Test
    void getAllAccounts_shouldReturnAccountsList() throws Exception {
        List<Account> accounts = Arrays.asList(
                createTestAccount(1L, "John", "Tester"),
                createTestAccount(2L, "Mike", "Tester1")
        );
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    void getAllAccounts_withDisabledFilter_shouldReturnDisabled() throws Exception {
        Account disabled = createTestAccount(5L, "Bob", "Tester4");
        disabled.setIsEnabled("N");
        when(accountService.getDisabledAccounts()).thenReturn(Arrays.asList(disabled));

        mockMvc.perform(get("/api/accounts").param("enabled", "N"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isEnabled", is("N")));
    }

    @Test
    void getAccountById_shouldReturnAccount() throws Exception {
        Account account = createTestAccount(1L, "John", "Tester");
        when(accountService.getAccountById(1L)).thenReturn(account);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    void getAccountById_shouldReturn404WhenNotFound() throws Exception {
        when(accountService.getAccountById(999L))
                .thenThrow(new AccountService.AccountNotFoundException(999L));

        mockMvc.perform(get("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchAccounts_shouldReturnMatchingAccounts() throws Exception {
        Account account = createTestAccount(1L, "John", "Tester");
        when(accountService.searchAccounts("John")).thenReturn(Arrays.asList(account));

        mockMvc.perform(get("/api/accounts/search").param("q", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    void createAccount_shouldReturnCreatedAccount() throws Exception {
        Account account = createTestAccount(12L, "New", "User");
        when(accountService.createAccount(any(Account.class))).thenReturn(account);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("New")));
    }

    @Test
    void updateAccount_shouldReturnUpdatedAccount() throws Exception {
        Account updated = createTestAccount(1L, "Updated", "Name");
        when(accountService.updateAccount(eq(1L), any(Account.class))).thenReturn(updated);

        mockMvc.perform(put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void deleteAccount_shouldReturn204() throws Exception {
        doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());
    }
}
