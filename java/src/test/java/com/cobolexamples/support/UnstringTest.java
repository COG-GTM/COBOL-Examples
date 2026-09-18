package com.cobolexamples.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UnstringTest {

    private static final String SRC = Pic.x("Hello World", 30);

    @Test
    void example1SimpleTwoDest() {
        Unstring u = new Unstring(SRC).delimit(" ").dest(15).dest(15);
        boolean overflow = u.execute();
        assertEquals("Hello          ", u.destValue(0));
        assertEquals("World          ", u.destValue(1));
    }

    @Test
    void example2PointerAndOverflow() {
        int[] ptr = {1};
        Unstring u1 = new Unstring(SRC).delimitAll(" ").dest(15);
        assertTrue(u1.execute(ptr, null));
        assertEquals("Hello          ", u1.destValue(0));
        assertEquals(7, ptr[0]);

        Unstring u2 = new Unstring(SRC).delimitAll(" ").dest(15);
        assertFalse(u2.execute(ptr, null));
        assertEquals("World          ", u2.destValue(0));
        assertEquals(31, ptr[0]);
    }

    @Test
    void example3TwoDestNoOverflow() {
        int[] ptr = {1};
        Unstring u = new Unstring(SRC).delimitAll(" ").dest(15).dest(15);
        assertFalse(u.execute(ptr, null));
        assertEquals("Hello          ", u.destValue(0));
        assertEquals("World          ", u.destValue(1));
        assertEquals(31, ptr[0]);
    }

    @Test
    void example4MultiDelimsTallying() {
        String src = Pic.x("A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST", 30);
        String[] expected = {"A    ", "B    ", "CD   ", "E%FG ", "HIJ  ",
                "KL   ", "MN   ", "OP#QR", "ST   "};
        String[] delims = {"<", "<", ">", "!", "|", "!", ">", "!", " "};
        int[] counts = {1, 1, 2, 4, 3, 2, 2, 5, 2};
        int[] ptr = {1};
        int[] tally = {0};
        for (int i = 0; i < 9; i++) {
            StringBuilder d = new StringBuilder();
            int[] c = new int[1];
            Unstring u = new Unstring(src)
                    .delimitAll("<").delimit(">").delimit("!").delimit("|")
                    .dest(5, d, c);
            u.execute(ptr, tally);
            assertEquals(expected[i], u.destValue(0));
            assertEquals(delims[i], d.toString());
            assertEquals(counts[i], c[0]);
            assertEquals(i + 1, tally[0]);
        }
        assertEquals(31, ptr[0]);
    }

    @Test
    void example5SixDestinations() {
        String src = Pic.x("A<B<CD>EFG!HIJ|KLMN>O", 30);
        String[] expected = {"A    ", "B    ", "CD   ", "EFG  ", "HIJ  ", "KLMN "};
        Unstring u = new Unstring(src)
                .delimitAll("<").delimitAll(">").delimit("!").delimit("|");
        int[] tally = {0};
        for (int i = 0; i < 6; i++) u.dest(5);
        assertTrue(u.execute(new int[]{1}, tally));
        for (int i = 0; i < 6; i++) assertEquals(expected[i], u.destValue(i));
        assertEquals(6, tally[0]);
    }

    @Test
    void example6FormattedNumber() {
        Unstring u = new Unstring("123,456.12")
                .delimit(",").delimit(".")
                .dest(3).dest(3).dest(3);
        assertFalse(u.execute());
        assertEquals("123", u.destValue(0));
        assertEquals("456", u.destValue(1));
        assertEquals("12 ", u.destValue(2));
    }
}
