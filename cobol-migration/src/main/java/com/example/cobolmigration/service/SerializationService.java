package com.example.cobolmigration.service;

import com.example.cobolmigration.model.SerializableRecord;
import org.springframework.stereotype.Service;

/**
 * Service for serialization operations.
 * Replaces json_generate/json_generate.cbl and xml_generate/xml_generate.cbl.
 *
 * The COBOL programs create a ws-record with name="Test Name", value="Test Value",
 * enabled=true (flag), and generate JSON/XML output from it.
 */
@Service
public class SerializationService {

    /**
     * Creates the default record matching the COBOL test data:
     *   move "Test Name" to ws-record-name     (json_generate.cbl line 38)
     *   move "Test Value" to ws-record-value    (json_generate.cbl line 39)
     *   set ws-record-flag-enabled to true      (json_generate.cbl line 40)
     */
    public SerializableRecord getDefaultRecord() {
        return new SerializableRecord("Test Name", "Test Value", true);
    }
}
