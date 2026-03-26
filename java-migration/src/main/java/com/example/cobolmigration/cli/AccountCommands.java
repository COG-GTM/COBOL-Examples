package com.example.cobolmigration.cli;

import com.example.cobolmigration.service.AccountService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Spring Shell component mapping the main menu from sql/sql_example.cbl (lines 157-185).
 *
 * COBOL menu:
 *   1) Display all accounts
 *   2) Display disabled accounts
 *   3) Query accounts
 *   4) Exit
 */
@ShellComponent
public class AccountCommands {

    private final AccountService accountService;

    public AccountCommands(AccountService accountService) {
        this.accountService = accountService;
    }

    @ShellMethod("Display all accounts")
    public String displayAll() {
        return accountService.formatAccountTable(accountService.getAllAccounts());
    }

    @ShellMethod("Display disabled accounts")
    public String displayDisabled() {
        return accountService.formatAccountTable(accountService.getDisabledAccounts());
    }

    @ShellMethod("Search accounts")
    public String search(@ShellOption String term) {
        return accountService.formatAccountTable(accountService.searchAccounts(term));
    }
}
