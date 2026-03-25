package com.cobolmigration.controller;

import com.cobolmigration.entity.Account;
import com.cobolmigration.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AccountController.
 * Uses @SpringBootTest with H2 in PostgreSQL compatibility mode.
 * Tests all REST endpoints return correct data, verifying the same
 * SQL queries produce identical results to the COBOL embedded SQL.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        // Insert test data matching sql/create_test_db.sql (lines 22-53)
        accountRepository.save(Account.builder()
                .firstName("John").lastName("Tester")
                .phone("15555550100").address("123 Fake St, Nowhere")
                .isEnabled("Y").createDt(now).modDt(now).build());

        accountRepository.save(Account.builder()
                .firstName("Mike").lastName("Tester1")
                .phone("15555550121").address("122 Real St, Nowhere")
                .isEnabled("Y").createDt(now).modDt(now).build());

        accountRepository.save(Account.builder()
                .firstName("Bob").lastName("Tester4")
                .phone("15555550154").address("119 Truck St, Nowhere")
                .isEnabled("N").createDt(now).modDt(now).build());

        accountRepository.save(Account.builder()
                .firstName("Paula").lastName("Tester5")
                .phone("1555550165").address("118 Car St, Nowhere")
                .isEnabled("N").createDt(now).modDt(now).build());

        accountRepository.save(Account.builder()
                .firstName("Lucy").lastName("Tester9")
                .phone("1555550209").address("114 Beach St, Nowhere")
                .isEnabled("Y").createDt(now).modDt(now).build());
    }

    @Test
    void getAllAccounts_returnsAllOrderedById() throws Exception {
        // Replaces menu option 1 (sql_example.cbl lines 158-185)
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].firstName", is("Mike")))
                .andExpect(jsonPath("$[4].firstName", is("Lucy")));
    }

    @Test
    void getDisabledAccounts_returnsOnlyDisabled() throws Exception {
        // Replaces menu option 2 (sql_example.cbl lines 258-295)
        mockMvc.perform(get("/api/accounts").param("enabled", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Bob")))
                .andExpect(jsonPath("$[0].isEnabled", is("N")))
                .andExpect(jsonPath("$[1].firstName", is("Paula")))
                .andExpect(jsonPath("$[1].isEnabled", is("N")));
    }

    @Test
    void searchAccounts_byFirstName() throws Exception {
        // Replaces menu option 3 (sql_example.cbl lines 318-394)
        // COBOL uses LIKE '%search_term%' across first_name, last_name, phone, address
        mockMvc.perform(get("/api/accounts").param("search", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    void searchAccounts_byLastName() throws Exception {
        mockMvc.perform(get("/api/accounts").param("search", "Tester4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].lastName", is("Tester4")));
    }

    @Test
    void searchAccounts_byAddress() throws Exception {
        mockMvc.perform(get("/api/accounts").param("search", "Beach"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Lucy")));
    }

    @Test
    void searchAccounts_byPhone() throws Exception {
        mockMvc.perform(get("/api/accounts").param("search", "15555550100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    void searchAccounts_noResults() throws Exception {
        mockMvc.perform(get("/api/accounts").param("search", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchAccounts_caseInsensitive() throws Exception {
        mockMvc.perform(get("/api/accounts").param("search", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }
}
