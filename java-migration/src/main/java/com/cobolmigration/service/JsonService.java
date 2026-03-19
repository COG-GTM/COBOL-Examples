package com.cobolmigration.service;

import com.cobolmigration.dto.RecordDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Service generating JSON from record objects.
 * Replaces the COBOL JSON GENERATE statement from json_generate/json_generate.cbl.
 *
 * <p>COBOL equivalent (json_generate.cbl lines 42-54):
 * <pre>
 *   JSON GENERATE ws-json-output FROM ws-record
 *       COUNT IN ws-json-char-count
 *       NAME OF ws-record-name IS "name", ...
 * </pre>
 *
 * @see json_generate/json_generate.cbl
 */
@Service
public class JsonService {

    private final ObjectMapper objectMapper;

    public JsonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Generates JSON from a RecordDto.
     * Returns a result containing the JSON string and its character count,
     * matching the COBOL COUNT IN behavior.
     *
     * @param record the record to serialize
     * @return the generated JSON string
     * @throws JsonProcessingException if serialization fails
     */
    public String generateJson(RecordDto record) throws JsonProcessingException {
        return objectMapper.writeValueAsString(record);
    }

    /**
     * Generates JSON and returns the character count alongside the output,
     * matching the COBOL COUNT IN ws-json-char-count behavior.
     *
     * @param record the record to serialize
     * @return a JsonResult containing the JSON string and character count
     * @throws JsonProcessingException if serialization fails
     */
    public JsonResult generateJsonWithCount(RecordDto record) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(record);
        return new JsonResult(json, json.length());
    }

    /**
     * Result holder matching COBOL's dual output of JSON content and character count.
     */
    public static class JsonResult {
        private final String json;
        private final int charCount;

        public JsonResult(String json, int charCount) {
            this.json = json;
            this.charCount = charCount;
        }

        public String getJson() {
            return json;
        }

        public int getCharCount() {
            return charCount;
        }
    }
}
