package com.cobolexamples.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SearchExampleTest {

    private final SearchExample app = new SearchExample();

    @Test
    void binarySearchHitsAndMisses() {
        assertNotNull(app.findById1(2));
        assertNull(app.findById1(7));
    }

    @Test
    void fullKeySearch() {
        assertNotNull(app.find(3, 103, 498));
        assertNull(app.find(3, 103, 500));
    }
}
