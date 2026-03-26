package com.cobolmigration.service;

import com.cobolmigration.dto.RecordDto;
import com.cobolmigration.dto.RecordXmlDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

/**
 * Service for JSON and XML serialization of record data.
 * Replaces:
 *   - json_generate.cbl: JSON GENERATE ws-json-output FROM ws-record
 *     with NAME OF clauses renaming fields during generation
 *   - xml_generate.cbl: XML GENERATE ws-xml-output FROM ws-record
 *     with NAME OF clauses, TYPE OF for attributes, SUPPRESS WHEN SPACES
 */
@Service
public class SerializationService {

    private final ObjectMapper objectMapper;

    public SerializationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Serialize a RecordDto to JSON string.
     * Replaces: JSON GENERATE ws-json-output FROM ws-record
     *           NAME OF ws-record-name is "name", ws-record-value is "value",
     *           ws-record-flag is "enabled"
     * Jackson annotations on RecordDto handle the NAME OF field renaming.
     */
    public String toJson(RecordDto record) {
        try {
            return objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error generating JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Serialize a RecordDto to XML string.
     * Replaces: XML GENERATE ws-xml-output FROM ws-record
     *           WITH XML-DECLARATION
     *           NAME OF ws-record-name is "name", ws-record-value is "value",
     *           ws-record-flag is "enabled"
     *           TYPE OF ws-record-flag IS ATTRIBUTE
     *           SUPPRESS WHEN SPACES
     * JAXB annotations on RecordXmlDto handle the NAME OF renaming,
     * attribute typing, and blank suppression.
     */
    public String toXml(RecordDto record) {
        try {
            RecordXmlDto xmlDto = RecordXmlDto.fromRecordDto(record);
            JAXBContext context = JAXBContext.newInstance(RecordXmlDto.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(xmlDto, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new SerializationException("Error generating XML: " + e.getMessage(), e);
        }
    }

    public static class SerializationException extends RuntimeException {
        public SerializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
