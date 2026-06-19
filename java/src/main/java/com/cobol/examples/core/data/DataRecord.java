package com.cobol.examples.core.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Record serialised by the JSON/XML examples
 * ({@code json_generate/json_generate.cbl} and
 * {@code xml_generate/xml_generate.cbl}).
 *
 * <p>The COBOL {@code JSON GENERATE} / {@code XML GENERATE} verbs renamed fields
 * via {@code NAME OF} and, for XML, emitted the flag as an attribute. The same
 * mapping is declared here with Jackson annotations.
 */
@JsonPropertyOrder({"name", "value", "enabled"})
@JacksonXmlRootElement(localName = "record")
public record DataRecord(
        @JacksonXmlProperty(localName = "name") String name,
        @JacksonXmlProperty(localName = "value") String value,
        @JacksonXmlProperty(localName = "enabled", isAttribute = true) boolean enabled) {
}
