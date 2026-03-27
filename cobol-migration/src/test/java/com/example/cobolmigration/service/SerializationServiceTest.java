package com.example.cobolmigration.service;

import com.example.cobolmigration.model.SerializableRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SerializationServiceTest {

    private SerializationService service;

    @BeforeEach
    void setUp() {
        service = new SerializationService();
    }

    @Test
    void getDefaultRecord_matchesCobolTestData() {
        // json_generate.cbl lines 38-40 / xml_generate.cbl lines 37-39
        SerializableRecord record = service.getDefaultRecord();
        assertEquals("Test Name", record.getName());
        assertEquals("Test Value", record.getValue());
        assertTrue(record.isEnabled());
    }
}
