package com.cobolmigration.cli;

import com.cobolmigration.model.Account;
import com.cobolmigration.service.AccountService;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Interactive command-line interface replacing the terminal menu from sql_example.cbl.
 *
 * <p>COBOL menu mapping (sql_example.cbl lines 158-185):
 * <ul>
 *   <li>1) Display all accounts &rarr; PERFORM display-all-accounts</li>
 *   <li>2) Display disabled accounts &rarr; PERFORM display-disabled-accounts</li>
 *   <li>3) Query accounts &rarr; PERFORM query-accounts</li>
 *   <li>4) Add account (new)</li>
 *   <li>5) Update account (new)</li>
 *   <li>6) Delete account (new)</li>
 *   <li>7) Toggle account enabled (new)</li>
 *   <li>8) Exit &rarr; EXIT PERFORM / CONNECT RESET</li>
 * </ul>
 *
 * @see sql/sql_example.cbl
 */
@Component
@Profile("cli")
public class AccountCli implements CommandLineRunner {

    private final AccountService accountService;

    public AccountCli(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("COBOL SQL DB Example Program (Java Migration)");
        System.out.println("----------------------------------------------");
        System.out.println();

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Display all accounts");
            System.out.println("2) Display disabled accounts");
            System.out.println("3) Query accounts");
            System.out.println("4) Add account");
            System.out.println("5) Update account");
            System.out.println("6) Delete account");
            System.out.println("7) Toggle account enabled");
            System.out.println("8) Exit");
            System.out.print("Selection: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> displayAllAccounts();
                case "2" -> displayDisabledAccounts();
                case "3" -> queryAccounts(scanner);
                case "4" -> addAccount(scanner);
                case "5" -> updateAccount(scanner);
                case "6" -> deleteAccount(scanner);
                case "7" -> toggleAccountEnabled(scanner);
                case "8" -> running = false;
                default -> System.out.println("Please make a selection between 1-8");
            }
        }

        System.out.println("Disconnected.");
        System.out.println();
    }

    private void displayAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        displayAccountResults(accounts);
    }

    private void displayDisabledAccounts() {
        List<Account> accounts = accountService.getDisabledAccounts();
        displayAccountResults(accounts);
    }

    private void queryAccounts(Scanner scanner) {
        boolean searchAgain = true;
        while (searchAgain) {
            System.out.println();
            System.out.print("Enter search value: ");
            String searchTerm = scanner.nextLine();

            List<Account> accounts = accountService.searchAccounts(searchTerm);
            displayAccountResults(accounts);

            System.out.println();
            System.out.print("Search again? (Y/[N]) ");
            String answer = scanner.nextLine().trim().toUpperCase();
            searchAgain = "Y".equals(answer);
        }
    }

    private void addAccount(Scanner scanner) {
        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();

        Account account = accountService.addAccount(firstName, lastName, phone, address);
        System.out.println("Account added with ID: " + account.getId());
    }

    private void updateAccount(Scanner scanner) {
        System.out.print("Account ID to update: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        System.out.print("New first name: ");
        String firstName = scanner.nextLine();
        System.out.print("New last name: ");
        String lastName = scanner.nextLine();
        System.out.print("New phone: ");
        String phone = scanner.nextLine();
        System.out.print("New address: ");
        String address = scanner.nextLine();

        Optional<Account> updated = accountService.updateAccount(id, firstName, lastName, phone, address);
        if (updated.isPresent()) {
            System.out.println("Account updated.");
        } else {
            System.out.println("Account not found.");
        }
    }

    private void deleteAccount(Scanner scanner) {
        System.out.print("Account ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        if (accountService.deleteAccount(id)) {
            System.out.println("Account deleted.");
        } else {
            System.out.println("Account not found.");
        }
    }

    private void toggleAccountEnabled(Scanner scanner) {
        System.out.print("Account ID to toggle: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        Optional<Account> toggled = accountService.toggleAccountEnabled(id);
        if (toggled.isPresent()) {
            System.out.println("Account enabled status toggled to: " + toggled.get().getIsEnabled());
        } else {
            System.out.println("Account not found.");
        }
    }

    /**
     * Displays account results in a table format matching the COBOL
     * display-account-results paragraph (sql_example.cbl lines 400-430).
     */
    private void displayAccountResults(List<Account> accounts) {
        System.out.println();
        System.out.println("ACCOUNTS:");
        System.out.println();
        System.out.println(" ID   | First    | Last     | Phone      | Address                | Enabled ");
        System.out.println("------|----------|----------|------------|------------------------|---------");

        for (Account account : accounts) {
            System.out.println(account.toString());
        }
    }
}
