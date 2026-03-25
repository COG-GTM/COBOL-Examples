package com.cobolmigration.service;

import com.cobolmigration.model.SerializationRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

/**
 * Service replacing COBOL JSON GENERATE in json_generate/json_generate.cbl.
 * Uses Jackson ObjectMapper for serialization.
 *
 * COBOL mapping (lines 42-54):
 *   JSON GENERATE ws-json-output FROM ws-record
 *     NAME OF ws-record-name IS "name"
 *     NAME OF ws-record-value IS "value"
 *     NAME OF ws-record-flag IS "enabled"
 */
@Service
public class JsonSerializationService {

    private final ObjectMapper objectMapper;

    public JsonSerializationService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Serializes a SerializationRecord to JSON string.
     * Replaces JSON GENERATE statement in json_generate.cbl (lines 42-54).
     *
     * @param record the record to serialize
     * @return JSON string representation
     * @throws JsonProcessingException if serialization fails
     */
    public String toJson(SerializationRecord record) throws JsonProcessingException {
        return objectMapper.writeValueAsString(record);
    }

    /**
     * Creates a sample record with test data matching json_generate.cbl (lines 37-40).
     * COBOL sets: ws-record-name = "Test Name", ws-record-value = "Test Value",
     * ws-record-flag = "true" (enabled), ws-record-blank = spaces.
     *
     * @return sample SerializationRecord
     */
    public SerializationRecord createSampleRecord() {
        return SerializationRecord.builder()
                .name("Test Name")
                .value("Test Value")
                .blank(null)
                .enabled("true")
                .build();
    }
}
