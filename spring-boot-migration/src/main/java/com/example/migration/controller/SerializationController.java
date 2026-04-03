package com.example.migration.controller;

import com.example.migration.model.RecordDto;
import com.example.migration.service.SerializationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for JSON and XML serialization demonstrations.
 *
 * Replaces:
 *   - json_generate/json_generate.cbl: demonstrates JSON GENERATE
 *   - xml_generate/xml_generate.cbl: demonstrates XML GENERATE
 */
@RestController
@RequestMapping("/api/serialize")
public class SerializationController {

    private final SerializationService serializationService;

    public SerializationController(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    /**
     * Demonstrates JSON generation (replaces json_generate.cbl).
     * Accepts a RecordDto and returns its JSON representation.
     */
    @PostMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateJson(@RequestBody RecordDto record) {
        String json = serializationService.generateJson(record);
        return ResponseEntity.ok(json);
    }

    /**
     * Demonstrates XML generation (replaces xml_generate.cbl).
     * Accepts a RecordDto and returns its XML representation.
     */
    @PostMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> generateXml(@RequestBody RecordDto record) {
        String xml = serializationService.generateXml(record);
        return ResponseEntity.ok(xml);
    }
}
