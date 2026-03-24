package com.cobolmigration.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Demonstrates Java equivalents of COBOL REDEFINES using ByteBuffer.
 * Replaces: redifines/redefines.cbl
 *
 * In COBOL, REDEFINES allows the same memory area to be interpreted as different types:
 *   05 ws-customer-name.
 *       10 ws-customer-first-name    pic x(10).
 *       10 ws-customer-last-name     pic x(20).
 *   05 ws-corp-name REDEFINES ws-customer-name pic x(30).
 *
 * And for type reinterpretation:
 *   05 ws-data-disp-value    pic x(10).
 *   05 ws-data-comp-value REDEFINES ws-data-disp-value COMP-2.
 *
 * In Java, we use ByteBuffer to slice the same byte array and interpret
 * it as different types (String, double, etc.).
 */
public class DataRedefines {

    private final byte[] data;

    public DataRedefines(int size) {
        this.data = new byte[size];
    }

    public DataRedefines(byte[] data) {
        this.data = data.clone();
    }

    /**
     * Write a string value at the given offset with a fixed length.
     * Mimics COBOL MOVE "value" TO ws-field where the field has a fixed PIC X(n).
     */
    public void writeString(int offset, int length, String value) {
        byte[] valueBytes = value.getBytes(StandardCharsets.US_ASCII);
        // COBOL pads with spaces
        for (int i = 0; i < length; i++) {
            data[offset + i] = (i < valueBytes.length) ? valueBytes[i] : (byte) ' ';
        }
    }

    /**
     * Read a string from the given offset with a fixed length.
     * Mimics reading a PIC X(n) field.
     */
    public String readString(int offset, int length) {
        return new String(data, offset, length, StandardCharsets.US_ASCII);
    }

    /**
     * Write a double value at the given offset.
     * Mimics MOVE value TO ws-data-comp-value (COMP-2 = IEEE 754 double).
     */
    public void writeDouble(int offset, double value) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, 8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putDouble(value);
    }

    /**
     * Read a double from the given offset.
     * Mimics reading a COMP-2 field (redefines over a PIC X(10) area).
     */
    public double readDouble(int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, 8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getDouble();
    }

    /**
     * Write an integer at the given offset.
     * Mimics COMP or COMP-5 fields.
     */
    public void writeInt(int offset, int value) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, 4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(value);
    }

    /**
     * Read an integer from the given offset.
     */
    public int readInt(int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, 4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getInt();
    }

    /**
     * Get the raw byte array (the shared memory area).
     */
    public byte[] getRawData() {
        return data.clone();
    }

    /**
     * Example: Customer name redefines.
     * The same 30-byte area can be read as:
     *   - first_name (10 bytes) + last_name (20 bytes) for person records
     *   - corp_name (30 bytes) for corporate records
     */
    public static class CustomerNameRedefines {
        private final DataRedefines buffer;
        private static final int NAME_OFFSET = 0;
        private static final int FIRST_NAME_LENGTH = 10;
        private static final int LAST_NAME_LENGTH = 20;
        private static final int CORP_NAME_LENGTH = 30;

        public CustomerNameRedefines() {
            this.buffer = new DataRedefines(CORP_NAME_LENGTH);
        }

        public void setFirstName(String value) {
            buffer.writeString(NAME_OFFSET, FIRST_NAME_LENGTH, value);
        }

        public String getFirstName() {
            return buffer.readString(NAME_OFFSET, FIRST_NAME_LENGTH).trim();
        }

        public void setLastName(String value) {
            buffer.writeString(NAME_OFFSET + FIRST_NAME_LENGTH, LAST_NAME_LENGTH, value);
        }

        public String getLastName() {
            return buffer.readString(NAME_OFFSET + FIRST_NAME_LENGTH, LAST_NAME_LENGTH).trim();
        }

        public void setCorpName(String value) {
            buffer.writeString(NAME_OFFSET, CORP_NAME_LENGTH, value);
        }

        public String getCorpName() {
            return buffer.readString(NAME_OFFSET, CORP_NAME_LENGTH).trim();
        }
    }
}
