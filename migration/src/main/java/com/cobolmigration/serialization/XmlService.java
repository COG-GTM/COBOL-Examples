package com.cobolmigration.serialization;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

/**
 * XML serialization service migrated from xml_generate/xml_generate.cbl (lines 41-56).
 *
 * COBOL source:
 *   XML GENERATE ws-xml-output
 *     FROM ws-record
 *     COUNT IN ws-xml-char-count
 *     WITH XML-DECLARATION
 *     NAME OF
 *       ws-record-name IS "name",
 *       ws-record-value IS "value",
 *       ws-record-flag IS "enabled"
 *     TYPE OF ws-record-flag IS ATTRIBUTE
 *     SUPPRESS WHEN SPACES
 *     ON EXCEPTION
 *       DISPLAY "Error generating xml error " XML-CODE
 *       STOP RUN
 *     NOT ON EXCEPTION
 *       DISPLAY "XML document successfully generated."
 *   END-XML
 */
@Service
public class XmlService {

    /**
     * Record DTO matching the COBOL ws-record structure from xml_generate.cbl.
     *
     * COBOL field mappings with XML GENERATE:
     *   ws-record-name  (pic x(10))  -> @XmlElement(name="name")
     *   ws-record-value (pic x(10))  -> @XmlElement(name="value")
     *   ws-record-blank (pic x(10))  -> suppressed when spaces (SUPPRESS WHEN SPACES)
     *   ws-record-flag  (pic x(5))   -> @XmlAttribute(name="enabled")
     *     TYPE OF ws-record-flag IS ATTRIBUTE -> modeled as XML attribute
     */
    @XmlRootElement(name = "ws-record")
    public static class Record {

        private String name;
        private String value;
        private String blank;
        private String enabled;

        public Record() {
        }

        public Record(String name, String value, String blank, String enabled) {
            this.name = name;
            this.value = value;
            this.blank = blank;
            this.enabled = enabled;
        }

        @XmlElement(name = "name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlElement(name = "value")
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Returns blank field value, or null if empty/spaces only.
         * Implements COBOL SUPPRESS WHEN SPACES: if the field is all spaces,
         * it is not included in the XML output.
         */
        @XmlElement(name = "blank", nillable = false)
        public String getBlank() {
            if (blank == null || blank.trim().isEmpty()) {
                return null;
            }
            return blank;
        }

        public void setBlank(String blank) {
            this.blank = blank;
        }

        /**
         * The enabled flag is rendered as an XML attribute.
         * COBOL: TYPE OF ws-record-flag IS ATTRIBUTE
         */
        @XmlAttribute(name = "enabled")
        public String getEnabled() {
            return enabled;
        }

        public void setEnabled(String enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Result of XML generation, including the output string and character count.
     * Models COBOL's COUNT IN ws-xml-char-count.
     */
    public static class GenerateResult {
        private final String xml;
        private final int charCount;

        public GenerateResult(String xml, int charCount) {
            this.xml = xml;
            this.charCount = charCount;
        }

        public String getXml() {
            return xml;
        }

        public int getCharCount() {
            return charCount;
        }
    }

    /**
     * Generates an XML string from a Record object.
     * Equivalent to COBOL: XML GENERATE ws-xml-output FROM ws-record
     *                       WITH XML-DECLARATION
     *
     * The XML-DECLARATION is included via Marshaller.JAXB_FRAGMENT = false,
     * which produces the standard XML declaration header.
     *
     * Handles error equivalent to COBOL's ON EXCEPTION clause by
     * throwing an XmlGenerationException.
     *
     * @param record the record to serialize
     * @return GenerateResult containing the XML string and character count
     * @throws XmlGenerationException if XML generation fails (equivalent to ON EXCEPTION)
     */
    public GenerateResult generateXml(Record record) {
        try {
            JAXBContext context = JAXBContext.newInstance(Record.class);
            Marshaller marshaller = context.createMarshaller();
            // WITH XML-DECLARATION: include the XML declaration
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter writer = new StringWriter();
            marshaller.marshal(record, writer);
            String xml = writer.toString();
            return new GenerateResult(xml, xml.length());
        } catch (JAXBException e) {
            throw new XmlGenerationException("Error generating XML: " + e.getMessage(), e);
        }
    }

    /**
     * Exception equivalent to COBOL's ON EXCEPTION during XML GENERATE.
     */
    public static class XmlGenerationException extends RuntimeException {
        public XmlGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
