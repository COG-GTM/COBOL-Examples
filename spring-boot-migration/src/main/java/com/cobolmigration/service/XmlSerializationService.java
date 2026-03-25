package com.cobolmigration.service;

import com.cobolmigration.model.SerializationRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.stereotype.Service;

/**
 * Service replacing COBOL XML GENERATE in xml_generate/xml_generate.cbl.
 * Uses Jackson XmlMapper for serialization.
 *
 * COBOL mapping (lines 41-56):
 *   XML GENERATE ws-xml-output FROM ws-record
 *     NAME OF ws-record-name IS "name"
 *     NAME OF ws-record-value IS "value"
 *     NAME OF ws-record-flag IS "enabled"
 *     TYPE OF ws-record-flag IS ATTRIBUTE
 *     SUPPRESS WHEN SPACES
 */
@Service
public class XmlSerializationService {

    private final XmlMapper xmlMapper;

    public XmlSerializationService() {
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    /**
     * Serializes a SerializationRecord to XML string.
     * Replaces XML GENERATE statement in xml_generate.cbl (lines 41-56).
     *
     * @param record the record to serialize
     * @return XML string representation
     * @throws JsonProcessingException if serialization fails
     */
    public String toXml(SerializationRecord record) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(record);
    }

    /**
     * Creates a sample record with test data matching xml_generate.cbl (lines 37-39).
     * COBOL sets: ws-record-name = "Test Name", ws-record-value = "Test Value",
     * ws-record-flag = "true" (enabled), ws-record-blank = spaces (suppressed).
     *
     * @return sample SerializationRecord
     */
    public SerializationRecord createSampleRecord() {
        return SerializationRecord.builder()
                .name("Test Name")
                .value("Test Value")
                .blank(null)
                .enabled("true")
                .build();
    }
}
