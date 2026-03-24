package com.cobolmigration.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * XML-specific DTO with JAXB annotations for XML serialization.
 * Replaces the COBOL XML GENERATE statement behavior from xml_generate.cbl:
 *   xml generate ws-xml-output from ws-record
 *       name of ws-record-name is "name",
 *              ws-record-value is "value",
 *              ws-record-flag is "enabled"
 *       type of ws-record-flag is attribute
 *       suppress when spaces
 *
 * The 'enabled' field is rendered as an XML attribute (type of ws-record-flag is attribute).
 * Blank/empty fields are suppressed (suppress when spaces).
 */
@XmlRootElement(name = "ws-record")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordXmlDto {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "value")
    private String value;

    @XmlElement(name = "ws-record-blank")
    private String blank;

    @XmlAttribute(name = "enabled")
    private String enabled;

    public RecordXmlDto() {
    }

    public RecordXmlDto(String name, String value, String blank, boolean flag) {
        this.name = name;
        this.value = value;
        this.blank = blank;
        this.enabled = flag ? "true" : "false";
    }

    public static RecordXmlDto fromRecordDto(RecordDto dto) {
        RecordXmlDto xmlDto = new RecordXmlDto();
        xmlDto.name = dto.getName();
        xmlDto.value = dto.getValue();
        xmlDto.blank = (dto.getBlank() != null && !dto.getBlank().isBlank()) ? dto.getBlank() : null;
        xmlDto.enabled = dto.isFlag() ? "true" : "false";
        return xmlDto;
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
