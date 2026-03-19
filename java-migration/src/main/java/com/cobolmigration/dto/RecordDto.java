package com.cobolmigration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO with Jackson annotations mapping the COBOL NAME OF renames from
 * json_generate/json_generate.cbl.
 *
 * <p>COBOL field mapping (json_generate.cbl lines 45-48):
 * <ul>
 *   <li>ws-record-name IS "name" &rarr; {@code @JsonProperty("name")}</li>
 *   <li>ws-record-value IS "value" &rarr; {@code @JsonProperty("value")}</li>
 *   <li>ws-record-blank (suppressed when spaces) &rarr;
 *       {@code @JsonInclude(JsonInclude.Include.NON_EMPTY)}</li>
 *   <li>ws-record-flag IS "enabled" &rarr; {@code @JsonProperty("enabled")}</li>
 * </ul>
 *
 * @see json_generate/json_generate.cbl
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RecordDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    @JsonProperty("blank")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String blank;

    @JsonProperty("enabled")
    private String enabled;

    public RecordDto() {
    }

    public RecordDto(String name, String value, String blank, String enabled) {
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
