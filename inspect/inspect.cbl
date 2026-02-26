      ******************************************************************
      * author: Devin AI
      * date: 2026-02-26
      * purpose: INSPECT verb examples for character counting and
      *          replacement.
      * tectonics: cobc
      ******************************************************************
       identification division.
       program-id. inspect-example.

       data division.
       file section.

       working-storage section.

       01  ws-source-str              pic x(30).
       01  ws-tally-count             pic 99 value 0.

       01  ws-replacing-str           pic x(30).
       01  ws-converting-str          pic x(30).

       procedure division.

       main-procedure.

      *> ============================================================
      *> EXAMPLE 1: INSPECT TALLYING - CHARACTERS
      *> Count total characters in the string.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 1 : INSPECT TALLYING - ALL CHARACTERS"
           display space

           move "Hello World" to ws-source-str
           move 0 to ws-tally-count

           inspect ws-source-str
               tallying ws-tally-count
               for characters

           display "SOURCE STRING: " ws-source-str
           display "TOTAL CHARACTERS: " ws-tally-count


      *> ============================================================
      *> EXAMPLE 2: INSPECT TALLYING - ALL specific character
      *> Count how many times a specific character appears.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 2 : INSPECT TALLYING - ALL 'l'"
           display space

           move "Hello World" to ws-source-str
           move 0 to ws-tally-count

           inspect ws-source-str
               tallying ws-tally-count
               for all "l"

           display "SOURCE STRING: " ws-source-str
           display "COUNT OF 'l': " ws-tally-count


      *> ============================================================
      *> EXAMPLE 3: INSPECT TALLYING - LEADING characters
      *> Count leading occurrences of a character.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 3 : INSPECT TALLYING - LEADING SPACES"
           display space

           move "   Hello World" to ws-source-str
           move 0 to ws-tally-count

           inspect ws-source-str
               tallying ws-tally-count
               for leading spaces

           display "SOURCE STRING: --" ws-source-str "--"
           display "LEADING SPACES: " ws-tally-count


      *> ============================================================
      *> EXAMPLE 4: INSPECT TALLYING with BEFORE/AFTER
      *> Count characters before or after a specific character.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 4 : INSPECT TALLYING - BEFORE/AFTER"
           display space

           move "Hello World" to ws-source-str
           move 0 to ws-tally-count

           inspect ws-source-str
               tallying ws-tally-count
               for characters before initial " "

           display "SOURCE STRING: " ws-source-str
           display "CHARS BEFORE FIRST SPACE: " ws-tally-count

           move 0 to ws-tally-count

           inspect ws-source-str
               tallying ws-tally-count
               for characters after initial " "

           display "CHARS AFTER FIRST SPACE: " ws-tally-count


      *> ============================================================
      *> EXAMPLE 5: INSPECT REPLACING - ALL
      *> Replace all occurrences of one character with another.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 5 : INSPECT REPLACING - ALL"
           display space

           move "Hello World" to ws-replacing-str

           display "BEFORE: " ws-replacing-str

           inspect ws-replacing-str
               replacing all "l" by "L"

           display "AFTER:  " ws-replacing-str


      *> ============================================================
      *> EXAMPLE 6: INSPECT REPLACING - LEADING
      *> Replace leading occurrences of a character.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 6 : INSPECT REPLACING - LEADING"
           display space

           move "000123456" to ws-replacing-str

           display "BEFORE: " ws-replacing-str

           inspect ws-replacing-str
               replacing leading "0" by "*"

           display "AFTER:  " ws-replacing-str


      *> ============================================================
      *> EXAMPLE 7: INSPECT REPLACING - FIRST
      *> Replace only the first occurrence of a character.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 7 : INSPECT REPLACING - FIRST"
           display space

           move "Hello World" to ws-replacing-str

           display "BEFORE: " ws-replacing-str

           inspect ws-replacing-str
               replacing first "o" by "0"

           display "AFTER:  " ws-replacing-str


      *> ============================================================
      *> EXAMPLE 8: INSPECT REPLACING with BEFORE/AFTER
      *> Replace characters only before or after a specific point.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 8 : INSPECT REPLACING - WITH BEFORE/AFTER"
           display space

           move "aababcabcd" to ws-replacing-str

           display "BEFORE: " ws-replacing-str

           inspect ws-replacing-str
               replacing all "a" by "X"
               after initial "b"

           display "AFTER:  " ws-replacing-str


      *> ============================================================
      *> EXAMPLE 9: INSPECT CONVERTING
      *> Convert a set of characters to another set (similar to
      *> a translation table). Each character in the first string
      *> is replaced by the corresponding character in the second.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 9 : INSPECT CONVERTING - LOWERCASE TO UPPER"
           display space

           move "hello world" to ws-converting-str

           display "BEFORE: " ws-converting-str

           inspect ws-converting-str
               converting
               "abcdefghijklmnopqrstuvwxyz"
               to
               "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

           display "AFTER:  " ws-converting-str


      *> ============================================================
      *> EXAMPLE 10: INSPECT CONVERTING with BEFORE/AFTER
      *> Convert characters only within a specific range.
      *> ============================================================
           display spaces
           display "================================================="
           display "EX 10: INSPECT CONVERTING - WITH AFTER"
           display space

           move "hello world" to ws-converting-str

           display "BEFORE: " ws-converting-str

           inspect ws-converting-str
               converting
               "abcdefghijklmnopqrstuvwxyz"
               to
               "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
               after initial " "

           display "AFTER:  " ws-converting-str

           display space

           goback.

       end program inspect-example.
