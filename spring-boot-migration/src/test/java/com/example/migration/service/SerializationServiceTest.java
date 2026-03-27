package com.example.migration.service;

import com.example.migration.model.RecordDto;
import com.example.migration.model.RecordXmlDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SerializationService.
 * Verifies JSON and XML output matches the COBOL-generated output format.
 *
 * COBOL json_generate.cbl produces:
 *   {"name":"Test Name","value":"Test Value","enabled":true}
 *
 * COBOL xml_generate.cbl produces XML with:
 *   - XML declaration
 *   - ws-record root element with enabled as attribute
 *   - name and value as child elements
 *   - blank field suppressed when spaces
 */
class SerializationServiceTest {

    private SerializationService serializationService;

    @BeforeEach
    void setUp() {
        serializationService = new SerializationService(new ObjectMapper());
    }

    @Test
    void generateJson_withEnabledRecord_producesCorrectJson() {
        RecordDto record = new RecordDto("Test Name", "Test Value", "", true);

        String json = serializationService.generateJson(record);

        assertTrue(json.contains("\"name\":\"Test Name\""));
        assertTrue(json.contains("\"value\":\"Test Value\""));
        assertTrue(json.contains("\"enabled\":true"));
        assertFalse(json.contains("blank"));
    }

    @Test
    void generateJson_withDisabledRecord_producesCorrectJson() {
        RecordDto record = new RecordDto("Other", "Data", "", false);

        String json = serializationService.generateJson(record);

        assertTrue(json.contains("\"name\":\"Other\""));
        assertTrue(json.contains("\"value\":\"Data\""));
        assertTrue(json.contains("\"enabled\":false"));
    }

    @Test
    void generateXml_withEnabledRecord_producesCorrectXml() {
        RecordXmlDto record = new RecordXmlDto("Test Name", "Test Value", null, "true");

        String xml = serializationService.generateXml(record);

        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("<name>Test Name</name>"));
        assertTrue(xml.contains("<value>Test Value</value>"));
        assertTrue(xml.contains("enabled=\"true\""));
    }

    @Test
    void generateXml_withBlankField_suppressesBlankElement() {
        RecordXmlDto record = new RecordXmlDto("Name", "Value", "   ", "false");

        String xml = serializationService.generateXml(record);

        assertFalse(xml.contains("<blank>   </blank>"));
    }

    @Test
    void generateXml_fromRecordDto_convertsCorrectly() {
        RecordDto record = new RecordDto("Test", "Val", null, true);

        String xml = serializationService.generateXml(record);

        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("<name>Test</name>"));
        assertTrue(xml.contains("<value>Val</value>"));
        assertTrue(xml.contains("enabled=\"true\""));
    }
}
