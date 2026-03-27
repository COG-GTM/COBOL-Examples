package com.example.migration.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * XML-specific DTO replacing the COBOL ws-record structure for XML generation
 * from xml_generate/xml_generate.cbl (lines 26-32).
 *
 * Key COBOL XML directives mapped:
 *   NAME OF ws-record-name IS "name"   -> @XmlElement(name = "name")
 *   NAME OF ws-record-value IS "value" -> @XmlElement(name = "value")
 *   NAME OF ws-record-flag IS "enabled"-> @XmlAttribute(name = "enabled")
 *   TYPE OF ws-record-flag IS ATTRIBUTE-> @XmlAttribute (line 49 of xml_generate.cbl)
 *   SUPPRESS WHEN SPACES               -> SuppressWhenSpacesAdapter (line 50 of xml_generate.cbl)
 */
@XmlRootElement(name = "ws-record")
public class RecordXmlDto {

    private String name;
    private String value;
    private String blank;
    private String enabled;

    public RecordXmlDto() {
    }

    public RecordXmlDto(String name, String value, String blank, String enabled) {
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

    @XmlElement(name = "blank")
    @XmlJavaTypeAdapter(SuppressWhenSpacesAdapter.class)
    public String getBlank() {
        return blank;
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
