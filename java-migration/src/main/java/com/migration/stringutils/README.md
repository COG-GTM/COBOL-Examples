# Phase 1: String Utilities

## COBOL Sources
- `trim/trim.cbl` - Intrinsic TRIM function examples
- `unstring/unstring.cbl` - UNSTRING field splitting examples
- `is_numeric/is_numeric.cbl` - IS NUMERIC check examples
- `numval_test/numval_test.cbl` - FUNCTION NUMVAL conversion

## COBOL-to-Java Mapping

### FUNCTION TRIM
| COBOL | Java |
|-------|------|
| `FUNCTION TRIM(value)` | `String.strip()` / `StringUtils.trim()` |
| `FUNCTION TRIM(value LEADING)` | `String.stripLeading()` / `StringUtils.trimLeading()` |
| `FUNCTION TRIM(value TRAILING)` | `String.stripTrailing()` / `StringUtils.trimTrailing()` |

### UNSTRING ... DELIMITED BY
| COBOL | Java |
|-------|------|
| `UNSTRING src DELIMITED BY delim INTO dest1 dest2` | `StringUtils.unstring(src, delim)` |
| `UNSTRING ... DELIMITED BY ALL delim` | `StringUtils.unstringAll(src, delim)` |
| `UNSTRING ... DELIMITED BY d1 OR d2` | `StringUtils.unstringMultipleDelimiters(src, d1, d2)` |
| `TALLYING IN count` | `UnstringResult.fieldsFilled()` |
| `DELIMITER IN delim` | `UnstringResult.delimitersFound()` |
| `COUNT IN charCount` | `UnstringResult.charCounts()` |

### IS NUMERIC
| COBOL | Java |
|-------|------|
| `IF value IS NUMERIC` | `StringUtils.isNumeric(value)` |
| `IF FUNCTION TRIM(value) IS NUMERIC` | `StringUtils.isNumericTrimmed(value)` |
| Right-justify + zero-fill + IS NUMERIC | `StringUtils.isNumericZeroFilled(value, width)` |

### FUNCTION NUMVAL
| COBOL | Java |
|-------|------|
| `FUNCTION NUMVAL(value)` | `StringUtils.numval(value)` / `Double.parseDouble()` |

## Behavioral Differences

1. **Fixed-width fields**: COBOL PIC X(n) fields are always padded to n characters with spaces. Java strings are variable-length. The `isNumeric()` method preserves COBOL behavior where trailing spaces cause the check to fail.

2. **UNSTRING pointer**: COBOL UNSTRING maintains a running pointer position. Java `String.split()` processes the entire string at once. For iterative processing, use the `UnstringResult` to get individual parts.

3. **NUMVAL trailing signs**: COBOL allows trailing signs (e.g., "123-"). The `numval()` method handles this convention.
