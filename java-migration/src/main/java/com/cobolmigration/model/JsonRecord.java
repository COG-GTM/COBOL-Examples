package com.cobolmigration.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for JSON serialization with Jackson.
 *
 * Migrated from: json_generate/json_generate.cbl (lines 27-33)
 *
 * Original COBOL record structure:
 *   01  ws-record.
 *       05  ws-record-name   PIC X(10).
 *       05  ws-record-value  PIC X(10).
 *       05  ws-record-blank  PIC X(10).
 *       05  ws-record-flag   PIC X(5) VALUE "false".
 *
 * The COBOL JSON GENERATE renames fields:
 *   ws-record-name  -> "name"
 *   ws-record-value -> "value"
 *   ws-record-flag  -> "enabled"
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JsonRecord {

    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    @JsonProperty("blank")
    private String blank;

    @JsonProperty("enabled")
    private String enabled;

    public JsonRecord() {
        this.enabled = "false";
    }

    public JsonRecord(String name, String value, String blank, String enabled) {
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
