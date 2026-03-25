package com.cobolmigration.controller;

import com.cobolmigration.entity.Account;
import com.cobolmigration.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Account operations.
 * Replaces the COBOL menu-driven interface in sql_example.cbl (lines 158-185).
 *
 * Endpoint mapping:
 *   GET /api/accounts              -> menu option 1 (display all accounts)
 *   GET /api/accounts?enabled=false -> menu option 2 (display disabled accounts)
 *   GET /api/accounts?search={term} -> menu option 3 (query accounts)
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * GET /api/accounts - Returns all accounts, with optional filtering.
     *
     * @param enabled if "false", returns only disabled accounts (menu option 2)
     * @param search  if provided, searches accounts by term (menu option 3)
     * @return list of matching accounts
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAccounts(
            @RequestParam(required = false) String enabled,
            @RequestParam(required = false) String search) {

        List<Account> accounts;

        if (search != null && !search.isBlank()) {
            accounts = accountService.searchAccounts(search);
        } else if ("false".equalsIgnoreCase(enabled)) {
            accounts = accountService.getDisabledAccounts();
        } else {
            accounts = accountService.getAllAccounts();
        }

        return ResponseEntity.ok(accounts);
    }
}
