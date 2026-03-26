package com.example.cobolmigration.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.cobolmigration.model.SerializableRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests verifying JSON and XML output matches expected format
 * from json_generate/json_generate.cbl and xml_generate/xml_generate.cbl.
 */
class SerializationServiceTest {

    private SerializationService service;

    @BeforeEach
    void setUp() {
        service = new SerializationService();
    }

    // --- JSON tests (maps JSON GENERATE) ---

    @Test
    void generateJson_containsCustomFieldNames() {
        // From json_generate.cbl: name="Test Name", value="Test Value", enabled="true"
        SerializableRecord record = new SerializableRecord("Test Name", 42, "true");
        String json = service.generateJson(record);

        assertNotNull(json);
        assertTrue(json.contains("\"name\""), "JSON should contain 'name' field");
        assertTrue(json.contains("\"value\""), "JSON should contain 'value' field");
        assertTrue(json.contains("\"enabled\""), "JSON should contain 'enabled' field");
        assertTrue(json.contains("\"Test Name\""), "JSON should contain name value");
        assertTrue(json.contains("42"), "JSON should contain value");
        assertTrue(json.contains("\"true\""), "JSON should contain enabled value");
    }

    @Test
    void generateJson_withDisabledFlag() {
        SerializableRecord record = new SerializableRecord("Other", 0, "false");
        String json = service.generateJson(record);

        assertNotNull(json);
        assertTrue(json.contains("\"false\""));
    }

    @Test
    void generateJson_validJsonStructure() {
        SerializableRecord record = new SerializableRecord("Test", 1, "true");
        String json = service.generateJson(record);

        assertTrue(json.trim().startsWith("{"));
        assertTrue(json.trim().endsWith("}"));
    }

    // --- XML tests (maps XML GENERATE) ---

    @Test
    void generateXml_containsXmlDeclaration() {
        // From xml_generate.cbl: WITH XML-DECLARATION
        SerializableRecord record = new SerializableRecord("Test Name", 42, "true");
        String xml = service.generateXml(record);

        assertNotNull(xml);
        assertTrue(xml.contains("<?xml"), "XML should contain XML declaration");
    }

    @Test
    void generateXml_containsCustomFieldNames() {
        SerializableRecord record = new SerializableRecord("Test Name", 42, "true");
        String xml = service.generateXml(record);

        assertTrue(xml.contains("name"), "XML should contain 'name' element/attribute");
        assertTrue(xml.contains("value"), "XML should contain 'value' element/attribute");
        assertTrue(xml.contains("Test Name"), "XML should contain name value");
    }

    @Test
    void generateXml_enabledIsAttribute() {
        // From xml_generate.cbl: TYPE OF ws-record-flag IS ATTRIBUTE
        SerializableRecord record = new SerializableRecord("Test", 1, "true");
        String xml = service.generateXml(record);

        // The enabled field should be an attribute: enabled="true"
        assertTrue(xml.contains("enabled=\"true\""),
                "XML should contain 'enabled' as an attribute, got: " + xml);
    }

    @Test
    void generateXml_rootElement() {
        SerializableRecord record = new SerializableRecord("Test", 1, "true");
        String xml = service.generateXml(record);

        assertTrue(xml.contains("<ws-record"), "XML should have ws-record root element");
    }
}
