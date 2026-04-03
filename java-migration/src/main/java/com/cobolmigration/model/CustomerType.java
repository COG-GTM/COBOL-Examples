package com.cobolmigration.model;

/**
 * Replaces the 88-level conditions from redefines.cbl:
 * ws-customer-type-person value 1
 * ws-customer-type-corp value 2
 */
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
