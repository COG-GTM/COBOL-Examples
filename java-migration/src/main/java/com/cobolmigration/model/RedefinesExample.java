package com.cobolmigration.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Demonstrates COBOL REDEFINES pattern using ByteBuffer.
 *
 * Migrated from: redifines/redefines.cbl
 *
 * In COBOL, REDEFINES allows the same memory area to be interpreted as different
 * data types. For example:
 *
 *   05  ws-customer-name.
 *       10  ws-customer-first-name   PIC X(10).
 *       10  ws-customer-last-name    PIC X(20).
 *   05  ws-corp-name REDEFINES ws-customer-name PIC X(30).
 *
 * And:
 *   05  ws-data-disp-value           PIC X(10).
 *   05  ws-data-comp-value REDEFINES ws-data-disp-value COMP-2.
 *
 * In Java, we use ByteBuffer to simulate this memory-sharing behavior,
 * allowing the same bytes to be read as either a String or a double.
 */
public class RedefinesExample {

    /**
     * Represents a customer record that can be either a person (first/last name)
     * or a corporation (single corp name), sharing the same 30-byte name field.
     */
    public static class CustomerRecord {
        private static final int NAME_FIELD_LENGTH = 30;
        private static final int FIRST_NAME_LENGTH = 10;
        private static final int LAST_NAME_LENGTH = 20;
        private static final int ADDRESS_LENGTH = 20;
        private static final int STATE_LENGTH = 2;
        private static final int ZIP_LENGTH = 5;

        private int customerType; // 1 = Person, 2 = Corporation
        private final byte[] nameBytes = new byte[NAME_FIELD_LENGTH];
        private String streetAddress;
        private String state;
        private String zipCode;

        public CustomerRecord() {
        }

        public int getCustomerType() {
            return customerType;
        }

        public void setCustomerType(int customerType) {
            this.customerType = customerType;
        }

        public void setFirstName(String firstName) {
            byte[] src = padRight(firstName, FIRST_NAME_LENGTH).getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(src, 0, nameBytes, 0, Math.min(src.length, FIRST_NAME_LENGTH));
        }

        public String getFirstName() {
            return new String(nameBytes, 0, FIRST_NAME_LENGTH, StandardCharsets.US_ASCII).trim();
        }

        public void setLastName(String lastName) {
            byte[] src = padRight(lastName, LAST_NAME_LENGTH).getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(src, 0, nameBytes, FIRST_NAME_LENGTH, Math.min(src.length, LAST_NAME_LENGTH));
        }

        public String getLastName() {
            return new String(nameBytes, FIRST_NAME_LENGTH, LAST_NAME_LENGTH, StandardCharsets.US_ASCII).trim();
        }

        /** REDEFINES: access the same 30-byte field as a single corp name */
        public void setCorpName(String corpName) {
            byte[] src = padRight(corpName, NAME_FIELD_LENGTH).getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(src, 0, nameBytes, 0, Math.min(src.length, NAME_FIELD_LENGTH));
        }

        /** REDEFINES: read the same 30-byte field as a single corp name */
        public String getCorpName() {
            return new String(nameBytes, 0, NAME_FIELD_LENGTH, StandardCharsets.US_ASCII).trim();
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

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        private static String padRight(String s, int length) {
            if (s == null) {
                s = "";
            }
            if (s.length() >= length) {
                return s.substring(0, length);
            }
            return String.format("%-" + length + "s", s);
        }
    }

    /**
     * Demonstrates REDEFINES across different data types.
     *
     * In COBOL:
     *   05  ws-data-disp-value           PIC X(10).
     *   05  ws-data-comp-value REDEFINES ws-data-disp-value COMP-2.
     *
     * The same 8 bytes (COMP-2 = double-precision float) can be read as
     * either a display string or a numeric value.
     */
    public static class DualTypeField {
        private final ByteBuffer buffer;

        public DualTypeField() {
            this.buffer = ByteBuffer.allocate(10).order(ByteOrder.BIG_ENDIAN);
        }

        /** Write a display (string) value into the shared buffer */
        public void setDisplayValue(String value) {
            buffer.clear();
            byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
            buffer.put(bytes, 0, Math.min(bytes.length, 10));
            while (buffer.position() < 10) {
                buffer.put((byte) ' ');
            }
        }

        /** Read the shared buffer as a display string */
        public String getDisplayValue() {
            return new String(buffer.array(), 0, 10, StandardCharsets.US_ASCII);
        }

        /** Write a COMP-2 (double) value into the shared buffer */
        public void setCompValue(double value) {
            buffer.clear();
            buffer.putDouble(value);
        }

        /** Read the shared buffer as a COMP-2 (double) value */
        public double getCompValue() {
            buffer.position(0);
            return buffer.getDouble();
        }
    }
}
