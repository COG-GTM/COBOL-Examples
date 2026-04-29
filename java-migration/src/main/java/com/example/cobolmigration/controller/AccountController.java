package com.example.cobolmigration.controller;

import com.example.cobolmigration.entity.Account;
import com.example.cobolmigration.service.AccountService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller replacing the terminal-based menu system from
 * sql/sql_example.cbl (lines 155-186).
 *
 * COBOL menu option mapping:
 *   1) Display all accounts     -> GET /api/accounts
 *   2) Display disabled accounts -> GET /api/accounts/disabled
 *   3) Query accounts           -> GET /api/accounts/search?q={value}
 *   4) Exit                     -> (not applicable for REST)
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Replaces menu option "1) Display all accounts".
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    /**
     * Replaces menu option "2) Display disabled accounts".
     */
    @GetMapping("/disabled")
    public ResponseEntity<List<Account>> getDisabledAccounts() {
        return ResponseEntity.ok(accountService.getDisabledAccounts());
    }

    /**
     * Replaces menu option "3) Query accounts".
     *
     * The COBOL implementation wraps the trimmed search value with '%'
     * wildcards and queries across FIRST_NAME, LAST_NAME, PHONE, and ADDRESS.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Account>> searchAccounts(
            @RequestParam("q") String searchValue) {
        return ResponseEntity.ok(accountService.searchAccounts(searchValue));
    }
}
