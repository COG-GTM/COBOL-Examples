# COBOL Coding Standards

This document defines coding conventions and best practices for the COBOL-Examples repository. These standards are designed to ensure consistency, maintainability, and educational clarity across all example programs.

## 1. Comment Formatting and Placement

### 1.1 Header Block Format

Every COBOL source file should begin with a standard header block using traditional asterisk comments in column 7:

```cobol
******************************************************************
* Author: [Your Name]
* Date: YYYY-MM-DD
* Purpose: [Brief description of the program's purpose]
* Tectonics: [Compilation instructions, e.g., "cobc"]
******************************************************************
```

**Example from merge_sort_test.cbl:**
```cobol
******************************************************************
* author: Erik Eriksen
* date: 2021-09-19
* purpose: Testing sort and merge syntax on test data.
* tectonics: cobc
******************************************************************
```

### 1.2 Comment Styles

This repository supports two comment styles:

#### Traditional Comments (`*` in column 7)
- Used for block comments and headers
- The asterisk must be in column 7
- Commonly used for section dividers and multi-line explanations

```cobol
      * This is a traditional comment
      * spanning multiple lines
```

#### Modern Inline Comments (`*>`)
- Can appear anywhere after column 7
- Used for inline explanations and annotations
- Preferred for single-line clarifications

```cobol
       01  ws-input                     pic x(50). *> User input buffer
```

**Example from sql_example.cbl:**
```cobol
*>****************************************************************
*> Author: Erik Eriksen
*> Date: 2022-04-15
*> Purpose: Example program showing connecting and using a Postgres
*>          SQL database in an application.
*>****************************************************************
```

### 1.3 When to Use Each Style

- **Header blocks**: Use traditional `*` comments
- **Section/paragraph documentation**: Use traditional `*` comments
- **Variable descriptions**: Use inline `*>` comments when space permits
- **Statement clarifications**: Use inline `*>` comments
- **Multi-line explanations**: Use traditional `*` comments for readability

### 1.4 Comment Content Guidelines

- Keep comments concise and meaningful
- Explain "why" rather than "what" when the code is self-explanatory
- For educational examples, provide context about COBOL features being demonstrated
- Update comments when code changes
- Use proper grammar and spelling

## 2. Error Handling Patterns

### 2.1 Current Pattern (Before Improvement)

Many existing examples use a simple pattern that checks only for successful status and abruptly terminates on error:

```cobol
if ws-fs-status-merge not = "00" then
    display "Error opening merged output file: "
        ws-fs-status-merge
    end-display
    stop run
end-if
```

**Issues with this approach:**
- Abrupt termination with `STOP RUN` provides no recovery options
- Only checks for successful status ("00")
- No logging or detailed error information
- Difficult to test and debug

### 2.2 Improved Pattern (Recommended)

Use the standardized error handling from `common-utilities.cbl`:

```cobol
open input fd-merged-file

if ws-fs-status-merge not = "00" then
    move "Opening merged file" to ws-error-operation
    move ws-fs-status-merge to ws-error-file-status
    perform HANDLE-ERROR
    
    if ws-error-is-fatal then
        perform CLEANUP-AND-EXIT
    end-if
end-if
```

**Benefits of this approach:**
- Graceful error recovery instead of immediate termination
- Detailed error logging with context
- Allows for cleanup operations before exit
- Distinguishes between fatal and recoverable errors
- Easier to maintain and extend

### 2.3 File Status Variable Naming

Use consistent naming for file status variables:

- **Pattern**: `ws-fs-status-{descriptive-name}`
- **Examples**:
  - `ws-fs-status-1` (for generic file 1)
  - `ws-fs-status-merge` (for merged output file)
  - `ws-fs-status-sorted` (for sorted output file)

```cobol
01  ws-fs-status-input                  pic xx.
01  ws-fs-status-output                 pic xx.
01  ws-fs-status-master                 pic xx.
```

### 2.4 Error Handling Best Practices

1. **Always check file status** after file operations (OPEN, READ, WRITE, CLOSE)
2. **Provide context** in error messages (what operation failed, which file)
3. **Allow for cleanup** (close open files, release resources)
4. **Log errors** with sufficient detail for troubleshooting
5. **Use the common-utilities.cbl** routines for consistency
6. **Set error flags** for calling programs to check
7. **Avoid STOP RUN** unless absolutely necessary (use GOBACK or controlled exit)

### 2.5 File Status Codes Reference

The `common-utilities.cbl` module handles these standard file status codes:

- **00**: Successful operation
- **02**: Duplicate key (indexed files) - may continue with alternate key
- **10**: End of file - normal condition for read loops
- **23**: Record not found - may need graceful handling
- **30**: Permanent error - usually hardware/permission issues
- **35**: File not found - need to create or verify path

## 3. File Naming and Organization

### 3.1 File Naming Conventions

COBOL source files should follow these naming patterns:

**Preferred Format**: `lowercase-with-hyphens.cbl`
- Examples: `accept-secure.cbl`, `display-test.cbl`

**Acceptable Alternative**: `lowercase_with_underscores.cbl`
- Examples: `merge_sort_test.cbl`, `sql_example.cbl`

**Key Rules**:
- All lowercase letters
- Use hyphens or underscores for word separation (be consistent within a directory)
- Use `.cbl` extension for COBOL source files
- Use descriptive names that indicate the program's purpose
- Avoid abbreviations unless they are well-known (e.g., `sql`, `xml`, `json`)

### 3.2 Directory Organization

The repository follows a feature-based organization pattern:

```
COBOL-Examples/
├── accept/              # ACCEPT statement examples
│   ├── accept.cbl
│   ├── accept-secure.cbl
│   ├── accept_from.cbl
│   └── README.md
├── sql/                 # Database examples
│   ├── sql_example.cbl
│   ├── create_test_db.sql
│   └── README.md
├── merge_sort/          # Sorting examples
│   ├── merge_sort_test.cbl
│   └── README.md
└── common-utilities.cbl # Shared utilities (copybook)
```

**Organization Principles**:
1. Each major feature or topic gets its own directory
2. Related examples are grouped together
3. Each directory should have a `README.md` explaining the examples
4. Keep examples self-contained when possible
5. Use copybooks (like `common-utilities.cbl`) for shared code

### 3.3 README Requirements

Each example directory should include a `README.md` with:
- Brief description of what the example demonstrates
- Prerequisites (libraries, databases, etc.)
- Compilation instructions
- Execution instructions
- Expected output or behavior
- References to related COBOL features or documentation

## 4. Indentation and Spacing Rules

### 4.1 Column Alignment

COBOL has specific column requirements that must be followed:

- **Columns 1-6**: Sequence area (typically unused in modern COBOL)
- **Column 7**: Indicator area (`*` for comments, `-` for continuation)
- **Columns 8-11**: Area A (division, section, paragraph names)
- **Columns 12-72**: Area B (statements, data descriptions)

### 4.2 Indentation Standards

Use consistent indentation to improve readability:

**Division and Section Headers**: Start in Area A (column 8)
```cobol
       IDENTIFICATION DIVISION.
       PROGRAM-ID. example-program.
       
       DATA DIVISION.
       WORKING-STORAGE SECTION.
```

**Paragraph Names**: Start in Area A (column 8)
```cobol
       main-procedure.
       
       create-test-data.
       
       cleanup-and-exit.
```

**Data Declarations**: Start in Area A for level numbers, indent for clarity
```cobol
       01  ws-customer-record.
           05  ws-customer-id                  pic 9(5).
           05  ws-customer-name                pic x(50).
           05  ws-customer-address.
               10  ws-street                   pic x(30).
               10  ws-city                     pic x(20).
               10  ws-state                    pic xx.
```

**Statements**: Indent with spaces (typically 4 spaces from Area A)
```cobol
       main-procedure.
           display "Starting program"
           
           perform initialize-variables
           
           if ws-valid-input then
               perform process-data
           else
               perform display-error
           end-if
           
           goback.
```

**Nested Statements**: Add additional indentation for each nesting level
```cobol
       process-file.
           open input fd-input-file
           
           if ws-file-status = "00" then
               perform until ws-eof
                   read fd-input-file
                       at end
                           set ws-eof to true
                       not at end
                           if ws-record-valid then
                               perform process-record
                           end-if
                   end-read
               end-perform
           end-if
           
           close fd-input-file.
```

### 4.3 Spacing Guidelines

**Blank Lines**: Use blank lines to separate logical sections
```cobol
       01  ws-input-variables.
           05  ws-customer-id                  pic 9(5).
           05  ws-customer-name                pic x(50).
       
       01  ws-output-variables.
           05  ws-formatted-output             pic x(100).
       
       01  ws-control-flags.
           05  ws-eof-sw                       pic x value 'N'.
               88  ws-eof                      value 'Y'.
```

**Alignment**: Align related elements for readability
```cobol
       01  ws-record.
           05  ws-field-1                      pic 9(5).
           05  ws-field-2                      pic x(20).
           05  ws-field-3                      pic 9(3)v99.
```

**Statement Continuation**: Use proper continuation for long statements
```cobol
       display "This is a very long message that needs to be"
           " continued on the next line for better readability"
           " and to stay within recommended line lengths"
```

### 4.4 GnuCOBOL-Specific Considerations

When using GnuCOBOL (the compiler for this repository):

- Free-format source is supported but not used in these examples
- Fixed-format (traditional) is used for educational clarity
- Column rules are enforced by the compiler
- Modern features (like inline comments with `*>`) are supported
- Use `cobc` compiler flags appropriately for examples

### 4.5 Consistent Style Within Files

- Choose one indentation style (spaces) and use it consistently
- Align similar constructs vertically when it improves readability
- Keep line length reasonable (typically under 80 columns for statements)
- Be consistent with blank line usage throughout a file

## 5. Summary

Following these coding standards will help maintain consistency and quality across the COBOL-Examples repository. Remember that these examples serve an educational purpose, so clarity and readability are paramount. When in doubt, favor code that is easy to understand over code that is merely clever or compact.

For questions or suggestions about these standards, please open an issue or submit a pull request.

## References

- Example files demonstrating these standards:
  - `merge_sort/merge_sort_test.cbl` - File handling and error checking
  - `accept/accept_from.cbl` - Comment styles and documentation
  - `sql/sql_example.cbl` - Modern comment style usage
  - `sub_program/main_app.cbl` - Clean code structure
  - `report_writer/report_test.cbl` - Standard indentation

- `common-utilities.cbl` - Shared utility routines implementing these standards
