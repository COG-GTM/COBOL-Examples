package com.example.migration.model;

/**
 * Replaces the COBOL REDEFINES pattern from redifines/redefines.cbl (lines 17-31).
 *
 * In COBOL, REDEFINES allows multiple data names to share the same memory:
 *   05 ws-customer-name.
 *       10 ws-customer-first-name  pic x(10).
 *       10 ws-customer-last-name   pic x(20).
 *   05 ws-corp-name REDEFINES ws-customer-name pic x(30).
 *
 * In Java, we use an inheritance/composition approach instead of memory reinterpretation.
 * The Customer class uses a type discriminator (PERSON vs CORP) and stores both
 * name representations, with accessor methods that return the appropriate view.
 */
public class Customer {

    public enum CustomerType {
        PERSON(1),
        CORP(2);

        private final int code;

        CustomerType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static CustomerType fromCode(int code) {
            for (CustomerType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown customer type code: " + code);
        }
    }

    private CustomerType customerType;
    private String firstName;
    private String lastName;
    private String corpName;
    private String streetAddress;
    private String state;
    private int zipCode;

    public Customer() {
    }

    /**
     * Creates a person-type customer (ws-customer-type-person value 1).
     */
    public static Customer createPerson(String firstName, String lastName,
                                        String streetAddress, String state, int zipCode) {
        Customer customer = new Customer();
        customer.customerType = CustomerType.PERSON;
        customer.firstName = firstName;
        customer.lastName = lastName;
        customer.streetAddress = streetAddress;
        customer.state = state;
        customer.zipCode = zipCode;
        return customer;
    }

    /**
     * Creates a corp-type customer (ws-customer-type-corp value 2).
     * The corpName occupies the same storage as firstName+lastName via REDEFINES.
     */
    public static Customer createCorp(String corpName,
                                      String streetAddress, String state, int zipCode) {
        Customer customer = new Customer();
        customer.customerType = CustomerType.CORP;
        customer.corpName = corpName;
        customer.streetAddress = streetAddress;
        customer.state = state;
        customer.zipCode = zipCode;
        return customer;
    }

    /**
     * Returns the display name based on customer type.
     * For PERSON: "firstName lastName"
     * For CORP: corpName (which in COBOL shares memory with firstName+lastName)
     */
    public String getDisplayName() {
        if (customerType == CustomerType.CORP) {
            return corpName;
        }
        return firstName + " " + lastName;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
}
