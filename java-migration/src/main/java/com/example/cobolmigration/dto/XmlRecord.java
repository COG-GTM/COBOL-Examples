package com.example.cobolmigration.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * DTO replacing the COBOL ws-record used in xml_generate/xml_generate.cbl.
 *
 * COBOL NAME OF mappings (lines 45-48):
 *   ws-record-name  IS "name"    -> @XmlElement(name = "name")
 *   ws-record-value IS "value"   -> @XmlElement(name = "value")
 *   ws-record-flag  IS "enabled" -> @XmlAttribute(name = "enabled")
 *
 * The COBOL TYPE OF ws-record-flag IS ATTRIBUTE (line 49) is replicated
 * with @XmlAttribute.
 *
 * SUPPRESS WHEN SPACES (line 50) is handled by omitting null/empty values
 * during marshalling.
 */
@XmlRootElement(name = "ws-record")
public class XmlRecord {

    private String name;
    private String value;
    private String blank;
    private String enabled;

    public XmlRecord() {
    }

    public XmlRecord(String name, String value, String blank, String enabled) {
        this.name = name;
        this.value = value;
        this.blank = blank;
        this.enabled = enabled;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @XmlElement(name = "blank", nillable = false)
    public String getBlank() {
        return (blank == null || blank.isBlank()) ? null : blank;
    }

    public void setBlank(String blank) {
        this.blank = blank;
    }

    @XmlAttribute(name = "enabled")
    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
}
