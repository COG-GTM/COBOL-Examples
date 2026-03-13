package com.cobolmigration.service;

import com.cobolmigration.model.JsonRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonGenerationService.
 * Verifies JSON output matches the COBOL json_generate example.
 */
class JsonGenerationServiceTest {

    private JsonGenerationService service;

    @BeforeEach
    void setUp() {
        service = new JsonGenerationService(new ObjectMapper());
    }

    @Test
    void generateJson_shouldProduceCorrectFieldNames() throws Exception {
        // Match COBOL example: name="Test Name", value="Test Value", enabled="true"
        JsonRecord record = new JsonRecord("Test Name", "Test Value", null, "true");
        String json = service.generateJson(record);

        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"value\""));
        assertTrue(json.contains("\"enabled\""));
        assertTrue(json.contains("\"Test Name\""));
        assertTrue(json.contains("\"Test Value\""));
        assertTrue(json.contains("\"true\""));
    }

    @Test
    void generateJson_shouldExcludeBlankFields() throws Exception {
        // COBOL suppresses blank/empty fields in JSON output
        JsonRecord record = new JsonRecord("Test Name", "Test Value", null, "true");
        String json = service.generateJson(record);

        // blank field should not appear (it's null -> excluded by @JsonInclude(NON_EMPTY))
        assertFalse(json.contains("\"blank\""));
    }

    @Test
    void generateJson_shouldIncludeNonBlankFields() throws Exception {
        JsonRecord record = new JsonRecord("Test Name", "Test Value", "has value", "true");
        String json = service.generateJson(record);

        assertTrue(json.contains("\"blank\""));
        assertTrue(json.contains("\"has value\""));
    }

    @Test
    void generateExampleJson_shouldMatchCobolOutput() throws Exception {
        String json = service.generateExampleJson();

        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Test Name\""));
        assertTrue(json.contains("\"value\":\"Test Value\""));
        assertTrue(json.contains("\"enabled\":\"true\""));
        assertFalse(json.contains("\"blank\""));
    }

    @Test
    void generateJson_shouldHandleDefaultEnabledValue() throws Exception {
        // COBOL default: ws-record-flag PIC X(5) VALUE "false"
        JsonRecord record = new JsonRecord();
        record.setName("test");
        String json = service.generateJson(record);

        assertTrue(json.contains("\"enabled\":\"false\""));
    }
}
