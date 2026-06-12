package com.example.cobolmigration.controller;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.service.AccountService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST replacement for the COBOL console menu in {@code sql/sql_example.cbl}
 * (lines 158-185). Each menu branch becomes an endpoint:
 * <ul>
 *     <li>menu option 1 ("Display all accounts") -&gt; {@code GET /api/accounts}</li>
 *     <li>menu option 2 ("Display disabled accounts") -&gt; {@code GET /api/accounts/disabled}</li>
 *     <li>menu option 3 ("Query accounts") -&gt; {@code GET /api/accounts/search?q=}</li>
 * </ul>
 * Jackson serializes the responses to JSON, replacing the manual
 * {@code JSON GENERATE} usage demonstrated in {@code json_generate/json_generate.cbl}.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/disabled")
    public List<Account> getDisabledAccounts() {
        return accountService.getDisabledAccounts();
    }

    @GetMapping("/search")
    public List<Account> searchAccounts(@RequestParam("q") String q) {
        return accountService.searchAccounts(q);
    }
}
