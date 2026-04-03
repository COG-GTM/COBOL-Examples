package com.cobolmigration.service;

import com.cobolmigration.model.SerializableRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for XmlGeneratorService - validates migration of xml_generate/xml_generate.cbl.
 */
class XmlGeneratorServiceTest {

    private XmlGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new XmlGeneratorService();
    }

    @Test
    void generateXml_containsXmlDeclaration() {
        SerializableRecord record = new SerializableRecord("Test Name", "Test Value", "true");
        String xml = service.generateXml(record);
        assertTrue(xml.startsWith("<?xml"));
    }

    @Test
    void generateXml_enabledIsAttribute() {
        // COBOL: TYPE OF ws-record-flag IS ATTRIBUTE (xml_generate.cbl line 49)
        SerializableRecord record = new SerializableRecord("Test Name", "Test Value", "true");
        String xml = service.generateXml(record);
        assertTrue(xml.contains("enabled=\"true\""));
    }

    @Test
    void generateXml_suppressesEmptyFields() {
        // COBOL: SUPPRESS WHEN SPACES (xml_generate.cbl line 50)
        SerializableRecord record = new SerializableRecord("Name", "Value", "", "true");
        String xml = service.generateXml(record);
        assertFalse(xml.contains("ws-record-blank"));
    }

    @Test
    void generateXml_containsNameAndValue() {
        SerializableRecord record = new SerializableRecord("Test Name", "Test Value", "true");
        String xml = service.generateXml(record);
        assertTrue(xml.contains("<name>Test Name</name>"));
        assertTrue(xml.contains("<value>Test Value</value>"));
    }

    @Test
    void generateXml_rootElementIsWsRecord() {
        SerializableRecord record = new SerializableRecord("Test", "Val", "false");
        String xml = service.generateXml(record);
        assertTrue(xml.contains("<ws-record"));
        assertTrue(xml.contains("</ws-record>"));
    }

    @Test
    void getXmlCharCount_returnsCorrectLength() {
        String xml = "<test>value</test>";
        assertEquals(18, service.getXmlCharCount(xml));
    }

    @Test
    void getXmlCharCount_nullReturnsZero() {
        assertEquals(0, service.getXmlCharCount(null));
    }

    @Test
    void generateXml_matchesExpectedStructure() {
        SerializableRecord record = new SerializableRecord("Test Name", "Test Value", "true");
        String xml = service.generateXml(record);

        // Verify overall structure
        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("<ws-record"));
        assertTrue(xml.contains("enabled=\"true\""));
        assertTrue(xml.contains("<name>Test Name</name>"));
        assertTrue(xml.contains("<value>Test Value</value>"));
        assertTrue(xml.contains("</ws-record>"));
    }
}
