package com.cobolmigration.service;

import com.cobolmigration.model.XmlRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for XmlGenerationService.
 * Verifies XML output matches the COBOL xml_generate example.
 */
class XmlGenerationServiceTest {

    private XmlGenerationService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new XmlGenerationService();
    }

    @Test
    void generateXml_shouldIncludeXmlDeclaration() throws Exception {
        // COBOL: WITH XML-DECLARATION
        XmlRecord record = new XmlRecord("Test Name", "Test Value", null, "true");
        String xml = service.generateXml(record);

        assertTrue(xml.startsWith("<?xml"));
    }

    @Test
    void generateXml_shouldHaveEnabledAsAttribute() throws Exception {
        // COBOL: TYPE OF ws-record-flag IS ATTRIBUTE
        XmlRecord record = new XmlRecord("Test Name", "Test Value", null, "true");
        String xml = service.generateXml(record);

        // enabled should be an XML attribute, not an element
        assertTrue(xml.contains("enabled=\"true\""));
    }

    @Test
    void generateXml_shouldSuppressBlankFields() throws Exception {
        // COBOL: SUPPRESS WHEN SPACES
        XmlRecord record = new XmlRecord("Test Name", "Test Value", null, "true");
        String xml = service.generateXml(record);

        // blank field should not appear (null -> suppressed)
        assertFalse(xml.contains("<blank"));
    }

    @Test
    void generateXml_shouldSuppressEmptyStringFields() throws Exception {
        // Also suppress empty strings (spaces in COBOL)
        XmlRecord record = new XmlRecord("Test Name", "Test Value", "   ", "true");
        String xml = service.generateXml(record);

        assertFalse(xml.contains("<blank"));
    }

    @Test
    void generateXml_shouldIncludeNonBlankFields() throws Exception {
        XmlRecord record = new XmlRecord("Test Name", "Test Value", null, "true");
        String xml = service.generateXml(record);

        assertTrue(xml.contains("<name>Test Name</name>"));
        assertTrue(xml.contains("<value>Test Value</value>"));
    }

    @Test
    void generateExampleXml_shouldMatchCobolOutput() throws Exception {
        String xml = service.generateExampleXml();

        assertNotNull(xml);
        assertTrue(xml.startsWith("<?xml"));
        assertTrue(xml.contains("enabled=\"true\""));
        assertTrue(xml.contains("<name>Test Name</name>"));
        assertTrue(xml.contains("<value>Test Value</value>"));
        assertFalse(xml.contains("<blank"));
    }
}
