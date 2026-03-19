package com.cobolmigration.controller;

import com.cobolmigration.model.Account;
import com.cobolmigration.service.AccountService;
import java.util.List;
import org.springframework.http.HttpStatus;
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

/**
 * REST API layer providing a modern alternative to the COBOL terminal UI.
 * Maps the account operations from sql/sql_example.cbl to HTTP endpoints.
 *
 * <p>Endpoint mapping:
 * <ul>
 *   <li>GET /accounts &rarr; display-all-accounts (ACCOUNT-ALL-CUR)</li>
 *   <li>GET /accounts/disabled &rarr; display-disabled-accounts (ACCOUNT-DISABLED-CUR)</li>
 *   <li>GET /accounts/search?q= &rarr; query-accounts (ACCOUNT-QUERY-CUR)</li>
 *   <li>GET /accounts/{id} &rarr; single account lookup</li>
 *   <li>POST /accounts &rarr; add account</li>
 *   <li>PUT /accounts/{id} &rarr; update account</li>
 *   <li>DELETE /accounts/{id} &rarr; delete account</li>
 *   <li>PUT /accounts/{id}/toggle &rarr; toggle enabled status</li>
 * </ul>
 *
 * @see sql/sql_example.cbl
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * GET /accounts - Display all accounts ordered by ID.
     */
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    /**
     * GET /accounts/disabled - Display disabled accounts.
     */
    @GetMapping("/disabled")
    public List<Account> getDisabledAccounts() {
        return accountService.getDisabledAccounts();
    }

    /**
     * GET /accounts/search?q={term} - Search accounts across all text fields.
     */
    @GetMapping("/search")
    public List<Account> searchAccounts(@RequestParam("q") String searchTerm) {
        return accountService.searchAccounts(searchTerm);
    }

    /**
     * GET /accounts/{id} - Get a single account by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /accounts - Add a new account.
     */
    @PostMapping
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        Account created = accountService.addAccount(
                account.getFirstName(),
                account.getLastName(),
                account.getPhone(),
                account.getAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /accounts/{id} - Update an existing account.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id,
                                                  @RequestBody Account account) {
        return accountService.updateAccount(id,
                        account.getFirstName(),
                        account.getLastName(),
                        account.getPhone(),
                        account.getAddress())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /accounts/{id} - Delete an account.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        if (accountService.deleteAccount(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * PUT /accounts/{id}/toggle - Toggle the enabled status.
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Account> toggleAccountEnabled(@PathVariable Long id) {
        return accountService.toggleAccountEnabled(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
