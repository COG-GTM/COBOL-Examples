package com.cobolmigration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;

/**
 * Service for serializing objects to JSON and XML.
 *
 * <p>Replaces the COBOL JSON GENERATE statement in
 * {@code json_generate/json_generate.cbl} (lines 42-54) and the
 * XML GENERATE statement in {@code xml_generate/xml_generate.cbl}
 * (lines 41-56).</p>
 *
 * <p>In COBOL, the {@code NAME OF} clause maps working-storage field
 * names to custom output names. In Java, this is handled via
 * {@code @JsonProperty} annotations on the DTO classes.</p>
 */
@Service
public class SerializationService {

    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    public SerializationService() {
        this.jsonMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
    }

    /**
     * Serializes the given object to a JSON string.
     *
     * <p>Replaces:
     * <pre>
     *   JSON GENERATE ws-json-output
     *       FROM ws-record
     *       COUNT IN ws-json-char-count
     *       NAME OF ws-record-name IS "name", ...
     * </pre>
     *
     * @param object the object to serialize
     * @return JSON string representation
     * @throws JsonProcessingException if serialization fails
     */
    public String toJson(Object object) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(object);
    }

    /**
     * Serializes the given object to an XML string.
     *
     * <p>Replaces:
     * <pre>
     *   XML GENERATE ws-xml-output
     *       FROM ws-record
     *       COUNT IN ws-xml-char-count
     *       WITH XML-DECLARATION
     *       NAME OF ws-record-name IS "name", ...
     * </pre>
     *
     * @param object the object to serialize
     * @return XML string representation
     * @throws JsonProcessingException if serialization fails
     */
    public String toXml(Object object) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(object);
    }
}
