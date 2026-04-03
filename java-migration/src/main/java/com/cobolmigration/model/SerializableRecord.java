package com.cobolmigration.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Maps the COBOL WORKING-STORAGE record from json_generate/json_generate.cbl lines 27-33
 * and xml_generate/xml_generate.cbl lines 26-32.
 * Fields: ws-record-name, ws-record-value, ws-record-blank, ws-record-flag
 */
@JacksonXmlRootElement(localName = "ws-record")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SerializableRecord {

    @JsonProperty("name")
    @JacksonXmlProperty(localName = "name")
    private String name;        // pic x(10)

    @JsonProperty("value")
    @JacksonXmlProperty(localName = "value")
    private String value;       // pic x(10)

    @JsonProperty("ws-record-blank")
    @JacksonXmlProperty(localName = "ws-record-blank")
    private String blank;       // pic x(10) - suppressed when spaces

    @JsonProperty("enabled")
    @JacksonXmlProperty(localName = "enabled", isAttribute = true)
    private String enabled;     // pic x(5) - "true" or "false"

    public SerializableRecord() {
    }

    public SerializableRecord(String name, String value, String enabled) {
        this.name = name;
        this.value = value;
        this.enabled = enabled;
    }

    public SerializableRecord(String name, String value, String blank, String enabled) {
        this.name = name;
        this.value = value;
        this.blank = blank;
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

    public String getBlank() {
        return blank;
    }

    public void setBlank(String blank) {
        this.blank = blank;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "SerializableRecord{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", blank='" + blank + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }
}
