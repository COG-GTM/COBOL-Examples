      ******************************************************************
      * Author: Devin AI
      * Date: 2024-10-08
      * Purpose: Common utility routines for COBOL examples repository.
      *          Provides standardized error handling, file status
      *          validation, and display formatting functions.
      *
      * Usage: Copy the WORKING-STORAGE variables and PROCEDURE
      *        paragraphs from this file into your program, or
      *        reference this file as a guide when implementing
      *        similar error handling in your applications.
      *
      * Note: This is a library of reusable utility routines.
      *       The paragraphs can be copied into your programs
      *       to provide consistent error handling and formatting.
      *
      * Tectonics: cobc -fsyntax-only common-utilities.cbl
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. common-utilities.
       
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       
      *> Error handling variables
       01  ws-error-operation              pic x(50).
       01  ws-error-file-status            pic xx.
       01  ws-error-message                pic x(100).
       01  ws-error-is-fatal               pic 9 value 0.
           88  ws-error-fatal              value 1.
           88  ws-error-recoverable        value 0.
       
      *> File status validation variables
       01  ws-fs-code                      pic xx.
       01  ws-fs-description               pic x(80).
       01  ws-fs-severity                  pic x(7).
       
      *> Display formatting variables
       01  ws-display-line                 pic x(80).
       01  ws-display-divider              pic x(80) value all "-".
       01  ws-display-header-line          pic x(80) value all "=".
       
       
       PROCEDURE DIVISION.
       
       main-procedure.
           display "Common Utilities Library"
           display "This program contains reusable utility routines."
           display "Copy the paragraphs you need into your program."
           goback.
       
       
      ******************************************************************
      * VALIDATE-FILE-STATUS
      * 
      * Purpose: Validates file status codes and returns descriptive
      *          messages and severity levels.
      *
      * Input:  ws-fs-code (pic xx) - File status code to validate
      * Output: ws-fs-description (pic x(80)) - Description of status
      *         ws-fs-severity (pic x(7)) - "ERROR" or "WARNING"
      *
      * Handles file status codes:
      *   00 - Successful operation
      *   02 - Duplicate key (indexed files)
      *   10 - End of file
      *   23 - Record not found
      *   30 - Permanent error
      *   35 - File not found
      *   Others - Unknown status
      ******************************************************************
       VALIDATE-FILE-STATUS.
           
           evaluate ws-fs-code
               when "00"
                   move "Successful operation" to ws-fs-description
                   move "SUCCESS" to ws-fs-severity
                   
               when "02"
                   move "Duplicate key - alternate key may be used"
                       to ws-fs-description
                   move "WARNING" to ws-fs-severity
                   
               when "10"
                   move "End of file reached" to ws-fs-description
                   move "INFO   " to ws-fs-severity
                   
               when "23"
                   move "Record not found" to ws-fs-description
                   move "WARNING" to ws-fs-severity
                   
               when "30"
                   move "Permanent error - hardware or permission issue"
                       to ws-fs-description
                   move "ERROR  " to ws-fs-severity
                   
               when "35"
                   move "File not found - check path and filename"
                       to ws-fs-description
                   move "ERROR  " to ws-fs-severity
                   
               when other
                   string "Unknown file status code: " ws-fs-code
                       delimited by size
                       into ws-fs-description
                   end-string
                   move "ERROR  " to ws-fs-severity
           end-evaluate
           
           exit paragraph.
       
       
      ******************************************************************
      * HANDLE-ERROR
      * 
      * Purpose: Provides standardized error handling with context and
      *          graceful recovery options. Logs errors and determines
      *          if the error is fatal.
      *
      * Input:  ws-error-operation (pic x(50)) - Description of operation
      *         ws-error-file-status (pic xx) - File status code
      * Output: ws-error-is-fatal (pic 9) - 1=fatal, 0=recoverable
      *         ws-error-message (pic x(100)) - Formatted error message
      *
      * This routine provides better error handling than immediate
      * STOP RUN, allowing for cleanup and graceful exit.
      ******************************************************************
       HANDLE-ERROR.
           
           move ws-error-file-status to ws-fs-code
           perform VALIDATE-FILE-STATUS
           
           display space
           display "*** ERROR OCCURRED ***"
           display "Operation: " ws-error-operation
           display "File Status: " ws-error-file-status
           display "Severity: " ws-fs-severity
           display "Description: " ws-fs-description
           display space
           
           string "ERROR: " delimited by size
               ws-error-operation delimited by space
               " - " delimited by size
               ws-fs-description delimited by size
               into ws-error-message
           end-string
           
           evaluate ws-fs-severity
               when "ERROR  "
                   set ws-error-fatal to true
               when "WARNING"
                   set ws-error-recoverable to true
                   display "Warning: Operation may continue"
               when other
                   set ws-error-recoverable to true
           end-evaluate
           
           exit paragraph.
       
       
      ******************************************************************
      * DISPLAY-HEADER
      * 
      * Purpose: Display formatted section header with borders.
      *
      * Input:  ws-display-line (pic x(80)) - Header text to display
      *
      * Example output:
      *   ========================================
      *   Program Execution Started
      *   ========================================
      ******************************************************************
       DISPLAY-HEADER.
           
           display space
           display ws-display-header-line
           display ws-display-line
           display ws-display-header-line
           display space
           
           exit paragraph.
       
       
      ******************************************************************
      * DISPLAY-ERROR-MESSAGE
      * 
      * Purpose: Display formatted error message with visual emphasis.
      *
      * Input:  ws-display-line (pic x(80)) - Error message to display
      *
      * Example output:
      *   ----------------------------------------
      *   ERROR: Failed to open input file
      *   ----------------------------------------
      ******************************************************************
       DISPLAY-ERROR-MESSAGE.
           
           display space
           display ws-display-divider
           display "ERROR: " ws-display-line
           display ws-display-divider
           display space
           
           exit paragraph.
       
       
      ******************************************************************
      * DISPLAY-SUCCESS-MESSAGE
      * 
      * Purpose: Display formatted success message.
      *
      * Input:  ws-display-line (pic x(80)) - Success message to display
      *
      * Example output:
      *   SUCCESS: File processing completed
      ******************************************************************
       DISPLAY-SUCCESS-MESSAGE.
           
           display space
           display "SUCCESS: " ws-display-line
           display space
           
           exit paragraph.
       
       
      ******************************************************************
      * DISPLAY-DIVIDER
      * 
      * Purpose: Display visual separator for output sections.
      *
      * Example output:
      *   ----------------------------------------
      ******************************************************************
       DISPLAY-DIVIDER.
           
           display ws-display-divider
           
           exit paragraph.
       
       
      ******************************************************************
      * EXAMPLE USAGE IN A COBOL PROGRAM:
      *
      * To use these utilities in your COBOL program:
      *
      * 1. Include the copybook:
      *    COPY common-utilities.
      *
      * 2. For error handling:
      *    open input fd-my-file
      *    if ws-fs-status-my-file not = "00" then
      *        move "Opening input file" to ws-error-operation
      *        move ws-fs-status-my-file to ws-error-file-status
      *        perform HANDLE-ERROR
      *        if ws-error-is-fatal then
      *            close any-open-files
      *            stop run
      *        end-if
      *    end-if
      *
      * 3. For file status validation:
      *    move ws-fs-status-my-file to ws-fs-code
      *    perform VALIDATE-FILE-STATUS
      *    display "Status: " ws-fs-description
      *
      * 4. For formatted output:
      *    move "Program Starting" to ws-display-line
      *    perform DISPLAY-HEADER
      *    
      *    move "File processed successfully" to ws-display-line
      *    perform DISPLAY-SUCCESS-MESSAGE
      *    
      *    perform DISPLAY-DIVIDER
      ******************************************************************
