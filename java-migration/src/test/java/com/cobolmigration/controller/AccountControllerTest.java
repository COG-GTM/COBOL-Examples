package com.cobolmigration.controller;

import com.cobolmigration.model.Account;
import com.cobolmigration.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AccountController REST endpoints.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /accounts returns all accounts")
    void testGetAllAccounts() throws Exception {
        Account account = createTestAccount(1L, "John", "Tester", 'Y');
        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Tester"));
    }

    @Test
    @DisplayName("GET /accounts/disabled returns disabled accounts")
    void testGetDisabledAccounts() throws Exception {
        Account account = createTestAccount(1L, "Bob", "Tester4", 'N');
        when(accountService.getDisabledAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/accounts/disabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Bob"))
                .andExpect(jsonPath("$[0].isEnabled").value("N"));
    }

    @Test
    @DisplayName("GET /accounts/search?q=term searches accounts")
    void testSearchAccounts() throws Exception {
        Account account = createTestAccount(1L, "John", "Tester", 'Y');
        when(accountService.searchAccounts("John")).thenReturn(List.of(account));

        mockMvc.perform(get("/accounts/search").param("q", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @DisplayName("GET /accounts/{id} returns account by ID")
    void testGetAccountById() throws Exception {
        Account account = createTestAccount(1L, "John", "Tester", 'Y');
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @DisplayName("GET /accounts/{id} returns 404 for nonexistent ID")
    void testGetAccountByIdNotFound() throws Exception {
        when(accountService.getAccountById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /accounts creates a new account")
    void testAddAccount() throws Exception {
        Account created = createTestAccount(1L, "Jane", "Doe", 'Y');
        when(accountService.addAccount(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(created);

        Account request = new Account("Jane", "Doe", "5551234567", "456 St", 'Y');

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    @DisplayName("PUT /accounts/{id} updates an existing account")
    void testUpdateAccount() throws Exception {
        Account updated = createTestAccount(1L, "Updated", "Name", 'Y');
        when(accountService.updateAccount(anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(updated));

        Account request = new Account("Updated", "Name", "9999999999", "New Address", 'Y');

        mockMvc.perform(put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /accounts/{id} deletes an account")
    void testDeleteAccount() throws Exception {
        when(accountService.deleteAccount(1L)).thenReturn(true);

        mockMvc.perform(delete("/accounts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /accounts/{id} returns 404 for nonexistent ID")
    void testDeleteAccountNotFound() throws Exception {
        when(accountService.deleteAccount(999L)).thenReturn(false);

        mockMvc.perform(delete("/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /accounts/{id}/toggle toggles enabled status")
    void testToggleAccountEnabled() throws Exception {
        Account toggled = createTestAccount(1L, "John", "Tester", 'N');
        when(accountService.toggleAccountEnabled(1L)).thenReturn(Optional.of(toggled));

        mockMvc.perform(put("/accounts/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isEnabled").value("N"));
    }

    private Account createTestAccount(Long id, String firstName, String lastName,
                                       Character isEnabled) {
        Account account = new Account(firstName, lastName, "5551234567", "123 Test St", isEnabled);
        account.setId(id);
        return account;
    }
}
