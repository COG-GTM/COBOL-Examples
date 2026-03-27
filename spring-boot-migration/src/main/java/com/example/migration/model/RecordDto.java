package com.example.migration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO replacing the COBOL ws-record structure from json_generate/json_generate.cbl (lines 27-33).
 *
 * COBOL field mapping:
 *   ws-record-name  (pic x(10))  -> name   (@JsonProperty("name"))
 *   ws-record-value (pic x(10))  -> value  (@JsonProperty("value"))
 *   ws-record-blank (pic x(10))  -> blank  (suppressed when empty in XML via SUPPRESS WHEN SPACES)
 *   ws-record-flag  (pic x(5))   -> enabled (@JsonProperty("enabled"))
 *
 * The NAME OF clause mappings in COBOL (json_generate.cbl lines 42-54) become @JsonProperty annotations.
 */
public class RecordDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    @JsonIgnore
    private String blank;

    @JsonProperty("enabled")
    private boolean enabled;

    public RecordDto() {
    }

    public RecordDto(String name, String value, String blank, boolean enabled) {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
