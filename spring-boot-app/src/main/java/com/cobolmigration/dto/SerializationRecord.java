package com.cobolmigration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * DTO for serialization operations.
 *
 * <p>Maps to the COBOL record in {@code json_generate/json_generate.cbl}
 * (lines 27-33) and {@code xml_generate/xml_generate.cbl} (lines 26-32):</p>
 * <pre>
 *   01  ws-record.
 *       05  ws-record-name    pic x(10).
 *       05  ws-record-value   pic x(10).
 *       05  ws-record-blank   pic x(10).
 *       05  ws-record-flag    pic x(5) value "false".
 * </pre>
 *
 * <p>The COBOL {@code NAME OF} mappings are handled via Jackson annotations:</p>
 * <ul>
 *   <li>ws-record-name &rarr; "name"</li>
 *   <li>ws-record-value &rarr; "value"</li>
 *   <li>ws-record-flag &rarr; "enabled"</li>
 * </ul>
 */
@JacksonXmlRootElement(localName = "ws-record")
public class SerializationRecord {

    @JsonProperty("name")
    @JacksonXmlProperty(localName = "name")
    private String name;

    @JsonProperty("value")
    @JacksonXmlProperty(localName = "value")
    private String value;

    @JsonProperty("enabled")
    @JacksonXmlProperty(isAttribute = true, localName = "enabled")
    private String enabled;

    public SerializationRecord() {
    }

    public SerializationRecord(String name, String value, String enabled) {
        this.name = name;
        this.value = value;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
}
