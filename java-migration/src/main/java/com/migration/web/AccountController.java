package com.migration.web;

import com.migration.database.Account;
import com.migration.database.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Phase 7: UI Layer - Account REST Controller
 *
 * Replaces COBOL screen-mode UI (ACCEPT/DISPLAY) with REST API endpoints.
 *
 * COBOL UI patterns replaced:
 *
 * - DISPLAY "1) Display all accounts" + ACCEPT ws-menu-choice
 *   -> GET /api/accounts (no menu needed, client decides what to call)
 *
 * - DISPLAY all account data in table format
 *   -> GET /api/accounts returns JSON array
 *
 * - ACCEPT ws-search-string + ACCOUNT-QUERY-CUR
 *   -> GET /api/accounts?search=term
 *
 * - ACCEPT input fields for creating records
 *   -> POST /api/accounts with JSON body
 *
 * COBOL accept/accept.cbl features intentionally not migrated:
 * - Screen mode coordinates (AT yyxx) - no equivalent in REST
 * - Timeout accept - no equivalent (HTTP has its own timeouts)
 * - Auto-skip - no equivalent (form validation is client-side)
 * - No-echo/Secure input - handled by HTTPS + client-side masking
 * - Mouse handling (mouse/mouse_example.cbl) - no equivalent
 * - Screen size detection (screen_size/screen_size.cbl) - no equivalent
 *
 * These screen-mode features are intentionally dropped in favor of
 * a web UI where the browser handles all presentation concerns.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * GET /api/accounts - List all accounts.
     * Replaces COBOL: display-all-accounts paragraph (menu choice '1').
     * Optional ?search= parameter replaces menu choice '3' (query-accounts).
     * Optional ?enabled=N replaces menu choice '2' (display-disabled-accounts).
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAccounts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String enabled) {

        List<Account> accounts;
        if (search != null && !search.isBlank()) {
            accounts = accountService.searchAccounts(search);
        } else if ("N".equalsIgnoreCase(enabled)) {
            accounts = accountService.getDisabledAccounts();
        } else {
            accounts = accountService.getAllAccounts();
        }
        return ResponseEntity.ok(accounts);
    }

    /**
     * GET /api/accounts/{id} - Get a single account.
     * Replaces COBOL: ACCEPT + query by ID pattern.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable int id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/accounts - Create a new account.
     * Replaces COBOL: ACCEPT input fields + INSERT INTO ACCOUNTS.
     */
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, String> body) {
        Account account = accountService.createAccount(
                body.getOrDefault("firstName", ""),
                body.getOrDefault("lastName", ""),
                body.getOrDefault("phone", ""),
                body.getOrDefault("address", ""),
                body.getOrDefault("isEnabled", "Y")
        );
        return ResponseEntity.status(201).body(account);
    }

    /**
     * PUT /api/accounts/{id} - Update an existing account.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable int id,
            @RequestBody Map<String, String> body) {
        return accountService.updateAccount(
                        id,
                        body.get("firstName"),
                        body.get("lastName"),
                        body.get("phone"),
                        body.get("address"),
                        body.get("isEnabled"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/accounts/{id} - Delete an account.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int id) {
        if (accountService.deleteAccount(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
