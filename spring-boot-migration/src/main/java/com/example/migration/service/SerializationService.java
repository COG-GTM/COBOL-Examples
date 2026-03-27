package com.example.migration.service;

import com.example.migration.model.RecordDto;
import com.example.migration.model.RecordXmlDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;
import java.io.StringWriter;

/**
 * Service handling JSON and XML serialization of records.
 *
 * Replaces:
 *   - json_generate/json_generate.cbl: JSON GENERATE statement (lines 42-54)
 *   - xml_generate/xml_generate.cbl: XML GENERATE statement (lines 41-56)
 *
 * The COBOL JSON GENERATE converts a record structure to JSON using
 * NAME OF clauses for field name mapping. Jackson @JsonProperty annotations
 * provide the same functionality.
 *
 * The COBOL XML GENERATE uses NAME OF, TYPE OF (ATTRIBUTE), and
 * SUPPRESS WHEN SPACES directives. JAXB annotations replicate this.
 */
@Service
public class SerializationService {

    private final ObjectMapper objectMapper;

    public SerializationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Generates JSON from a RecordDto.
     * Replaces COBOL: JSON GENERATE ws-json-output FROM ws-record
     *   COUNT IN ws-json-char-count
     *   NAME OF ws-record-name IS "name", ws-record-value IS "value",
     *           ws-record-flag IS "enabled"
     */
    public String generateJson(RecordDto record) {
        try {
            return objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error generating JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Generates XML from a RecordXmlDto.
     * Replaces COBOL: XML GENERATE ws-xml-output FROM ws-record
     *   WITH XML-DECLARATION
     *   NAME OF ws-record-name IS "name", ws-record-value IS "value",
     *           ws-record-flag IS "enabled"
     *   TYPE OF ws-record-flag IS ATTRIBUTE
     *   SUPPRESS WHEN SPACES
     */
    public String generateXml(RecordXmlDto record) {
        try {
            JAXBContext context = JAXBContext.newInstance(RecordXmlDto.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            StringWriter writer = new StringWriter();
            marshaller.marshal(record, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Error generating XML: " + e.getMessage(), e);
        }
    }

    /**
     * Convenience method to generate XML from a RecordDto by converting to RecordXmlDto.
     */
    public String generateXml(RecordDto record) {
        RecordXmlDto xmlDto = new RecordXmlDto(
                record.getName(),
                record.getValue(),
                record.getBlank(),
                String.valueOf(record.isEnabled())
        );
        return generateXml(xmlDto);
    }
}
