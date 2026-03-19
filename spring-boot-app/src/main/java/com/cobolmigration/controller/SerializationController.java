package com.cobolmigration.controller;

import com.cobolmigration.dto.SerializationRecord;
import com.cobolmigration.service.SerializationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for serialization operations.
 *
 * <p>Replaces the COBOL JSON GENERATE functionality from
 * {@code json_generate/json_generate.cbl} and XML GENERATE from
 * {@code xml_generate/xml_generate.cbl}.</p>
 */
@RestController
@RequestMapping("/api/serialize")
public class SerializationController {

    private final SerializationService serializationService;

    public SerializationController(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    /**
     * Accepts a record and returns its JSON representation.
     *
     * <p>Replaces the JSON GENERATE statement with NAME OF mappings.</p>
     *
     * @param record the record to serialize
     * @return JSON string
     */
    @PostMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> serializeToJson(
            @RequestBody SerializationRecord record) throws JsonProcessingException {
        String json = serializationService.toJson(record);
        return ResponseEntity.ok(json);
    }

    /**
     * Accepts a record and returns its XML representation.
     *
     * <p>Replaces the XML GENERATE statement with NAME OF mappings
     * and TYPE OF attribute handling.</p>
     *
     * @param record the record to serialize
     * @return XML string
     */
    @PostMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> serializeToXml(
            @RequestBody SerializationRecord record) throws JsonProcessingException {
        String xml = serializationService.toXml(record);
        return ResponseEntity.ok(xml);
    }
}
