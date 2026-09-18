package com.cobolexamples.redefines;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class RedefinesExample {

    enum CustomerType { PERSON, CORP }

    static class Customer {
        CustomerType type;
        private String name = " ".repeat(30);
        String street = " ".repeat(20);
        String state = "  ";
        int zip;

        String firstName() { return name.substring(0, 10); }
        String lastName() { return name.substring(10, 30); }
        String corpName() { return name; }

        void setFirstName(String v) {
            name = Pic.x(v, 10) + name.substring(10);
        }

        void setLastName(String v) {
            name = name.substring(0, 10) + Pic.x(v, 20);
        }

        void setCorpName(String v) {
            name = Pic.x(v, 30);
        }
    }

    static class DataTypes {
        char type;
        final byte[] data = new byte[10];

        String dispValue() {
            return new String(data, java.nio.charset.StandardCharsets.UTF_8);
        }

        void setDispValue(String v) {
            byte[] b = Pic.x(v, 10).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            System.arraycopy(b, 0, data, 0, 10);
        }

        double compValue() {
            return ByteBuffer.wrap(data, 0, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        }

        void setCompValue(double v) {
            ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).putDouble(v);
            data[8] = ' ';
            data[9] = ' ';
        }
    }

    private final Customer[] customers = {new Customer(), new Customer(), new Customer()};
    private final DataTypes[] dataTypes = {new DataTypes(), new DataTypes()};

    public static void main(String[] args) {
        new RedefinesExample().run();
    }

    void run() {
        setupTestData();
        displayCustomerData();
        setupSecondTestData();
        displaySecondTestData();
    }

    void setupTestData() {
        Console.display(" ");
        Console.display("1. Person record with first/last name entered.");
        customers[0].type = CustomerType.PERSON;
        customers[0].setFirstName("test-first");
        customers[0].setLastName("test-last");
        customers[0].street = Pic.x("123 fake st", 20);
        customers[0].state = "NV";
        customers[0].zip = 12345;

        Console.display("2. Corp record with corp name entered.");
        customers[1].type = CustomerType.CORP;
        customers[1].setCorpName("no-name corp");
        customers[1].street = Pic.x("567 real st", 20);
        customers[1].state = "NY";
        customers[1].zip = 11795;

        Console.display("3. Person record with corp name entered.");
        customers[2].type = CustomerType.PERSON;
        customers[2].setCorpName("SET CORP VALUE");
        customers[2].street = Pic.x("890 what st", 20);
        customers[2].state = "MA";
        customers[2].zip = 9345;
    }

    void displayCustomerData() {
        Console.display(" ");
        Console.display("Displaying fake customer data:");
        Console.display("------------------------------");
        Console.display(" ");

        for (Customer c : customers) {
            if (c.type == CustomerType.PERSON) {
                Console.display("Customer Type: PERSON");
                Console.display("First Name: ", c.firstName());
                Console.display("Last Name: ", c.lastName());
            } else {
                Console.display("Customer Type: CORP");
                Console.display("Company name: ", c.corpName());
            }
            Console.display("Address: ");
            Console.display(c.street);
            Console.display(c.state, ", ", Pic.nine(c.zip, 5));
            Console.display("------------------------------");
            Console.display(" ");
        }
    }

    void setupSecondTestData() {
        dataTypes[0].type = 'D';
        dataTypes[0].setDispValue("ABC123");

        dataTypes[1].type = 'C';
        dataTypes[1].setCompValue(12345.63);
    }

    void displaySecondTestData() {
        Console.display(" ");
        Console.display("Redefines with different variable types:");
        Console.display("----------------------------------------");
        Console.display("Value entered in ws-data-disp-value: ABC123");
        Console.display("ws-data-disp-value x(10): ", dataTypes[0].dispValue());
        Console.display("ws-data-comp-value comp-2: ", dataTypes[0].compValue());
        Console.display(" ");
        Console.display("----------------------------------------");
        Console.display("Value entered in ws-data-comp-value: 12345.63");
        Console.display("ws-data-disp-value x(10): ", dataTypes[1].dispValue());
        Console.display("ws-data-comp-value comp-2: ", dataTypes[1].compValue());
        Console.display(" ");
    }
}
