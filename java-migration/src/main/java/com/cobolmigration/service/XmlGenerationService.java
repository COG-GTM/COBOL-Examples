package com.cobolmigration.service;

import com.cobolmigration.model.XmlRecord;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

/**
 * Service for generating XML from record structures.
 *
 * Migrated from: xml_generate/xml_generate.cbl
 *
 * The original COBOL program uses XML GENERATE with:
 *   - XML declaration header (WITH XML-DECLARATION)
 *   - Field renaming (NAME OF ... IS ...)
 *   - TYPE OF ws-record-flag IS ATTRIBUTE (flag becomes XML attribute)
 *   - SUPPRESS WHEN SPACES (empty fields are omitted)
 */
@Service
public class XmlGenerationService {

    private final JAXBContext jaxbContext;

    public XmlGenerationService() throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(XmlRecord.class);
    }

    /**
     * Serializes an XmlRecord to an XML string with declaration header.
     * Empty/blank fields are suppressed (not included in output).
     *
     * @param record the record to serialize
     * @return XML string with declaration
     * @throws JAXBException if marshalling fails
     */
    public String generateXml(XmlRecord record) throws JAXBException {
        // Suppress blank fields by nullifying them before marshalling
        XmlRecord cleaned = suppressBlanks(record);

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        // Include XML declaration (matches COBOL's WITH XML-DECLARATION)
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);

        StringWriter writer = new StringWriter();
        marshaller.marshal(cleaned, writer);
        return writer.toString();
    }

    /**
     * Convenience method matching the COBOL example's test data.
     * Creates a record with name="Test Name", value="Test Value", enabled="true",
     * and blank field suppressed.
     *
     * @return XML string of the example record
     * @throws JAXBException if marshalling fails
     */
    public String generateExampleXml() throws JAXBException {
        XmlRecord record = new XmlRecord("Test Name", "Test Value", null, "true");
        return generateXml(record);
    }

    /**
     * Suppresses blank/empty fields by setting them to null.
     * Mirrors COBOL's SUPPRESS WHEN SPACES behavior.
     */
    private XmlRecord suppressBlanks(XmlRecord original) {
        XmlRecord cleaned = new XmlRecord();
        cleaned.setName(isBlank(original.getName()) ? null : original.getName());
        cleaned.setValue(isBlank(original.getValue()) ? null : original.getValue());
        cleaned.setBlank(isBlank(original.getBlank()) ? null : original.getBlank());
        cleaned.setEnabled(original.getEnabled());
        return cleaned;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
