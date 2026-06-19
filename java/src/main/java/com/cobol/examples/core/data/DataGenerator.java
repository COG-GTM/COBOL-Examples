package com.cobol.examples.core.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Replaces COBOL's {@code JSON GENERATE} / {@code XML GENERATE} with Jackson,
 * the de-facto standard for serialisation in modern Java.
 */
public final class DataGenerator {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final XmlMapper XML = new XmlMapper();

    private DataGenerator() {
    }

    public static String toJson(DataRecord record) {
        try {
            return JSON.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to generate JSON", e);
        }
    }

    public static String toXml(DataRecord record) {
        try {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + XML.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to generate XML", e);
        }
    }
}
