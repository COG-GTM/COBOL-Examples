package com.cobolmigration.service;

import com.cobolmigration.model.SerializableRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JsonGeneratorService - validates migration of json_generate/json_generate.cbl.
 */
class JsonGeneratorServiceTest {

    private JsonGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new JsonGeneratorService();
    }

    @Test
    void generateJson_matchesGoldenOutput() throws IOException {
        // Setup matching COBOL: move "Test Name" to ws-record-name,
        // move "Test Value" to ws-record-value, set ws-record-flag-enabled to true
        SerializableRecord record = new SerializableRecord("Test Name", "Test Value", "true");

        String json = service.generateCompactJson(record);

        // Load golden output
        String expected = Files.readString(
                Path.of("src/test/resources/golden/expected_json_output.json")).trim();

        assertEquals(expected, json);
    }

    @Test
    void generateJson_suppressesBlankFields() {
        // COBOL behavior: blank fields (ws-record-blank) are suppressed
        SerializableRecord record = new SerializableRecord("Name", "Value", "", "true");
        String json = service.generateCompactJson(record);

        assertFalse(json.contains("ws-record-blank"));
    }

    @Test
    void generateJson_suppressesNullFields() {
        SerializableRecord record = new SerializableRecord("Name", "Value", null, "true");
        String json = service.generateCompactJson(record);

        assertFalse(json.contains("ws-record-blank"));
    }

    @Test
    void generateJson_includesAllNonEmptyFields() {
        SerializableRecord record = new SerializableRecord("Test", "Val", "true");
        String json = service.generateCompactJson(record);

        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("\"value\""));
        assertTrue(json.contains("\"enabled\""));
    }

    @Test
    void getJsonCharCount_returnsCorrectLength() {
        String json = "{\"name\":\"test\"}";
        assertEquals(15, service.getJsonCharCount(json));
    }

    @Test
    void getJsonCharCount_nullReturnsZero() {
        assertEquals(0, service.getJsonCharCount(null));
    }

    @Test
    void generateJson_prettyPrinted() {
        SerializableRecord record = new SerializableRecord("Test", "Val", "true");
        String json = service.generateJson(record);

        // Pretty printed JSON should contain newlines
        assertTrue(json.contains("\n") || json.contains(System.lineSeparator()));
    }
}
