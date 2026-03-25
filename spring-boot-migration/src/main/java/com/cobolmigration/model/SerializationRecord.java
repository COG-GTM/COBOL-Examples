package com.cobolmigration.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model class for XML/JSON serialization.
 * Maps COBOL field names from xml_generate/xml_generate.cbl (lines 41-56)
 * and json_generate/json_generate.cbl (lines 42-54):
 *   ws-record-name  -> "name"
 *   ws-record-value -> "value"
 *   ws-record-blank -> (suppressed when empty, via @JsonInclude)
 *   ws-record-flag  -> "enabled" (XML attribute in XML output)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JacksonXmlRootElement(localName = "ws-record")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SerializationRecord {

    @JsonProperty("name")
    @JacksonXmlProperty(localName = "name")
    private String name;

    @JsonProperty("value")
    @JacksonXmlProperty(localName = "value")
    private String value;

    @JsonProperty("blank")
    @JacksonXmlProperty(localName = "blank")
    private String blank;

    @JsonProperty("enabled")
    @JacksonXmlProperty(isAttribute = true, localName = "enabled")
    private String enabled;
}
