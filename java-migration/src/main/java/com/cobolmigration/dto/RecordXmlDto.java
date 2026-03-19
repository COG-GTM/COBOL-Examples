package com.cobolmigration.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * DTO with JAXB annotations for XML serialization, mapping the COBOL XML GENERATE
 * from xml_generate/xml_generate.cbl.
 *
 * <p>COBOL XML mapping (xml_generate.cbl lines 45-50):
 * <ul>
 *   <li>ws-record-name IS "name" &rarr; {@code @XmlElement(name = "name")}</li>
 *   <li>ws-record-value IS "value" &rarr; {@code @XmlElement(name = "value")}</li>
 *   <li>ws-record-blank (SUPPRESS WHEN SPACES) &rarr; null check in marshalling</li>
 *   <li>ws-record-flag IS "enabled", TYPE IS ATTRIBUTE &rarr;
 *       {@code @XmlAttribute(name = "enabled")}</li>
 * </ul>
 *
 * @see xml_generate/xml_generate.cbl
 */
@XmlRootElement(name = "ws-record")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordXmlDto {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "value")
    private String value;

    @XmlElement(name = "blank", nillable = false)
    private String blank;

    @XmlAttribute(name = "enabled")
    private String enabled;

    public RecordXmlDto() {
    }

    public RecordXmlDto(String name, String value, String blank, String enabled) {
        this.name = name;
        this.value = value;
        // SUPPRESS WHEN SPACES: if blank is all spaces or empty, set to null
        this.blank = (blank != null && !blank.trim().isEmpty()) ? blank : null;
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
        this.blank = (blank != null && !blank.trim().isEmpty()) ? blank : null;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
}
