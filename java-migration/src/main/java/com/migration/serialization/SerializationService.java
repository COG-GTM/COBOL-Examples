package com.migration.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.stereotype.Service;

/**
 * Phase 3: Serialization Service
 *
 * Migrates COBOL JSON GENERATE (json_generate/json_generate.cbl) and
 * XML GENERATE (xml_generate/xml_generate.cbl) to Java/Jackson equivalents.
 *
 * COBOL JSON GENERATE uses libjson-c to serialize a record to JSON.
 * COBOL XML GENERATE uses libxml2 to serialize a record to XML.
 *
 * In Java, Jackson ObjectMapper replaces both operations with a unified API.
 *
 * COBOL field name mapping (NAME OF clause) is handled via Jackson annotations
 * (@JsonProperty, @JacksonXmlProperty) on the record classes, or by configuring
 * the ObjectMapper.
 *
 * COBOL: JSON GENERATE ws-json-output FROM ws-record COUNT IN ws-json-char-count
 * Java:  String json = serializationService.toJson(record); int count = json.length();
 *
 * COBOL: XML GENERATE ws-xml-output FROM ws-record WITH XML-DECLARATION
 * Java:  String xml = serializationService.toXml(record);
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
     * Serialize an object to JSON string.
     * Replaces COBOL: JSON GENERATE ws-json-output FROM ws-record
     */
    public String toJson(Object obj) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(obj);
    }

    /**
     * Deserialize a JSON string to an object of the specified type.
     */
    public <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return jsonMapper.readValue(json, clazz);
    }

    /**
     * Serialize an object to XML string.
     * Replaces COBOL: XML GENERATE ws-xml-output FROM ws-record WITH XML-DECLARATION
     */
    public String toXml(Object obj) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(obj);
    }

    /**
     * Deserialize an XML string to an object of the specified type.
     */
    public <T> T fromXml(String xml, Class<T> clazz) throws JsonProcessingException {
        return xmlMapper.readValue(xml, clazz);
    }
}
