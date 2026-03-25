package com.cobolmigration.controller;

import com.cobolmigration.model.SerializationRecord;
import com.cobolmigration.service.JsonSerializationService;
import com.cobolmigration.service.XmlSerializationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for serialization demonstrations.
 * Replaces COBOL XML GENERATE (xml_generate/) and JSON GENERATE (json_generate/) programs.
 */
@RestController
@RequestMapping("/api/serialize")
public class SerializationController {

    private final XmlSerializationService xmlService;
    private final JsonSerializationService jsonService;

    public SerializationController(XmlSerializationService xmlService,
                                   JsonSerializationService jsonService) {
        this.xmlService = xmlService;
        this.jsonService = jsonService;
    }

    /**
     * GET /api/serialize/xml - Demonstrates XML generation.
     * Replaces xml_generate/xml_generate.cbl program execution.
     */
    @GetMapping("/xml")
    public ResponseEntity<String> generateXml() throws JsonProcessingException {
        SerializationRecord record = xmlService.createSampleRecord();
        String xml = xmlService.toXml(record);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }

    /**
     * GET /api/serialize/json - Demonstrates JSON generation.
     * Replaces json_generate/json_generate.cbl program execution.
     */
    @GetMapping("/json")
    public ResponseEntity<String> generateJson() throws JsonProcessingException {
        SerializationRecord record = jsonService.createSampleRecord();
        String json = jsonService.toJson(record);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}
