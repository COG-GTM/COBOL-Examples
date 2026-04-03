package com.cobolmigration.service;

import com.cobolmigration.model.SerializableRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

/**
 * Replaces json_generate/json_generate.cbl.
 * Uses Jackson ObjectMapper to produce JSON from SerializableRecord objects.
 * Handles the COBOL behavior where blank fields are suppressed (SUPPRESS WHEN SPACES).
 */
@Service
public class JsonGeneratorService {

    private final ObjectMapper objectMapper;

    public JsonGeneratorService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Generates JSON from a SerializableRecord.
     * Replaces: JSON GENERATE ws-json-output FROM ws-record (lines 42-54)
     * Blank fields are suppressed (NON_EMPTY inclusion).
     *
     * @param record the record to serialize
     * @return JSON string
     * @throws JsonGenerationException if serialization fails (replaces ON EXCEPTION)
     */
    public String generateJson(SerializableRecord record) {
        try {
            return objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new JsonGenerationException("Error generating JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Generates compact (non-pretty) JSON from a SerializableRecord.
     *
     * @param record the record to serialize
     * @return compact JSON string
     */
    public String generateCompactJson(SerializableRecord record) {
        try {
            ObjectMapper compactMapper = new ObjectMapper();
            compactMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            return compactMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new JsonGenerationException("Error generating JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the character count of the generated JSON.
     * Replaces: COUNT IN ws-json-char-count
     */
    public int getJsonCharCount(String json) {
        return json != null ? json.length() : 0;
    }

    /**
     * Custom exception replacing COBOL's ON EXCEPTION handling for JSON GENERATE.
     */
    public static class JsonGenerationException extends RuntimeException {
        public JsonGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
