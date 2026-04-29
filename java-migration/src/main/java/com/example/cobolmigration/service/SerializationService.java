package com.example.cobolmigration.service;

import com.example.cobolmigration.dto.JsonRecord;
import com.example.cobolmigration.dto.XmlRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import org.springframework.stereotype.Service;

/**
 * Service replacing the JSON GENERATE (json_generate/) and XML GENERATE
 * (xml_generate/) COBOL modules.
 */
@Service
public class SerializationService {

    private final ObjectMapper objectMapper;

    public SerializationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Generates a JSON string from a {@link JsonRecord}, mirroring the COBOL
     * JSON GENERATE statement (json_generate/json_generate.cbl lines 42-54).
     */
    public String generateJson(JsonRecord record) {
        try {
            return objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error generating JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a sample {@link JsonRecord} matching the COBOL test data
     * (lines 38-40 of json_generate.cbl).
     */
    public JsonRecord createSampleJsonRecord() {
        return new JsonRecord("Test Name", "Test Value", null, "true");
    }

    /**
     * Generates an XML string from an {@link XmlRecord}, mirroring the COBOL
     * XML GENERATE statement (xml_generate/xml_generate.cbl lines 41-56).
     *
     * Includes the XML declaration (WITH XML-DECLARATION) and renders
     * ws-record-flag as an attribute (TYPE OF ... IS ATTRIBUTE).
     */
    public String generateXml(XmlRecord record) {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlRecord.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(record, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Error generating XML: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a sample {@link XmlRecord} matching the COBOL test data
     * (lines 37-39 of xml_generate.cbl).
     */
    public XmlRecord createSampleXmlRecord() {
        return new XmlRecord("Test Name", "Test Value", null, "true");
    }
}
