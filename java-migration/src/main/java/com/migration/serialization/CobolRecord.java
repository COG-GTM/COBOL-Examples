package com.migration.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Java record matching the COBOL ws-record structure used in both
 * json_generate.cbl and xml_generate.cbl.
 *
 * COBOL:
 *   01 ws-record.
 *       05 ws-record-name    PIC X(10).
 *       05 ws-record-value   PIC X(10).
 *       05 ws-record-blank   PIC X(10).
 *       05 ws-record-flag    PIC X(5) VALUE "false".
 *
 * COBOL NAME OF clause maps field names:
 *   ws-record-name  -> "name"
 *   ws-record-value -> "value"
 *   ws-record-flag  -> "enabled"
 *
 * COBOL XML: TYPE OF ws-record-flag IS ATTRIBUTE
 *   -> The "enabled" field becomes an XML attribute instead of element.
 *
 * COBOL XML: SUPPRESS WHEN SPACES
 *   -> Blank fields are omitted from output.
 */
@JacksonXmlRootElement(localName = "ws-record")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CobolRecord(
        @JsonProperty("name")
        @JacksonXmlProperty(localName = "name")
        String name,

        @JsonProperty("value")
        @JacksonXmlProperty(localName = "value")
        String value,

        @JsonProperty("ws-record-blank")
        @JacksonXmlProperty(localName = "ws-record-blank")
        String blank,

        @JsonProperty("enabled")
        @JacksonXmlProperty(localName = "enabled", isAttribute = true)
        String enabled
) {}
