package com.cobolmigration.service;

import com.cobolmigration.dto.RecordXmlDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import org.springframework.stereotype.Service;

/**
 * Service generating XML from record objects with XML declaration header.
 * Replaces the COBOL XML GENERATE statement from xml_generate/xml_generate.cbl.
 *
 * <p>COBOL equivalent (xml_generate.cbl lines 41-56):
 * <pre>
 *   XML GENERATE ws-xml-output FROM ws-record
 *       COUNT IN ws-xml-char-count
 *       WITH XML-DECLARATION
 *       NAME OF ws-record-name IS "name", ...
 *       TYPE OF ws-record-flag IS ATTRIBUTE
 *       SUPPRESS WHEN SPACES
 * </pre>
 *
 * @see xml_generate/xml_generate.cbl
 */
@Service
public class XmlService {

    /**
     * Generates XML from a RecordXmlDto with XML declaration.
     * The XML declaration header matches the COBOL WITH XML-DECLARATION option.
     * The enabled field is rendered as an attribute (TYPE IS ATTRIBUTE).
     * Fields with only spaces are suppressed (SUPPRESS WHEN SPACES).
     *
     * @param record the record to serialize
     * @return the generated XML string
     * @throws JAXBException if marshalling fails
     */
    public String generateXml(RecordXmlDto record) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(RecordXmlDto.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        // WITH XML-DECLARATION: include the <?xml ...?> header
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);

        StringWriter writer = new StringWriter();
        marshaller.marshal(record, writer);
        return writer.toString();
    }

    /**
     * Generates XML and returns the character count alongside the output,
     * matching the COBOL COUNT IN ws-xml-char-count behavior.
     *
     * @param record the record to serialize
     * @return an XmlResult containing the XML string and character count
     * @throws JAXBException if marshalling fails
     */
    public XmlResult generateXmlWithCount(RecordXmlDto record) throws JAXBException {
        String xml = generateXml(record);
        return new XmlResult(xml, xml.length());
    }

    /**
     * Result holder matching COBOL's dual output of XML content and character count.
     */
    public static class XmlResult {
        private final String xml;
        private final int charCount;

        public XmlResult(String xml, int charCount) {
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
}
