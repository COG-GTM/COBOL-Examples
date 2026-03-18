package com.migration.web;

import com.migration.database.Account;
import com.migration.database.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 7: UI Layer / REST Controller Tests
 *
 * @WebMvcTest tests for AccountController.
 * Verifies REST endpoints that replace COBOL ACCEPT/DISPLAY screen-mode UI.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    @DisplayName("GET /api/accounts - list all (replaces COBOL menu choice 1)")
    void listAllAccounts() throws Exception {
        Account account = createTestAccount(1, "John", "Tester");
        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Tester"));
    }

    @Test
    @DisplayName("GET /api/accounts?enabled=N - list disabled (replaces COBOL menu choice 2)")
    void listDisabledAccounts() throws Exception {
        Account account = createTestAccount(5, "Bob", "Tester4");
        account.setIsEnabled("N");
        when(accountService.getDisabledAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts").param("enabled", "N"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Bob"))
                .andExpect(jsonPath("$[0].isEnabled").value("N"));
    }

    @Test
    @DisplayName("GET /api/accounts?search=John - search (replaces COBOL menu choice 3)")
    void searchAccounts() throws Exception {
        Account account = createTestAccount(1, "John", "Tester");
        when(accountService.searchAccounts("John")).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts").param("search", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/accounts/{id} - get single account")
    void getAccountById() throws Exception {
        Account account = createTestAccount(1, "John", "Tester");
        when(accountService.getAccountById(1)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/accounts/{id} - not found returns 404")
    void getAccountNotFound() throws Exception {
        when(accountService.getAccountById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/accounts - create account (replaces COBOL ACCEPT input)")
    void createAccount() throws Exception {
        Account created = createTestAccount(1, "New", "User");
        when(accountService.createAccount(
                eq("New"), eq("User"), eq("1234567890"), eq("123 Test St"), eq("Y")))
                .thenReturn(created);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "New",
                                  "lastName": "User",
                                  "phone": "1234567890",
                                  "address": "123 Test St",
                                  "isEnabled": "Y"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("New"));
    }

    @Test
    @DisplayName("PUT /api/accounts/{id} - update account")
    void updateAccount() throws Exception {
        Account updated = createTestAccount(1, "Updated", "User");
        when(accountService.updateAccount(eq(1), eq("Updated"), eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id} - delete account")
    void deleteAccount() throws Exception {
        when(accountService.deleteAccount(1)).thenReturn(true);

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id} - not found returns 404")
    void deleteAccountNotFound() throws Exception {
        when(accountService.deleteAccount(999)).thenReturn(false);

        mockMvc.perform(delete("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }

    private Account createTestAccount(int id, String firstName, String lastName) {
        Account account = new Account(firstName, lastName, "15555550100", "123 Fake St", "Y");
        account.setId(id);
        account.setCreateDt(LocalDateTime.now());
        account.setModDt(LocalDateTime.now());
        return account;
    }
}
