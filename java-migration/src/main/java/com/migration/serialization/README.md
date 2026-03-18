# Phase 3: Serialization (XML/JSON)

## COBOL Sources
- `json_generate/json_generate.cbl` - JSON GENERATE with libjson-c
- `xml_generate/xml_generate.cbl` - XML GENERATE with libxml2

## COBOL-to-Java Mapping

| COBOL | Java |
|-------|------|
| `JSON GENERATE ws-output FROM ws-record` | `objectMapper.writeValueAsString(record)` |
| `XML GENERATE ws-output FROM ws-record` | `xmlMapper.writeValueAsString(record)` |
| `COUNT IN ws-char-count` | `json.length()` |
| `NAME OF ws-field IS "alias"` | `@JsonProperty("alias")` / `@JacksonXmlProperty` |
| `TYPE OF ws-field IS ATTRIBUTE` | `@JacksonXmlProperty(isAttribute = true)` |
| `SUPPRESS WHEN SPACES` | `@JsonInclude(JsonInclude.Include.NON_EMPTY)` |
| `WITH XML-DECLARATION` | `ToXmlGenerator.Feature.WRITE_XML_DECLARATION` |
| `ON EXCEPTION` | `try/catch JsonProcessingException` |

## Record Structure

The `CobolRecord` Java record maps the COBOL `ws-record` group:

```
COBOL:                          Java:
ws-record-name  PIC X(10)  ->  @JsonProperty("name") String name
ws-record-value PIC X(10)  ->  @JsonProperty("value") String value
ws-record-blank PIC X(10)  ->  String blank (suppressed when empty)
ws-record-flag  PIC X(5)   ->  @JsonProperty("enabled") String enabled
```

## Behavioral Differences

1. **Library dependencies**: COBOL requires libjson-c and libxml2 to be compiled into GnuCOBOL. Java uses Jackson, included as a Maven dependency.

2. **Field naming**: COBOL uses NAME OF clause for aliasing. Java uses annotations.

3. **Null/empty handling**: COBOL SUPPRESS WHEN SPACES omits blank fields. Jackson's `NON_EMPTY` inclusion achieves the same effect.

4. **XML attributes**: COBOL TYPE OF ... IS ATTRIBUTE maps to Jackson's `isAttribute = true`.
