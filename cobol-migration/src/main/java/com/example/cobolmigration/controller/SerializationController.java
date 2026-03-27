package com.example.cobolmigration.controller;

import com.example.cobolmigration.model.SerializableRecord;
import com.example.cobolmigration.service.SerializationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints replacing json_generate/json_generate.cbl and xml_generate/xml_generate.cbl.
 */
@RestController
@RequestMapping("/api/serialize")
public class SerializationController {

    private final SerializationService serializationService;
    private final XmlMapper xmlMapper;

    public SerializationController(SerializationService serializationService) {
        this.serializationService = serializationService;
        this.xmlMapper = new XmlMapper();
    }

    /**
     * Returns JSON — replaces json_generate.cbl.
     * JSON GENERATE ws-json-output FROM ws-record.
     */
    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SerializableRecord> getJson() {
        return ResponseEntity.ok(serializationService.getDefaultRecord());
    }

    /**
     * Returns XML with declaration — replaces xml_generate.cbl.
     * XML GENERATE ws-xml-output FROM ws-record WITH XML-DECLARATION.
     *
     * The COBOL XML output includes:
     *   - XML declaration (WITH XML-DECLARATION)
     *   - 'enabled' as an attribute (TYPE OF ws-record-flag IS ATTRIBUTE)
     *   - Blank fields suppressed (SUPPRESS WHEN SPACES)
     */
    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getXml() throws JsonProcessingException {
        SerializableRecord record = serializationService.getDefaultRecord();
        String xmlContent = xmlMapper.writeValueAsString(record);
        String xmlWithDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlContent;
        return ResponseEntity.ok(xmlWithDeclaration);
    }
}
