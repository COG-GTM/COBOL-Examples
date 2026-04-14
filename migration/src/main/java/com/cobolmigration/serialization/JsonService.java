package com.cobolmigration.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * JSON serialization service migrated from json_generate/json_generate.cbl (lines 42-54).
 *
 * COBOL source:
 *   JSON GENERATE ws-json-output
 *     FROM ws-record
 *     COUNT IN ws-json-char-count
 *     NAME OF
 *       ws-record-name IS "name",
 *       ws-record-value IS "value",
 *       ws-record-flag IS "enabled"
 *     ON EXCEPTION
 *       DISPLAY "Error generating JSON error " JSON-CODE
 *       STOP RUN
 *     NOT ON EXCEPTION
 *       DISPLAY "JSON document successfully generated."
 *   END-JSON
 */
@Service
public class JsonService {

    private final ObjectMapper objectMapper;

    public JsonService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * Record DTO matching the COBOL ws-record structure from json_generate.cbl.
     *
     * COBOL field mappings with JSON GENERATE NAME OF:
     *   ws-record-name  (pic x(10)) -> @JsonProperty("name")
     *   ws-record-value (pic x(10)) -> @JsonProperty("value")
     *   ws-record-blank (pic x(10)) -> suppressed when empty
     *   ws-record-flag  (pic x(5))  -> @JsonProperty("enabled")
     */
    public static class Record {

        @JsonProperty("name")
        private String name;

        @JsonProperty("value")
        private String value;

        @JsonProperty("blank")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String blank;

        @JsonProperty("enabled")
        private String enabled;

        public Record() {
        }

        public Record(String name, String value, String blank, String enabled) {
            this.name = name;
            this.value = value;
            this.blank = blank;
            this.enabled = enabled;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getBlank() {
            return blank;
        }

        public void setBlank(String blank) {
            this.blank = blank;
        }

        public String getEnabled() {
            return enabled;
        }

        public void setEnabled(String enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabledFlag() {
            return "true".equalsIgnoreCase(enabled);
        }

        public void setEnabledFlag(boolean flag) {
            this.enabled = flag ? "true" : "false";
        }
    }

    /**
     * Result of JSON generation, including the output string and character count.
     * Models COBOL's COUNT IN ws-json-char-count.
     */
    public static class GenerateResult {
        private final String json;
        private final int charCount;

        public GenerateResult(String json, int charCount) {
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

    /**
     * Generates a JSON string from a Record object.
     * Equivalent to COBOL: JSON GENERATE ws-json-output FROM ws-record
     *
     * Handles error equivalent to COBOL's ON EXCEPTION clause by
     * throwing a JsonGenerationException.
     *
     * @param record the record to serialize
     * @return GenerateResult containing the JSON string and character count
     * @throws JsonGenerationException if JSON generation fails (equivalent to ON EXCEPTION)
     */
    public GenerateResult generateJson(Record record) {
        try {
            String json = objectMapper.writeValueAsString(record);
            return new GenerateResult(json, json.length());
        } catch (JsonProcessingException e) {
            throw new JsonGenerationException("Error generating JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a JSON string from any object.
     *
     * @param object the object to serialize
     * @return the JSON string
     * @throws JsonGenerationException if JSON generation fails
     */
    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonGenerationException("Error generating JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Exception equivalent to COBOL's ON EXCEPTION during JSON GENERATE.
     */
    public static class JsonGenerationException extends RuntimeException {
        public JsonGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
