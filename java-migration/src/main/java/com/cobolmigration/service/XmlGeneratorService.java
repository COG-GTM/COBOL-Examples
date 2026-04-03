package com.cobolmigration.service;

import com.cobolmigration.model.SerializableRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.stereotype.Service;

/**
 * Replaces xml_generate/xml_generate.cbl.
 * Uses Jackson XmlMapper to produce XML from SerializableRecord objects.
 * Handles:
 * - XML declaration (WITH XML-DECLARATION from line 44)
 * - Attribute for enabled field (TYPE OF ws-record-flag IS ATTRIBUTE from line 49)
 * - Suppress empty/spaces fields (SUPPRESS WHEN SPACES from line 50)
 */
@Service
public class XmlGeneratorService {

    private final XmlMapper xmlMapper;

    public XmlGeneratorService() {
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    /**
     * Generates XML from a SerializableRecord.
     * Replaces: XML GENERATE ws-xml-output FROM ws-record (lines 41-56)
     * - Includes XML declaration
     * - enabled field rendered as attribute (via @JacksonXmlProperty annotation on model)
     * - Empty/spaces fields are suppressed (NON_EMPTY inclusion)
     *
     * @param record the record to serialize
     * @return XML string with declaration
     * @throws XmlGenerationException if serialization fails (replaces ON EXCEPTION)
     */
    public String generateXml(SerializableRecord record) {
        try {
            return xmlMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new XmlGenerationException("Error generating XML: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the character count of the generated XML.
     * Replaces: COUNT IN ws-xml-char-count
     */
    public int getXmlCharCount(String xml) {
        return xml != null ? xml.length() : 0;
    }

    /**
     * Custom exception replacing COBOL's ON EXCEPTION handling for XML GENERATE.
     */
    public static class XmlGenerationException extends RuntimeException {
        public XmlGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
