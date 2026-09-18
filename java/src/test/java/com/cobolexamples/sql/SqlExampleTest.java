package com.cobolexamples.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.cobolexamples.support.Pic;

class SqlExampleTest {

    @Test
    void formatRowPadsToCobolWidths() {
        var a = new SqlExample.Account(1, Pic.x("John", 8), Pic.x("Tester", 8),
                Pic.x("15555550100", 10), Pic.x("123 Fake St, Nowhere", 22),
                Pic.x("Y", 1), Pic.x("2022-04-15 10:00:00", 20),
                Pic.x("2022-04-15 10:00:00", 20));
        assertEquals("00001 | John     | Tester   | 1555555010 | "
                + "123 Fake St, Nowhere   | Y", SqlExample.formatRow(a));
    }
}
