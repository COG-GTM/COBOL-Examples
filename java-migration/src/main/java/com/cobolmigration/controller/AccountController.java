package com.cobolmigration.controller;

import com.cobolmigration.model.Account;
import com.cobolmigration.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API replacing the terminal menu from sql/sql_example.cbl lines 158-185.
 * Menu options 1-4 are replaced by distinct API endpoints.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * GET /api/accounts - Display all accounts.
     * Replaces menu option 1: display-all-accounts
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * GET /api/accounts/disabled - Display disabled accounts.
     * Replaces menu option 2: display-disabled-accounts
     */
    @GetMapping("/disabled")
    public ResponseEntity<List<Account>> getDisabledAccounts() {
        List<Account> accounts = accountService.getDisabledAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * GET /api/accounts/search?q={value} - Search accounts.
     * Replaces menu option 3: query-accounts
     */
    @GetMapping("/search")
    public ResponseEntity<List<Account>> searchAccounts(@RequestParam("q") String searchValue) {
        List<Account> accounts = accountService.searchAccounts(searchValue);
        return ResponseEntity.ok(accounts);
    }
}
