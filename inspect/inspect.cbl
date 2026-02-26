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

       01  ws-source-str              pic x(40).
       01  ws-tally-count             pic 99 value 0.

       01  ws-before-str              pic x(40).
       01  ws-after-str               pic x(40).

       procedure division.
       main-procedure.

      *> EXAMPLE 1:
      *> INSPECT TALLYING - Count all occurrences of a character.
      *> Counts how many times the letter 'L' appears in the string.
           display spaces
           display "================================================="
           display "EX 1 : TALLYING ALL OCCURRENCES OF A CHARACTER"
           display space

           move "HELLO WORLD, WELCOME TO COBOL" to ws-source-str
           move 0 to ws-tally-count

           display "SOURCE STRING: " ws-source-str

           inspect ws-source-str
               tallying ws-tally-count
               for all "L"

           display "COUNT OF 'L': " ws-tally-count


      *> EXAMPLE 2:
      *> INSPECT TALLYING - Count leading characters.
      *> Counts how many leading zeros appear before non-zero data.
           display spaces
           display "================================================="
           display "EX 2 : TALLYING LEADING CHARACTERS"
           display space

           move "000042HELLO WORLD" to ws-source-str
           move 0 to ws-tally-count

           display "SOURCE STRING: " ws-source-str

           inspect ws-source-str
               tallying ws-tally-count
               for leading "0"

           display "LEADING ZEROS: " ws-tally-count


      *> EXAMPLE 3:
      *> INSPECT TALLYING - Count characters before a delimiter.
      *> Counts all characters before the first occurrence of a
      *> specified string.
           display spaces
           display "================================================="
           display "EX 3 : TALLYING CHARACTERS BEFORE INITIAL"
           display space

           move "HELLO WORLD, WELCOME TO COBOL" to ws-source-str
           move 0 to ws-tally-count

           display "SOURCE STRING: " ws-source-str

           inspect ws-source-str
               tallying ws-tally-count
               for characters before initial ","

           display "CHARS BEFORE FIRST ',': " ws-tally-count


      *> EXAMPLE 4:
      *> INSPECT TALLYING - Multiple tallying conditions combined.
      *> Counts occurrences of multiple different patterns in a
      *> single INSPECT statement.
           display spaces
           display "================================================="
           display "EX 4 : TALLYING WITH MULTIPLE CONDITIONS"
           display space

           move "ABRACADABRA ALAKAZAM" to ws-source-str
           move 0 to ws-tally-count

           display "SOURCE STRING: " ws-source-str

           inspect ws-source-str
               tallying ws-tally-count
               for all "A"

           display "COUNT OF 'A': " ws-tally-count


      *> EXAMPLE 5:
      *> INSPECT REPLACING - Replace all occurrences of a character.
      *> Replaces every occurrence of one character with another.
           display spaces
           display "================================================="
           display "EX 5 : REPLACING ALL OCCURRENCES"
           display space

           move "HELLO WORLD" to ws-source-str
           move ws-source-str to ws-before-str

           inspect ws-source-str
               replacing all "L" by "R"

           display "BEFORE: " ws-before-str
           display "AFTER:  " ws-source-str


      *> EXAMPLE 6:
      *> INSPECT REPLACING - Replace leading characters.
      *> Replaces leading zeros with spaces (common for formatting).
           display spaces
           display "================================================="
           display "EX 6 : REPLACING LEADING CHARACTERS"
           display space

           move "00004200" to ws-source-str
           move ws-source-str to ws-before-str

           inspect ws-source-str
               replacing leading "0" by " "

           display "BEFORE: --" ws-before-str "--"
           display "AFTER:  --" ws-source-str "--"


      *> EXAMPLE 7:
      *> INSPECT REPLACING - Replace first occurrence only.
      *> Replaces only the first matching character occurrence.
           display spaces
           display "================================================="
           display "EX 7 : REPLACING FIRST OCCURRENCE"
           display space

           move "BANANA" to ws-source-str
           move ws-source-str to ws-before-str

           inspect ws-source-str
               replacing first "A" by "O"

           display "BEFORE: " ws-before-str
           display "AFTER:  " ws-source-str


      *> EXAMPLE 8:
      *> INSPECT REPLACING - Replace before/after initial.
      *> Replaces characters only before or after a specified
      *> delimiter string. Here we replace spaces with dashes
      *> but only before the first comma.
           display spaces
           display "================================================="
           display "EX 8 : REPLACING BEFORE INITIAL"
           display space

           move "HELLO WORLD, WELCOME HOME" to ws-source-str
           move ws-source-str to ws-before-str

           inspect ws-source-str
               replacing all " " by "-"
                   before initial ","

           display "BEFORE: " ws-before-str
           display "AFTER:  " ws-source-str


      *> EXAMPLE 9:
      *> INSPECT CONVERTING - Convert character-by-character.
      *> Converts each character from one set to a corresponding
      *> character in another set. This is useful for case conversion
      *> or character translation.
           display spaces
           display "================================================="
           display "EX 9 : CONVERTING CHARACTERS"
           display space

           move "Hello World 123" to ws-source-str
           move ws-source-str to ws-before-str

           inspect ws-source-str
               converting
                   "abcdefghijklmnopqrstuvwxyz"
               to  "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

           display "BEFORE: " ws-before-str
           display "AFTER:  " ws-source-str


      *> EXAMPLE 10:
      *> INSPECT TALLYING and REPLACING combined.
      *> Counts and replaces in a single INSPECT statement.
           display spaces
           display "================================================="
           display "EX 10: TALLYING AND REPLACING COMBINED"
           display space

           move "MISSISSIPPI" to ws-source-str
           move ws-source-str to ws-before-str
           move 0 to ws-tally-count

           inspect ws-source-str
               tallying ws-tally-count
               for all "S"
               replacing all "S" by "*"

           display "BEFORE:  " ws-before-str
           display "AFTER:   " ws-source-str
           display "COUNT OF 'S': " ws-tally-count

           display spaces

           stop run.

       end program inspect-example.
