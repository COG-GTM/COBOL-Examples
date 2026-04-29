package com.example.cobolmigration.service;

import com.example.cobolmigration.dto.JsonRecord;
import com.example.cobolmigration.dto.XmlRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link SerializationService} verifying JSON and XML output matches
 * the COBOL-generated format.
 */
class SerializationServiceTest {

    private SerializationService serializationService;

    @BeforeEach
    void setUp() {
        serializationService = new SerializationService(new ObjectMapper());
    }

    @Test
    void generateJson_containsCorrectFieldNames() {
        JsonRecord record = new JsonRecord("Test Name", "Test Value", null, "true");
        String json = serializationService.generateJson(record);

        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"value\""));
        assertTrue(json.contains("\"enabled\""));
        assertTrue(json.contains("Test Name"));
        assertTrue(json.contains("Test Value"));
        // blank field should be omitted (SUPPRESS WHEN SPACES equivalent)
        assertFalse(json.contains("\"blank\""));
    }

    @Test
    void generateXml_containsDeclaration() {
        XmlRecord record = new XmlRecord("Test Name", "Test Value", null, "true");
        String xml = serializationService.generateXml(record);

        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("Test Name"));
        assertTrue(xml.contains("Test Value"));
    }

    @Test
    void generateXml_flagIsAttribute() {
        XmlRecord record = new XmlRecord("Test Name", "Test Value", null, "true");
        String xml = serializationService.generateXml(record);

        // enabled should appear as an attribute, not an element
        assertTrue(xml.contains("enabled=\"true\""));
    }

    @Test
    void generateXml_suppressesBlankField() {
        XmlRecord record = new XmlRecord("Test Name", "Test Value", null, "true");
        String xml = serializationService.generateXml(record);

        // blank field with null value should not appear in output
        assertFalse(xml.contains("<blank"));
    }

    @Test
    void createSampleJsonRecord_returnsValidRecord() {
        JsonRecord record = serializationService.createSampleJsonRecord();
        assertNotNull(record.getName());
        assertNotNull(record.getValue());
        assertNotNull(record.getEnabled());
    }

    @Test
    void createSampleXmlRecord_returnsValidRecord() {
        XmlRecord record = serializationService.createSampleXmlRecord();
        assertNotNull(record.getName());
        assertNotNull(record.getValue());
        assertNotNull(record.getEnabled());
    }
}
