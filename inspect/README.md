# INSPECT Verb Example

The ```INSPECT``` verb is used for character counting (tallying) and replacement within a data item. It provides powerful string manipulation capabilities and comes in several forms:

- **INSPECT TALLYING** - Counts occurrences of characters or strings
- **INSPECT REPLACING** - Replaces characters or strings  
- **INSPECT CONVERTING** - Converts characters from one set to another
- **INSPECT TALLYING ... REPLACING** - Counts and replaces in one statement


**Basic examples of syntax**

Tallying:
```
   inspect ws-source-str
       tallying ws-count
       for all "A"
```

Replacing:
```
   inspect ws-source-str
       replacing all "A" by "B"
```

Converting:
```
   inspect ws-source-str
       converting "abc" to "ABC"
```

The ```for``` clause supports several options:
- ```ALL``` - counts/replaces all occurrences
- ```LEADING``` - counts/replaces only leading (consecutive from start) occurrences
- ```FIRST``` - replaces only the first occurrence
- ```CHARACTERS``` - counts/replaces all characters
- ```BEFORE INITIAL``` - limits scope to before first occurrence of a delimiter
- ```AFTER INITIAL``` - limits scope to after first occurrence of a delimiter


```inspect.cbl``` demonstrates several examples of using ```INSPECT``` in different forms including tallying, replacing, converting, and combined tallying/replacing.


**Example of program output:**

```
=================================================
EX 1 : TALLYING ALL OCCURRENCES OF A CHARACTER
 
SOURCE STRING: HELLO WORLD, WELCOME TO COBOL       
COUNT OF 'L': 05
 
=================================================
EX 2 : TALLYING LEADING CHARACTERS
 
SOURCE STRING: 000042HELLO WORLD                    
LEADING ZEROS: 04
 
=================================================
EX 3 : TALLYING CHARACTERS BEFORE INITIAL
 
SOURCE STRING: HELLO WORLD, WELCOME TO COBOL       
CHARS BEFORE FIRST ',': 11
 
=================================================
EX 4 : TALLYING WITH MULTIPLE CONDITIONS
 
SOURCE STRING: ABRACADABRA ALAKAZAM                 
COUNT OF 'A': 09
COUNT OF 'B': 02
 
=================================================
EX 5 : REPLACING ALL OCCURRENCES
 
BEFORE: HELLO WORLD                             
AFTER:  HERRO WORRD                             
 
=================================================
EX 6 : REPLACING LEADING CHARACTERS
 
BEFORE: --00004200                              --
AFTER:  --    4200                              --
 
=================================================
EX 7 : REPLACING FIRST OCCURRENCE
 
BEFORE: BANANA                                  
AFTER:  BONANA                                  
 
=================================================
EX 8 : REPLACING BEFORE INITIAL
 
BEFORE: HELLO WORLD, WELCOME HOME               
AFTER:  HELLO-WORLD, WELCOME HOME               
 
=================================================
EX 9 : CONVERTING CHARACTERS
 
BEFORE: Hello World 123                         
AFTER:  HELLO WORLD 123                         
 
=================================================
EX 10: TALLYING AND REPLACING COMBINED
 
BEFORE:  MISSISSIPPI                             
AFTER:   MI**I**IPPI                             
COUNT OF 'S': 04
```
