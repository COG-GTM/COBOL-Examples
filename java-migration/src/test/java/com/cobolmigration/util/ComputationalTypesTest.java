package com.cobolmigration.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComputationalTypes.
 * Verifies COBOL COMP type conversions and BigDecimal precision.
 */
class ComputationalTypesTest {

    @Test
    void compToInt_shouldConvertDisplayToInteger() {
        // COBOL: ws-comp-val PIC 999 COMP, value 24
        assertEquals(24, ComputationalTypes.compToInt("24"));
        assertEquals(12, ComputationalTypes.compToInt("012"));
    }

    @Test
    void compToInt_shouldHandleLeadingSpaces() {
        assertEquals(42, ComputationalTypes.compToInt("  42  "));
    }

    @Test
    void intToDisplay_shouldZeroPad() {
        // COBOL: PIC 999 displays as "024"
        assertEquals("024", ComputationalTypes.intToDisplay(24, 3));
        assertEquals("012", ComputationalTypes.intToDisplay(12, 3));
    }

    @Test
    void intToDynamicDisplay_shouldSuppressLeadingZeros() {
        // COBOL: PIC ZZ9 displays as " 24"
        assertEquals(" 24", ComputationalTypes.intToDynamicDisplay(24, 3));
        assertEquals("  1", ComputationalTypes.intToDynamicDisplay(1, 3));
    }

    @Test
    void comp3ToBigDecimal_shouldDecodePackedDecimal() {
        // COMP-3: 12345 positive (sign = 0x0C)
        // Packed BCD: digits 1,2,3,4,5 + sign C
        // Encoding: 0x12, 0x34, 0x5C (high nibble=digit, low nibble=digit, last low=sign)
        byte[] packed = new byte[]{0x12, 0x34, 0x5C};
        BigDecimal result = ComputationalTypes.comp3ToBigDecimal(packed, 0);
        assertEquals(new BigDecimal("12345"), result);
    }

    @Test
    void comp3ToBigDecimal_shouldHandleNegativeValues() {
        // COMP-3: -12345 (sign = 0x0D)
        byte[] packed = new byte[]{0x12, 0x34, 0x5D};
        BigDecimal result = ComputationalTypes.comp3ToBigDecimal(packed, 0);
        assertEquals(new BigDecimal("-12345"), result);
    }

    @Test
    void comp3ToBigDecimal_shouldHandleScale() {
        // COMP-3: 123.45 with scale 2 -> stored as 12345
        byte[] packed = new byte[]{0x12, 0x34, 0x5C};
        BigDecimal result = ComputationalTypes.comp3ToBigDecimal(packed, 2);
        assertEquals(new BigDecimal("123.45"), result);
    }

    @Test
    void bigDecimalToComp3_shouldEncodeCorrectly() {
        byte[] result = ComputationalTypes.bigDecimalToComp3(new BigDecimal("12345"), 0);
        assertNotNull(result);
        // Verify round-trip
        BigDecimal decoded = ComputationalTypes.comp3ToBigDecimal(result, 0);
        assertEquals(new BigDecimal("12345"), decoded);
    }

    @Test
    void bigDecimalToComp3_shouldHandleNegative() {
        byte[] result = ComputationalTypes.bigDecimalToComp3(new BigDecimal("-12345"), 0);
        BigDecimal decoded = ComputationalTypes.comp3ToBigDecimal(result, 0);
        assertEquals(new BigDecimal("-12345"), decoded);
    }

    @Test
    void comp5ToInt_shouldConvertNativeBinary() {
        // COMP-5: 2-byte value 256
        byte[] bytes = new byte[]{0x01, 0x00};
        assertEquals(256, ComputationalTypes.comp5ToInt(bytes));
    }

    @Test
    void comp5ToInt_shouldConvert4ByteValue() {
        byte[] bytes = new byte[]{0x00, 0x00, 0x00, 0x18}; // 24
        assertEquals(24, ComputationalTypes.comp5ToInt(bytes));
    }

    @Test
    void intToComp5_shouldRoundTrip() {
        byte[] bytes = ComputationalTypes.intToComp5(24, 4);
        assertEquals(24, ComputationalTypes.comp5ToInt(bytes));
    }

    @Test
    void cobolArithmeticExample() {
        // Replicate COBOL comp_test.cbl:
        // MOVE 12 TO ws-comp-val
        // MULTIPLY ws-comp-val BY 2 GIVING ws-comp-val -> 24
        int compVal = 12;
        compVal = compVal * 2;
        assertEquals(24, compVal);

        // Display as PIC 999
        assertEquals("024", ComputationalTypes.intToDisplay(compVal, 3));

        // Display as PIC ZZ9
        assertEquals(" 24", ComputationalTypes.intToDynamicDisplay(compVal, 3));
    }
}
