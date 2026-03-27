package com.example.cobolmigration.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * POJO matching the COBOL ws-record structure from json_generate/json_generate.cbl (lines 27-33)
 * and xml_generate/xml_generate.cbl (lines 26-32).
 *
 * COBOL fields:
 *   ws-record-name  pic x(10)
 *   ws-record-value pic x(10)
 *   ws-record-blank pic x(10)  — suppressed when spaces in XML (xml_generate.cbl line 50)
 *   ws-record-flag  pic x(5)   — "enabled" attribute in XML (xml_generate.cbl line 49)
 *
 * In XML output, 'enabled' is rendered as an attribute (type of ws-record-flag is attribute),
 * and blank fields are suppressed (suppress when spaces).
 */
@JacksonXmlRootElement(localName = "ws-record")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SerializableRecord {

    @JsonProperty("name")
    @JacksonXmlProperty(localName = "name")
    private String name;

    @JsonProperty("value")
    @JacksonXmlProperty(localName = "value")
    private String value;

    @JsonProperty("enabled")
    @JacksonXmlProperty(isAttribute = true, localName = "enabled")
    private boolean enabled;

    public SerializableRecord() {
    }

    public SerializableRecord(String name, String value, boolean enabled) {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
