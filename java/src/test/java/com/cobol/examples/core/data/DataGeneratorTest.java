package com.cobol.examples.core.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DataGeneratorTest {

    private static final DataRecord RECORD = new DataRecord("Test Name", "Test Value", true);

    @Test
    void generatesJsonWithRenamedFields() {
        assertEquals("{\"name\":\"Test Name\",\"value\":\"Test Value\",\"enabled\":true}",
                DataGenerator.toJson(RECORD));
    }

    @Test
    void generatesXmlWithEnabledAttribute() {
        String xml = DataGenerator.toXml(RECORD);
        assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(xml.contains("enabled=\"true\""), xml);
        assertTrue(xml.contains("<name>Test Name</name>"), xml);
        assertTrue(xml.contains("<value>Test Value</value>"), xml);
    }
}
