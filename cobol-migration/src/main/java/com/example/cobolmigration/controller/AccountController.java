package com.example.cobolmigration.controller;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API replacing the terminal menu in sql/sql_example.cbl (lines 158-185).
 *
 * Menu options:
 *   1) Display all accounts     -> GET /api/accounts
 *   2) Display disabled accounts -> GET /api/accounts/disabled
 *   3) Query accounts           -> GET /api/accounts/search?q={searchTerm}
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Replaces menu option 1 — "Display all accounts" (display-all-accounts paragraph).
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    /**
     * Replaces menu option 2 — "Display disabled accounts" (display-disabled-accounts paragraph).
     */
    @GetMapping("/disabled")
    public ResponseEntity<List<Account>> getDisabledAccounts() {
        return ResponseEntity.ok(accountService.getDisabledAccounts());
    }

    /**
     * Replaces menu option 3 — "Query accounts" (query-accounts paragraph).
     * The COBOL code wraps the search term with '%' wildcards and does LIKE matching
     * across first_name, last_name, phone, address (sql/sql_example.cbl lines 334-336).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Account>> searchAccounts(@RequestParam("q") String searchTerm) {
        return ResponseEntity.ok(accountService.searchAccounts(searchTerm));
    }
}
