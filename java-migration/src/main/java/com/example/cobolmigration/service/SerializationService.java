package com.example.cobolmigration.service;

import com.example.cobolmigration.model.SerializableRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.stereotype.Service;

/**
 * Spring Service mapping json_generate/json_generate.cbl and xml_generate/xml_generate.cbl.
 * Uses Jackson ObjectMapper for JSON and XmlMapper for XML serialization.
 */
@Service
public class SerializationService {

    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    public SerializationService() {
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

        this.xmlMapper = new XmlMapper();
        this.xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    /**
     * Serializes a record to JSON with custom field names.
     * Maps the COBOL {@code JSON GENERATE} statement from json_generate/json_generate.cbl.
     *
     * @param record the record to serialize
     * @return JSON string
     */
    public String generateJson(SerializableRecord record) {
        try {
            return jsonMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error generating JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes a record to XML with XML declaration, attribute support for the
     * "enabled" field (mapping {@code TYPE OF ws-record-flag IS ATTRIBUTE}),
     * and field suppression for empty/spaces values (mapping {@code SUPPRESS WHEN SPACES}).
     * Maps the COBOL {@code XML GENERATE} statement from xml_generate/xml_generate.cbl.
     *
     * @param record the record to serialize
     * @return XML string
     */
    public String generateXml(SerializableRecord record) {
        try {
            return xmlMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error generating XML: " + e.getMessage(), e);
        }
    }
}
