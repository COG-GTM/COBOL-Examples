package com.cobolmigration.service;

import com.cobolmigration.dto.RecordXmlDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for XmlService validating XML generation matching xml_generate/xml_generate.cbl.
 */
class XmlServiceTest {

    private final XmlService xmlService = new XmlService();

    @Test
    @DisplayName("generateXml includes XML declaration header (WITH XML-DECLARATION)")
    void testXmlDeclaration() throws Exception {
        RecordXmlDto record = new RecordXmlDto("Test Name", "Test Value", null, "true");

        String xml = xmlService.generateXml(record);

        assertThat(xml).startsWith("<?xml");
        assertThat(xml).contains("version=\"1.0\"");
    }

    @Test
    @DisplayName("generateXml renders enabled as attribute (TYPE IS ATTRIBUTE)")
    void testEnabledAsAttribute() throws Exception {
        RecordXmlDto record = new RecordXmlDto("Test Name", "Test Value", null, "true");

        String xml = xmlService.generateXml(record);

        // enabled should be an XML attribute, not an element
        assertThat(xml).contains("enabled=\"true\"");
        assertThat(xml).doesNotContain("<enabled>");
    }

    @Test
    @DisplayName("generateXml suppresses blank field when spaces (SUPPRESS WHEN SPACES)")
    void testSuppressWhenSpaces() throws Exception {
        RecordXmlDto record = new RecordXmlDto("Test Name", "Test Value", "          ", "true");

        String xml = xmlService.generateXml(record);

        // Blank field with only spaces should be suppressed (set to null in constructor)
        assertThat(xml).doesNotContain("<blank>");
    }

    @Test
    @DisplayName("generateXml includes blank field when non-empty")
    void testBlankFieldPresent() throws Exception {
        RecordXmlDto record = new RecordXmlDto("Name", "Value", "NonBlank", "false");

        String xml = xmlService.generateXml(record);

        assertThat(xml).contains("<blank>NonBlank</blank>");
    }

    @Test
    @DisplayName("generateXml includes name and value elements with renamed tags")
    void testElementNames() throws Exception {
        RecordXmlDto record = new RecordXmlDto("Test Name", "Test Value", null, "true");

        String xml = xmlService.generateXml(record);

        assertThat(xml).contains("<name>");
        assertThat(xml).contains("Test Name");
        assertThat(xml).contains("<value>");
        assertThat(xml).contains("Test Value");
    }

    @Test
    @DisplayName("generateXmlWithCount returns character count matching COBOL COUNT IN")
    void testGenerateXmlWithCount() throws Exception {
        RecordXmlDto record = new RecordXmlDto("Test Name", "Test Value", null, "true");

        XmlService.XmlResult result = xmlService.generateXmlWithCount(record);

        assertThat(result.getXml()).isNotEmpty();
        assertThat(result.getCharCount()).isEqualTo(result.getXml().length());
        assertThat(result.getCharCount()).isGreaterThan(0);
    }
}
