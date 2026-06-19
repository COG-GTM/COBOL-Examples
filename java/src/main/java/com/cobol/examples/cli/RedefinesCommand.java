package com.cobol.examples.cli;

import com.cobol.examples.core.redefines.Customer;
import com.cobol.examples.core.redefines.Customer.Address;
import com.cobol.examples.core.redefines.Customer.Corp;
import com.cobol.examples.core.redefines.Customer.Person;
import com.cobol.examples.ui.ConsoleView;
import java.util.List;
import picocli.CommandLine.Command;

@Command(name = "redefines", description = "REDEFINES overlay modelled with sealed types.")
public final class RedefinesCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        List<Customer> customers = List.of(
                new Person("test-first", "test-last", new Address("123 fake st", "NV", "12345")),
                new Corp("no-name corp", new Address("567 real st", "NY", "11795")));

        view.banner("Customer records");
        for (Customer customer : customers) {
            // Pattern matching replaces the COBOL 88-level type check + overlay read.
            switch (customer) {
                case Person p -> {
                    view.keyValue("Type", "PERSON");
                    view.keyValue("Name", p.firstName() + " " + p.lastName());
                }
                case Corp c -> {
                    view.keyValue("Type", "CORP");
                    view.keyValue("Company", c.companyName());
                }
            }
            Address a = customer.address();
            view.keyValue("Address", a.street() + ", " + a.state() + " " + a.zip());
            view.blank();
        }
    }
}
