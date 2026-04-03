package com.example.migration.cli;

import com.example.migration.model.Account;
import com.example.migration.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

/**
 * CLI runner that mirrors the original COBOL terminal menu from sql/sql_example.cbl.
 * Activated with the "cli" profile: --spring.profiles.active=cli
 *
 * Replaces the COBOL main menu (sql_example.cbl lines 157-185):
 *   1) Display all accounts
 *   2) Display disabled accounts
 *   3) Query accounts
 *   4) Exit
 *
 * Also replaces COBOL ACCEPT/DISPLAY patterns from accept/ and read_command_args/:
 *   - ACCEPT ws-menu-choice -> Scanner.nextLine()
 *   - DISPLAY "..." -> System.out.println()
 *   - ARGUMENT-VALUE/ARGUMENT-NUMBER -> ApplicationArguments (Spring Boot)
 */
@Component
@Profile("cli")
public class CliRunner implements CommandLineRunner {

    private final AccountService accountService;

    public CliRunner(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) {
        System.out.println();
        System.out.println("COBOL SQL DB Example Program (Java Migration)");
        System.out.println("----------------------------------------------");
        System.out.println();

        if (args.length > 0) {
            System.out.println("Command line arguments received: " + args.length);
            for (int i = 0; i < args.length; i++) {
                System.out.println("  Argument " + (i + 1) + ": " + args[i]);
            }
            System.out.println();
        }

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println();
                System.out.println("1) Display all accounts");
                System.out.println("2) Display disabled accounts");
                System.out.println("3) Query accounts");
                System.out.println("4) Exit");
                System.out.print("Selection: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> displayAllAccounts();
                    case "2" -> displayDisabledAccounts();
                    case "3" -> queryAccounts(scanner);
                    case "4" -> running = false;
                    default -> System.out.println("Please make a selection between 1-4");
                }
            }
        }

        System.out.println("Disconnected.");
        System.out.println();
    }

    /**
     * Replaces "display-all-accounts" paragraph (sql_example.cbl lines 201-246).
     */
    private void displayAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        displayAccountResults(accounts);
    }

    /**
     * Replaces "display-disabled-accounts" paragraph (sql_example.cbl lines 258-295).
     */
    private void displayDisabledAccounts() {
        List<Account> accounts = accountService.getDisabledAccounts();
        displayAccountResults(accounts);
    }

    /**
     * Replaces "query-accounts" paragraph (sql_example.cbl lines 318-394).
     */
    private void queryAccounts(Scanner scanner) {
        boolean searchAgain = true;
        while (searchAgain) {
            System.out.println();
            System.out.print("Enter search value: ");
            String searchString = scanner.nextLine().trim();

            System.out.println("Search value: %" + searchString + "%");

            List<Account> accounts = accountService.queryAccounts(searchString);
            displayAccountResults(accounts);

            System.out.println();
            System.out.print("Search again? (Y/[N]) ");
            String answer = scanner.nextLine().trim().toUpperCase();
            searchAgain = "Y".equals(answer);
        }
    }

    /**
     * Replaces "display-account-results" paragraph (sql_example.cbl lines 400-430).
     * Formats account data in a table similar to the COBOL output.
     */
    private void displayAccountResults(List<Account> accounts) {
        System.out.println();
        System.out.println("ACCOUNTS:");
        System.out.println();
        System.out.printf(" %-5s | %-8s | %-8s | %-10s | %-22s | %-7s%n",
                "ID", "First", "Last", "Phone", "Address", "Enabled");
        System.out.println("------|----------|----------|------------|" +
                "------------------------|---------");

        for (Account account : accounts) {
            System.out.printf(" %-5d | %-8s | %-8s | %-10s | %-22s | %-7s%n",
                    account.getId(),
                    account.getFirstName(),
                    account.getLastName(),
                    account.getPhone(),
                    account.getAddress(),
                    account.getIsEnabled());
        }
    }
}
