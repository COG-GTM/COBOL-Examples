package com.cobolmigration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for data serialization, replacing the COBOL ws-record group item:
 *   05 ws-record-name    pic x(10)
 *   05 ws-record-value   pic x(10)
 *   05 ws-record-blank   pic x(10)
 *   05 ws-record-flag    pic x(5) value "false"
 *       88 ws-record-flag-enabled  value "true"
 *       88 ws-record-flag-disabled value "false"
 *
 * Used by both json_generate.cbl and xml_generate.cbl equivalents.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RecordDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    @JsonProperty("blank")
    private String blank;

    @JsonProperty("enabled")
    private boolean flag;

    public RecordDto() {
    }

    public RecordDto(String name, String value, String blank, boolean flag) {
        this.name = name;
        this.value = value;
        this.blank = blank;
        this.flag = flag;
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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
