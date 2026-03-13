package com.cobolmigration.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * POJO for XML serialization with JAXB.
 *
 * Migrated from: xml_generate/xml_generate.cbl (lines 26-32)
 *
 * Original COBOL record structure:
 *   01  ws-record.
 *       05  ws-record-name   PIC X(10).
 *       05  ws-record-value  PIC X(10).
 *       05  ws-record-blank  PIC X(10).
 *       05  ws-record-flag   PIC X(5) VALUE "false".
 *
 * The COBOL XML GENERATE:
 *   - Renames ws-record-name  -> "name"
 *   - Renames ws-record-value -> "value"
 *   - Renames ws-record-flag  -> "enabled"
 *   - Makes ws-record-flag an XML ATTRIBUTE
 *   - Suppresses empty/blank fields (SUPPRESS WHEN SPACES)
 *   - Includes XML declaration header
 */
@XmlRootElement(name = "ws-record")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlRecord {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "value")
    private String value;

    @XmlElement(name = "blank")
    private String blank;

    @XmlAttribute(name = "enabled")
    private String enabled;

    public XmlRecord() {
        this.enabled = "false";
    }

    public XmlRecord(String name, String value, String blank, String enabled) {
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
}
