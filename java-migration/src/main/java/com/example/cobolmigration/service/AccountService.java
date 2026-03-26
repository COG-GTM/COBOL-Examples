package com.example.cobolmigration.service;

import com.example.cobolmigration.model.Account;
import com.example.cobolmigration.repository.AccountRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Spring Service for account operations.
 * Migrated from the main menu logic in sql/sql_example.cbl.
 */
@Service
public class AccountService {

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    /** Calls repository.findAllByOrderByIdAsc() — replaces ACCOUNT-ALL-CUR. */
    public List<Account> getAllAccounts() {
        return repository.findAllByOrderByIdAsc();
    }

    /** Calls repository.findByIsEnabledOrderByIdAsc("N") — replaces ACCOUNT-DISABLED-CUR. */
    public List<Account> getDisabledAccounts() {
        return repository.findByIsEnabledOrderByIdAsc("N");
    }

    /**
     * Wraps searchTerm with "%" on both sides (like the COBOL code at sql/sql_example.cbl
     * lines 334-336), then calls repository.searchAccounts().
     */
    public List<Account> searchAccounts(String searchTerm) {
        String term = "%" + searchTerm.strip() + "%";
        return repository.searchAccounts(term);
    }

    /**
     * Formats accounts into a table string matching the COBOL display format
     * from sql/sql_example.cbl display-account-results paragraph.
     */
    public String formatAccountTable(List<Account> accounts) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nACCOUNTS:\n\n");
        sb.append(String.format(" %-5s | %-8s | %-8s | %-10s | %-22s | %-7s | %-16s | %-16s%n",
                "ID", "First", "Last", "Phone", "Address", "Enabled", "Create Date", "Mod Date"));
        sb.append(String.format("-------|----------|----------|------------|"
                + "------------------------|---------|------------------|-----------------%n"));

        for (Account a : accounts) {
            sb.append(String.format(" %-5s | %-8s | %-8s | %-10s | %-22s | %-7s | %-16s | %-16s%n",
                    a.getId(),
                    truncate(a.getFirstName(), 8),
                    truncate(a.getLastName(), 8),
                    truncate(a.getPhone(), 10),
                    truncate(a.getAddress(), 22),
                    a.getIsEnabled(),
                    a.getCreateDt() != null ? a.getCreateDt().format(DT_FORMAT) : "",
                    a.getModDt() != null ? a.getModDt().format(DT_FORMAT) : ""));
        }
        return sb.toString();
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
