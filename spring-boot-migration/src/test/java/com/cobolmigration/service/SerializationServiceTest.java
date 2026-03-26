package com.cobolmigration.service;

import com.cobolmigration.dto.RecordDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for SerializationService verifying JSON and XML output
 * matches expected formats from the COBOL programs:
 *   json_generate/json_generate.cbl
 *   xml_generate/xml_generate.cbl
 */
class SerializationServiceTest {

    private SerializationService serializationService;

    @BeforeEach
    void setUp() {
        serializationService = new SerializationService(new ObjectMapper());
    }

    @Test
    void toJson_shouldGenerateValidJson() {
        RecordDto record = new RecordDto("Test Name", "Test Value", "", true);
        String json = serializationService.toJson(record);

        assertNotNull(json);
        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"Test Name\""));
        assertTrue(json.contains("\"value\""));
        assertTrue(json.contains("\"Test Value\""));
        assertTrue(json.contains("\"enabled\""));
        assertTrue(json.contains("true"));
    }

    @Test
    void toJson_shouldExcludeBlankFields() {
        // Mirrors COBOL behavior where blank ws-record-blank is empty
        RecordDto record = new RecordDto("Test Name", "Test Value", "", true);
        String json = serializationService.toJson(record);

        // @JsonInclude(NON_EMPTY) should suppress the blank field
        assertFalse(json.contains("\"blank\""));
    }

    @Test
    void toJson_shouldIncludeNonBlankFields() {
        RecordDto record = new RecordDto("Test Name", "Test Value", "has value", true);
        String json = serializationService.toJson(record);

        assertTrue(json.contains("\"blank\""));
        assertTrue(json.contains("has value"));
    }

    @Test
    void toJson_withFlagDisabled_shouldShowFalse() {
        RecordDto record = new RecordDto("Name", "Value", "", false);
        String json = serializationService.toJson(record);

        assertTrue(json.contains("false"));
    }

    @Test
    void toXml_shouldGenerateValidXml() {
        RecordDto record = new RecordDto("Test Name", "Test Value", "", true);
        String xml = serializationService.toXml(record);

        assertNotNull(xml);
        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("<ws-record"));
        assertTrue(xml.contains("<name>"));
        assertTrue(xml.contains("Test Name"));
        assertTrue(xml.contains("<value>"));
        assertTrue(xml.contains("Test Value"));
    }

    @Test
    void toXml_shouldHaveEnabledAsAttribute() {
        // In COBOL: TYPE OF ws-record-flag IS ATTRIBUTE
        RecordDto record = new RecordDto("Test", "Val", "", true);
        String xml = serializationService.toXml(record);

        assertTrue(xml.contains("enabled=\"true\""));
    }

    @Test
    void toXml_shouldSuppressBlankFields() {
        // In COBOL: SUPPRESS WHEN SPACES
        RecordDto record = new RecordDto("Test", "Val", "", true);
        String xml = serializationService.toXml(record);

        assertFalse(xml.contains("ws-record-blank"));
    }

    @Test
    void toXml_shouldIncludeNonBlankFields() {
        RecordDto record = new RecordDto("Test", "Val", "not blank", true);
        String xml = serializationService.toXml(record);

        assertTrue(xml.contains("ws-record-blank"));
        assertTrue(xml.contains("not blank"));
    }
}
