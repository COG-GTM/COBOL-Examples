package com.example.cobolmigration.model;

/**
 * Corporate customer — corresponds to the COBOL REDEFINES layout
 * (redefines/redefines.cbl line 26) where ws-corp-name redefines ws-customer-name pic x(30).
 * When ws-customer-type is 2 (CORP), the same memory is read as a single corp name.
 */
public class CorpCustomer extends Customer {

    private String corpName;

    public CorpCustomer() {
        setCustomerType(CustomerType.CORP);
    }

    public CorpCustomer(String corpName, String streetAddress, String state, String zipCode) {
        super(CustomerType.CORP, streetAddress, state, zipCode);
        this.corpName = corpName;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }
}
