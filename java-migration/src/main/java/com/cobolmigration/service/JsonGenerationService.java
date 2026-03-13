package com.cobolmigration.service;

import com.cobolmigration.model.JsonRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Service for generating JSON from record structures.
 *
 * Migrated from: json_generate/json_generate.cbl
 *
 * The original COBOL program uses JSON GENERATE to serialize a record
 * structure to JSON, renaming fields:
 *   ws-record-name  -> "name"
 *   ws-record-value -> "value"
 *   ws-record-flag  -> "enabled"
 *
 * Empty/blank fields are excluded via @JsonInclude(NON_EMPTY) on the model.
 */
@Service
public class JsonGenerationService {

    private final ObjectMapper objectMapper;

    public JsonGenerationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Serializes a JsonRecord to a JSON string.
     *
     * @param record the record to serialize
     * @return JSON string representation
     * @throws JsonProcessingException if serialization fails
     */
    public String generateJson(JsonRecord record) throws JsonProcessingException {
        return objectMapper.writeValueAsString(record);
    }

    /**
     * Convenience method matching the COBOL example's test data.
     * Creates a record with name="Test Name", value="Test Value", enabled="true".
     *
     * @return JSON string of the example record
     * @throws JsonProcessingException if serialization fails
     */
    public String generateExampleJson() throws JsonProcessingException {
        JsonRecord record = new JsonRecord("Test Name", "Test Value", null, "true");
        return generateJson(record);
    }
}
