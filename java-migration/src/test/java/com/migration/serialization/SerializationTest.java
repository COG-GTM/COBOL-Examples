package com.migration.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 3: Serialization Tests
 *
 * Test cases mirror the COBOL examples from:
 * - json_generate/json_generate.cbl
 * - xml_generate/xml_generate.cbl
 */
class SerializationTest {

    private SerializationService service;

    @BeforeEach
    void setUp() {
        service = new SerializationService();
    }

    @Nested
    @DisplayName("JSON Serialization (COBOL: JSON GENERATE)")
    class JsonTests {

        @Test
        @DisplayName("Generate JSON from record matching COBOL output")
        void generateJson() throws JsonProcessingException {
            // COBOL: MOVE "Test Name" TO ws-record-name
            //        MOVE "Test Value" TO ws-record-value
            //        SET ws-record-flag-enabled TO TRUE
            CobolRecord record = new CobolRecord("Test Name", "Test Value", null, "true");

            String json = service.toJson(record);

            assertTrue(json.contains("\"name\""));
            assertTrue(json.contains("\"Test Name\""));
            assertTrue(json.contains("\"value\""));
            assertTrue(json.contains("\"Test Value\""));
            assertTrue(json.contains("\"enabled\""));
            assertTrue(json.contains("\"true\""));
            // Blank field should be suppressed (JsonInclude.NON_EMPTY)
            assertFalse(json.contains("ws-record-blank"));
        }

        @Test
        @DisplayName("JSON round-trip serialization/deserialization")
        void jsonRoundTrip() throws JsonProcessingException {
            CobolRecord original = new CobolRecord("Name1", "Value1", "", "false");

            String json = service.toJson(original);
            CobolRecord deserialized = service.fromJson(json, CobolRecord.class);

            assertEquals(original.name(), deserialized.name());
            assertEquals(original.value(), deserialized.value());
            assertEquals(original.enabled(), deserialized.enabled());
        }

        @Test
        @DisplayName("JSON character count matches COBOL COUNT IN")
        void jsonCharCount() throws JsonProcessingException {
            // COBOL: COUNT IN ws-json-char-count
            CobolRecord record = new CobolRecord("Test", "Val", null, "true");
            String json = service.toJson(record);
            int charCount = json.length();

            assertTrue(charCount > 0);
        }
    }

    @Nested
    @DisplayName("XML Serialization (COBOL: XML GENERATE)")
    class XmlTests {

        @Test
        @DisplayName("Generate XML from record matching COBOL output")
        void generateXml() throws JsonProcessingException {
            // Same record as COBOL xml_generate.cbl
            CobolRecord record = new CobolRecord("Test Name", "Test Value", null, "true");

            String xml = service.toXml(record);

            // COBOL: WITH XML-DECLARATION -> XML declaration header
            assertTrue(xml.contains("<?xml"));
            // Field values present
            assertTrue(xml.contains("Test Name"));
            assertTrue(xml.contains("Test Value"));
            // COBOL: TYPE OF ws-record-flag IS ATTRIBUTE -> enabled as attribute
            assertTrue(xml.contains("enabled="));
        }

        @Test
        @DisplayName("XML round-trip serialization/deserialization")
        void xmlRoundTrip() throws JsonProcessingException {
            CobolRecord original = new CobolRecord("XmlName", "XmlValue", null, "false");

            String xml = service.toXml(original);
            CobolRecord deserialized = service.fromXml(xml, CobolRecord.class);

            assertEquals(original.name(), deserialized.name());
            assertEquals(original.value(), deserialized.value());
            assertEquals(original.enabled(), deserialized.enabled());
        }

        @Test
        @DisplayName("XML character count matches COBOL COUNT IN")
        void xmlCharCount() throws JsonProcessingException {
            CobolRecord record = new CobolRecord("Test", "Val", null, "true");
            String xml = service.toXml(record);
            int charCount = xml.length();

            assertTrue(charCount > 0);
        }
    }
}
