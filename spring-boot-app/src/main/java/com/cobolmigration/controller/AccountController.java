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
 * REST controller replacing the terminal-based menu from
 * {@code sql/sql_example.cbl} and the screen I/O from
 * {@code accept/accept.cbl}.
 *
 * <p>The original COBOL menu:
 * <pre>
 *   1) Display all accounts      &rarr; GET /api/accounts
 *   2) Display disabled accounts &rarr; GET /api/accounts/disabled
 *   3) Query accounts            &rarr; GET /api/accounts/search?q={query}
 *   4) Exit                      &rarr; (not applicable in REST API)
 * </pre>
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Returns all accounts (replaces menu option 1: display-all-accounts).
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Returns disabled accounts (replaces menu option 2: display-disabled-accounts).
     */
    @GetMapping("/disabled")
    public ResponseEntity<List<Account>> getDisabledAccounts() {
        List<Account> accounts = accountService.getDisabledAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Searches accounts by query string (replaces menu option 3: query-accounts).
     *
     * @param query the search term to match against account fields
     */
    @GetMapping("/search")
    public ResponseEntity<List<Account>> searchAccounts(
            @RequestParam("q") String query) {
        List<Account> accounts = accountService.searchAccounts(query);
        return ResponseEntity.ok(accounts);
    }
}
