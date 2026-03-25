package com.cobolmigration.service;

import com.cobolmigration.model.SerializationRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for JsonSerializationService.
 * Verifies output matches the expected JSON structure from json_generate/json_generate.cbl.
 */
class JsonSerializationServiceTest {

    private JsonSerializationService jsonService;

    @BeforeEach
    void setUp() {
        jsonService = new JsonSerializationService();
    }

    @Test
    void toJson_generatesValidJson() throws JsonProcessingException {
        SerializationRecord record = jsonService.createSampleRecord();

        String json = jsonService.toJson(record);

        assertNotNull(json);
        assertTrue(json.contains("{"));
        assertTrue(json.contains("}"));
    }

    @Test
    void toJson_containsNamedFields() throws JsonProcessingException {
        SerializationRecord record = jsonService.createSampleRecord();

        String json = jsonService.toJson(record);

        // Verify field name mappings match json_generate.cbl lines 46-48
        assertTrue(json.contains("\"name\""), "Should contain 'name' field");
        assertTrue(json.contains("\"value\""), "Should contain 'value' field");
        assertTrue(json.contains("\"enabled\""), "Should contain 'enabled' field");
    }

    @Test
    void toJson_containsCorrectValues() throws JsonProcessingException {
        SerializationRecord record = jsonService.createSampleRecord();

        String json = jsonService.toJson(record);

        // Values should match json_generate.cbl lines 37-40
        assertTrue(json.contains("Test Name"));
        assertTrue(json.contains("Test Value"));
        assertTrue(json.contains("true"));
    }

    @Test
    void toJson_omitsNullBlankField() throws JsonProcessingException {
        // Empty/null fields should be omitted per @JsonInclude(NON_EMPTY)
        SerializationRecord record = jsonService.createSampleRecord();

        String json = jsonService.toJson(record);

        assertFalse(json.contains("\"blank\""),
                "Null/empty fields should be omitted");
    }

    @Test
    void toJson_includesNonEmptyBlankField() throws JsonProcessingException {
        SerializationRecord record = SerializationRecord.builder()
                .name("Test")
                .value("Val")
                .blank("Has Content")
                .enabled("false")
                .build();

        String json = jsonService.toJson(record);

        assertTrue(json.contains("\"blank\""));
        assertTrue(json.contains("Has Content"));
    }

    @Test
    void createSampleRecord_matchesCobolTestData() {
        // Verify sample data matches json_generate.cbl lines 37-40
        SerializationRecord record = jsonService.createSampleRecord();

        assertNotNull(record);
        assertTrue("Test Name".equals(record.getName()));
        assertTrue("Test Value".equals(record.getValue()));
        assertTrue(record.getBlank() == null || record.getBlank().isEmpty());
        assertTrue("true".equals(record.getEnabled()));
    }
}
