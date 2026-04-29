package com.example.cobolmigration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO replacing the COBOL ws-record used in json_generate/json_generate.cbl.
 *
 * The COBOL NAME OF clause mappings (lines 45-48) are replicated with
 * {@code @JsonProperty} annotations:
 *   ws-record-name  IS "name"    -> @JsonProperty("name")
 *   ws-record-value IS "value"   -> @JsonProperty("value")
 *   ws-record-flag  IS "enabled" -> @JsonProperty("enabled")
 *
 * ws-record-blank is suppressed when empty (SUPPRESS WHEN SPACES equivalent).
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
