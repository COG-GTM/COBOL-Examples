package com.cobolmigration.controller;

import com.cobolmigration.model.Account;
import com.cobolmigration.repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller replacing the menu-driven UI from sql/sql_example.cbl (lines 158-185).
 *
 * COBOL menu options mapped to REST endpoints:
 *   "1) Display all accounts"      -> GET /api/accounts
 *   "2) Display disabled accounts" -> GET /api/accounts/disabled
 *   "3) Query accounts"            -> GET /api/accounts/search?q=term
 *   "4) Exit"                      -> N/A (stateless REST)
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Display all accounts, ordered by ID ascending.
     * Replaces COBOL menu option "1) Display all accounts"
     * which uses the ACCOUNT-ALL-CUR cursor (lines 119-125).
     *
     * @return list of all accounts
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountRepository.findAllByOrderByIdAsc();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Display disabled accounts, ordered by ID ascending.
     * Replaces COBOL menu option "2) Display disabled accounts"
     * which uses the ACCOUNT-DISABLED-CUR cursor (lines 130-137).
     *
     * @return list of disabled accounts
     */
    @GetMapping("/disabled")
    public ResponseEntity<List<Account>> getDisabledAccounts() {
        List<Account> accounts = accountRepository.findByEnabledFalseOrderByIdAsc();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Query accounts by search term across first name, last name, phone, and address.
     * Replaces COBOL menu option "3) Query accounts"
     * which uses the ACCOUNT-QUERY-CUR cursor (lines 142-153).
     *
     * The COBOL program wraps the search term with '%' wildcards and uses LIKE.
     * The JPA query handles this with %:term% in the JPQL.
     *
     * @param q the search term
     * @return list of matching accounts
     */
    @GetMapping("/search")
    public ResponseEntity<List<Account>> searchAccounts(@RequestParam("q") String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Account> accounts = accountRepository.searchByTerm(q.trim());
        return ResponseEntity.ok(accounts);
    }
}
