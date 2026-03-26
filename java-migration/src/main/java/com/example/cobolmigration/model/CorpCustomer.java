package com.example.cobolmigration.model;

/**
 * Corporate variant of the Customer record (customerType = 2).
 * Maps the corporate branch of the COBOL REDEFINES in redefines/redefines.cbl.
 * The corpName field (PIC X(30)) is the REDEFINES of the firstName + lastName fields.
 */
public class CorpCustomer extends Customer {

    private String corpName; // PIC X(30) — REDEFINES ws-customer-name

    public CorpCustomer() {
        setCustomerType(2);
    }

    public CorpCustomer(String corpName, String streetAddress, String state, String zipCode) {
        super(2, streetAddress, state, zipCode);
        setCorpName(corpName);
    }

    @Override
    public String getDisplayName() {
        return corpName;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = (corpName != null && corpName.length() > 30)
                ? corpName.substring(0, 30) : corpName;
    }
}
