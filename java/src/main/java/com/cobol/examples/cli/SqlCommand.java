package com.cobol.examples.cli;

import com.cobol.examples.core.sql.Account;
import com.cobol.examples.core.sql.AccountRepository;
import com.cobol.examples.ui.ConsoleView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "sql", description = "Embedded-SQL example backed by in-memory H2.")
public final class SqlCommand implements Runnable {

    @Option(names = "--search", description = "last_name LIKE pattern.", defaultValue = "%")
    private String search;

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:cobol_db_example;DB_CLOSE_DELAY=-1");
             AccountRepository repository = new AccountRepository(connection)) {

            repository.createSchema();
            seed(repository);

            view.banner("All accounts");
            print(view, repository.findAll());

            view.banner("Search last_name LIKE '" + search + "'");
            print(view, repository.searchByLastName(search));
        } catch (SQLException e) {
            view.error("SQL error: " + e.getMessage());
        }
    }

    private void seed(AccountRepository repository) throws SQLException {
        repository.insert(new Account(1, "Ada", "Lovelace", "5550000001", "1 Analytical Way", true));
        repository.insert(new Account(2, "Grace", "Hopper", "5550000002", "2 Compiler Rd", true));
        repository.insert(new Account(3, "Alan", "Turing", "5550000003", "3 Machine St", false));
    }

    private void print(ConsoleView view, List<Account> accounts) {
        accounts.forEach(a -> view.keyValue(
                "%d %s %s".formatted(a.id(), a.firstName(), a.lastName()),
                a.enabled() ? "enabled" : "disabled"));
    }
}
