package com.example.cobolmigration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cobolmigration.controller.AccountController;
import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.service.AccountService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private Account sample() {
        return new Account(1L, "John", "Tester", "15555550100", "123 Fake St, Nowhere",
                true, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void getAllAccounts_returnsJson() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(List.of(sample()));
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].isEnabled").value(true));
    }

    @Test
    void getDisabledAccounts_returnsJson() throws Exception {
        when(accountService.getDisabledAccounts()).thenReturn(List.of());
        mockMvc.perform(get("/api/accounts/disabled"))
                .andExpect(status().isOk());
    }

    @Test
    void searchAccounts_passesQueryParam() throws Exception {
        when(accountService.searchAccounts("john")).thenReturn(List.of(sample()));
        mockMvc.perform(get("/api/accounts/search").param("q", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Tester"));
    }
}
