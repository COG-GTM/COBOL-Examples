package com.cobolmigration.controller;

import com.cobolmigration.dto.RecordDto;
import com.cobolmigration.service.SerializationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for data serialization endpoints.
 * Replaces the COBOL programs json_generate.cbl and xml_generate.cbl
 * which serialized ws-record data to JSON and XML formats respectively.
 */
@RestController
@RequestMapping("/api/serialize")
public class SerializationController {

    private final SerializationService serializationService;

    public SerializationController(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @PostMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> toJson(@RequestBody RecordDto record) {
        return ResponseEntity.ok(serializationService.toJson(record));
    }

    @PostMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> toXml(@RequestBody RecordDto record) {
        return ResponseEntity.ok(serializationService.toXml(record));
    }
}
