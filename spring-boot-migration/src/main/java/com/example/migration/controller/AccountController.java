package com.example.migration.controller;

import com.example.migration.model.Account;
import com.example.migration.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller replacing the COBOL menu-driven terminal UI
 * from sql/sql_example.cbl (lines 155-185).
 *
 * COBOL menu mapping:
 *   1) Display all accounts     -> GET /api/accounts
 *   2) Display disabled accounts -> GET /api/accounts/disabled
 *   3) Query accounts           -> GET /api/accounts/search?q={query}
 *   4) Exit                     -> N/A (stateless REST)
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Replaces menu option "1) Display all accounts"
     * (sql_example.cbl line 170: perform display-all-accounts)
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Replaces menu option "2) Display disabled accounts"
     * (sql_example.cbl line 173: perform display-disabled-accounts)
     */
    @GetMapping("/disabled")
    public ResponseEntity<List<Account>> getDisabledAccounts() {
        List<Account> accounts = accountService.getDisabledAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Replaces menu option "3) Query accounts"
     * (sql_example.cbl line 176: perform query-accounts)
     *
     * The COBOL version prompts for a search string, trims it,
     * wraps with '%' wildcards, and queries across first_name,
     * last_name, phone, and address columns.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Account>> searchAccounts(@RequestParam("q") String query) {
        List<Account> accounts = accountService.queryAccounts(query);
        return ResponseEntity.ok(accounts);
    }
}
