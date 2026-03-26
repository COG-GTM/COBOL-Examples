package com.example.cobolmigration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * POJO for JSON/XML generation mapping json_generate/json_generate.cbl
 * and xml_generate/xml_generate.cbl.
 *
 * COBOL record structure:
 *   ws-record-name   PIC X(10) — mapped to "name"
 *   ws-record-value  PIC X(10) — mapped to "value"
 *   ws-record-blank  PIC X(10) — suppressed when spaces
 *   ws-record-flag   PIC X(5)  — mapped to "enabled", XML attribute
 */
@JacksonXmlRootElement(localName = "ws-record")
public class SerializableRecord {

    @JsonProperty("name")
    @JacksonXmlProperty(localName = "name")
    private String name;

    @JsonProperty("value")
    @JacksonXmlProperty(localName = "value")
    private int value;

    @JsonProperty("enabled")
    @JacksonXmlProperty(localName = "enabled", isAttribute = true)
    private String enabled;

    public SerializableRecord() {
    }

    public SerializableRecord(String name, int value, String enabled) {
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "SerializableRecord{name='" + name + "', value=" + value
                + ", enabled='" + enabled + "'}";
    }
}
