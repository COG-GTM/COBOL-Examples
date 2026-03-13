package com.cobolmigration.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Documents and provides conversion utilities for COBOL computational data types.
 *
 * Migrated from: comp_test/comp_test.cbl
 *
 * COBOL Computational Type Mappings:
 *
 * | COBOL Type    | Description              | Java Equivalent        |
 * |---------------|--------------------------|------------------------|
 * | COMP / COMP-4 | Binary integer           | int / long             |
 * | COMP-1        | Single-precision float   | float                  |
 * | COMP-2        | Double-precision float    | double                 |
 * | COMP-3        | Packed decimal (BCD)      | BigDecimal             |
 * | COMP-5        | Native binary integer    | int / long (native)    |
 * | DISPLAY       | Zoned decimal (default)  | String / BigDecimal    |
 *
 * COMP/COMP-4 (Binary):
 *   PIC 9(1) to 9(4)  -> 2 bytes -> Java short/int
 *   PIC 9(5) to 9(9)  -> 4 bytes -> Java int
 *   PIC 9(10) to 9(18) -> 8 bytes -> Java long
 *
 * COMP-3 (Packed Decimal / BCD):
 *   Each decimal digit occupies a half-byte (nibble).
 *   The last nibble is the sign (C = positive, D = negative, F = unsigned).
 *   PIC 9(n) COMP-3 occupies (n+1)/2 bytes.
 *   Best mapped to BigDecimal in Java for exact arithmetic.
 *
 * COMP-5 (Native Binary):
 *   Similar to COMP/COMP-4 but uses the full range of the binary storage.
 *   PIC 9(1) to 9(4)  -> 2 bytes -> Java short/int (max 65535)
 *   PIC 9(5) to 9(9)  -> 4 bytes -> Java int (max 2^31-1)
 *   PIC 9(10) to 9(18) -> 8 bytes -> Java long (max 2^63-1)
 */
public final class ComputationalTypes {

    private ComputationalTypes() {
    }

    /**
     * Converts a COBOL COMP/COMP-4 display value to a Java int.
     * COMP values in COBOL are stored as binary integers.
     *
     * In the COBOL example:
     *   01 ws-comp-val PIC 999 COMP.
     *   MOVE 12 TO ws-comp-val
     *   MULTIPLY ws-comp-val BY 2 GIVING ws-comp-val  -> result: 24
     *
     * @param displayValue the display (string) representation of the number
     * @return the integer value
     */
    public static int compToInt(String displayValue) {
        return Integer.parseInt(displayValue.strip());
    }

    /**
     * Converts a COBOL COMP/COMP-4 display value to a Java long.
     * For larger PIC definitions (PIC 9(10) to 9(18)).
     *
     * @param displayValue the display representation of the number
     * @return the long value
     */
    public static long compToLong(String displayValue) {
        return Long.parseLong(displayValue.strip());
    }

    /**
     * Converts a COBOL COMP-3 (packed decimal) value to BigDecimal.
     * COMP-3 stores digits as BCD (Binary Coded Decimal), which maps
     * directly to BigDecimal for exact arithmetic.
     *
     * @param packedBytes the raw bytes of the packed decimal
     * @param scale       the number of decimal places (implied by PIC)
     * @return BigDecimal representation
     */
    public static BigDecimal comp3ToBigDecimal(byte[] packedBytes, int scale) {
        StringBuilder digits = new StringBuilder();
        boolean isNegative = false;

        for (int i = 0; i < packedBytes.length; i++) {
            int highNibble = (packedBytes[i] >> 4) & 0x0F;
            int lowNibble = packedBytes[i] & 0x0F;

            if (i == packedBytes.length - 1) {
                // Last byte: high nibble is digit, low nibble is sign
                digits.append(highNibble);
                isNegative = (lowNibble == 0x0D); // D = negative
            } else {
                digits.append(highNibble);
                digits.append(lowNibble);
            }
        }

        BigDecimal result = new BigDecimal(new BigInteger(digits.toString()), scale);
        return isNegative ? result.negate() : result;
    }

    /**
     * Converts a BigDecimal to COBOL COMP-3 (packed decimal) byte representation.
     *
     * @param value the decimal value
     * @param scale the number of decimal places
     * @return packed decimal bytes
     */
    public static byte[] bigDecimalToComp3(BigDecimal value, int scale) {
        BigDecimal scaled = value.movePointRight(scale);
        BigInteger unscaled = scaled.toBigInteger().abs();
        String digits = unscaled.toString();

        // Ensure odd number of digits (for packed format: digits + sign nibble)
        if (digits.length() % 2 == 0) {
            digits = "0" + digits;
        }

        int byteLength = (digits.length() + 1) / 2;
        byte[] result = new byte[byteLength];

        int digitIdx = 0;
        for (int i = 0; i < byteLength; i++) {
            int highNibble;
            int lowNibble;

            if (i == byteLength - 1) {
                // Last byte: one digit + sign nibble
                highNibble = digits.charAt(digitIdx) - '0';
                lowNibble = value.signum() >= 0 ? 0x0C : 0x0D;
            } else {
                highNibble = digits.charAt(digitIdx) - '0';
                lowNibble = digits.charAt(digitIdx + 1) - '0';
                digitIdx += 2;
            }

            result[i] = (byte) ((highNibble << 4) | lowNibble);
        }

        return result;
    }

    /**
     * Converts a COBOL COMP-5 (native binary) value to int.
     * COMP-5 uses the full range of binary storage, unlike COMP/COMP-4
     * which may be limited by the PIC clause.
     *
     * @param value the byte array representing the native binary
     * @return the integer value
     */
    public static int comp5ToInt(byte[] value) {
        int result = 0;
        for (byte b : value) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }

    /**
     * Converts a COBOL COMP-5 (native binary) value to long.
     *
     * @param value the byte array representing the native binary
     * @return the long value
     */
    public static long comp5ToLong(byte[] value) {
        long result = 0;
        for (byte b : value) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }

    /**
     * Converts a Java int to COMP-5 byte array.
     *
     * @param value    the integer value
     * @param numBytes the number of bytes (2 for PIC 9(1-4), 4 for PIC 9(5-9))
     * @return the byte array
     */
    public static byte[] intToComp5(int value, int numBytes) {
        byte[] result = new byte[numBytes];
        for (int i = numBytes - 1; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    /**
     * Converts a COBOL DISPLAY (zoned decimal) value to int.
     * DISPLAY is the default COBOL usage where each digit takes one byte.
     *
     * @param displayValue the zoned decimal string
     * @return the integer value
     */
    public static int displayToInt(String displayValue) {
        return Integer.parseInt(displayValue.strip());
    }

    /**
     * Formats an integer to a COBOL-style display format with leading zeros.
     * Mimics COBOL PIC 9(n) which always displays leading zeros.
     *
     * @param value  the integer value
     * @param digits the number of digits in the PIC clause
     * @return zero-padded string representation
     */
    public static String intToDisplay(int value, int digits) {
        return String.format("%0" + digits + "d", value);
    }

    /**
     * Formats an integer to a COBOL-style dynamic display with suppressed
     * leading zeros. Mimics COBOL PIC Z(n)9.
     *
     * @param value  the integer value
     * @param width  total display width
     * @return right-justified string with leading spaces
     */
    public static String intToDynamicDisplay(int value, int width) {
        return String.format("%" + width + "d", value);
    }
}
