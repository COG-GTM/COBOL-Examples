package com.cobolmigration.service;

import com.cobolmigration.dto.RecordDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonService validating JSON generation matching json_generate/json_generate.cbl.
 */
class JsonServiceTest {

    private final JsonService jsonService = new JsonService(new ObjectMapper());

    @Test
    @DisplayName("generateJson produces correct JSON with renamed fields")
    void testGenerateJson() throws Exception {
        // Match the COBOL example data (json_generate.cbl lines 38-40)
        RecordDto record = new RecordDto("Test Name", "Test Value", null, "true");

        String json = jsonService.generateJson(record);

        assertThat(json).contains("\"name\":");
        assertThat(json).contains("\"Test Name\"");
        assertThat(json).contains("\"value\":");
        assertThat(json).contains("\"Test Value\"");
        assertThat(json).contains("\"enabled\":");
        assertThat(json).contains("\"true\"");
        // Blank field should be suppressed (null/empty)
        assertThat(json).doesNotContain("\"blank\"");
    }

    @Test
    @DisplayName("generateJsonWithCount returns character count matching COBOL COUNT IN")
    void testGenerateJsonWithCount() throws Exception {
        RecordDto record = new RecordDto("Test Name", "Test Value", null, "true");

        JsonService.JsonResult result = jsonService.generateJsonWithCount(record);

        assertThat(result.getJson()).isNotEmpty();
        assertThat(result.getCharCount()).isEqualTo(result.getJson().length());
        assertThat(result.getCharCount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("generateJson suppresses blank field when empty (SUPPRESS WHEN SPACES equivalent)")
    void testSuppressBlankField() throws Exception {
        RecordDto record = new RecordDto("Name", "Value", "", "false");

        String json = jsonService.generateJson(record);

        assertThat(json).doesNotContain("\"blank\"");
    }

    @Test
    @DisplayName("generateJson includes blank field when non-empty")
    void testNonEmptyBlankField() throws Exception {
        RecordDto record = new RecordDto("Name", "Value", "NotBlank", "false");

        String json = jsonService.generateJson(record);

        assertThat(json).contains("\"blank\"");
        assertThat(json).contains("\"NotBlank\"");
    }
}
