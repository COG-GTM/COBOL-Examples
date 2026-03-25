package com.cobolmigration.service;

import com.cobolmigration.model.SerializationRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for XmlSerializationService.
 * Verifies output matches the expected XML structure from xml_generate/xml_generate.cbl.
 */
class XmlSerializationServiceTest {

    private XmlSerializationService xmlService;

    @BeforeEach
    void setUp() {
        xmlService = new XmlSerializationService();
    }

    @Test
    void toXml_generatesValidXml() throws JsonProcessingException {
        SerializationRecord record = xmlService.createSampleRecord();

        String xml = xmlService.toXml(record);

        assertNotNull(xml);
        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("Test Name"));
        assertTrue(xml.contains("Test Value"));
    }

    @Test
    void toXml_containsNamedFields() throws JsonProcessingException {
        SerializationRecord record = xmlService.createSampleRecord();

        String xml = xmlService.toXml(record);

        // Verify field name mappings match xml_generate.cbl lines 46-48
        assertTrue(xml.contains("name"), "Should contain 'name' element");
        assertTrue(xml.contains("value"), "Should contain 'value' element");
        assertTrue(xml.contains("enabled"), "Should contain 'enabled' attribute");
    }

    @Test
    void toXml_enabledIsAttribute() throws JsonProcessingException {
        SerializationRecord record = xmlService.createSampleRecord();

        String xml = xmlService.toXml(record);

        // COBOL: TYPE OF ws-record-flag IS ATTRIBUTE (line 49)
        assertTrue(xml.contains("enabled=\"true\""),
                "enabled should be an XML attribute, not an element");
    }

    @Test
    void toXml_suppressesEmptyBlankField() throws JsonProcessingException {
        // COBOL: SUPPRESS WHEN SPACES (line 50) - blank field should be omitted
        SerializationRecord record = xmlService.createSampleRecord();

        String xml = xmlService.toXml(record);

        assertFalse(xml.contains("<blank"),
                "Blank/empty fields should be suppressed (SUPPRESS WHEN SPACES)");
    }

    @Test
    void toXml_includesNonEmptyBlankField() throws JsonProcessingException {
        SerializationRecord record = SerializationRecord.builder()
                .name("Test")
                .value("Val")
                .blank("Non-Empty")
                .enabled("false")
                .build();

        String xml = xmlService.toXml(record);

        assertTrue(xml.contains("Non-Empty"),
                "Non-empty blank field should be included");
    }

    @Test
    void createSampleRecord_matchesCobolTestData() {
        // Verify sample data matches xml_generate.cbl lines 37-39
        SerializationRecord record = xmlService.createSampleRecord();

        assertNotNull(record);
        assertTrue("Test Name".equals(record.getName()));
        assertTrue("Test Value".equals(record.getValue()));
        assertTrue(record.getBlank() == null || record.getBlank().isEmpty());
        assertTrue("true".equals(record.getEnabled()));
    }
}
