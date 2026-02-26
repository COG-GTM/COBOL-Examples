# INSPECT Verb Example (Character Counting/Replacement)


```INSPECT``` is used to count (tally) and replace characters or groups of characters within a string. It has three main forms:

- ```INSPECT TALLYING``` - Count occurrences of characters or substrings
- ```INSPECT REPLACING``` - Replace characters or substrings
- ```INSPECT CONVERTING``` - Translate characters using a character map


**Basic examples of syntax**

Tallying (counting):
```
   inspect ws-source-str
       tallying ws-tally-count
       for all "l"
```

Replacing:
```
   inspect ws-source-str
       replacing all "l" by "L"
```

Converting (translation table):
```
   inspect ws-source-str
       converting
       "abcdefghijklmnopqrstuvwxyz"
       to
       "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
```

You can also use ```BEFORE INITIAL``` and ```AFTER INITIAL``` clauses to limit the scope of tallying, replacing, or converting to a portion of the string.


```inspect.cbl``` demonstrates several examples of using ```INSPECT``` in different forms.


**Example of program output:**

```
=================================================
EX 1 : INSPECT TALLYING - ALL CHARACTERS
 
SOURCE STRING: Hello World                   
TOTAL CHARACTERS: 30
 
=================================================
EX 2 : INSPECT TALLYING - ALL 'l'
 
SOURCE STRING: Hello World                   
COUNT OF 'l': 03
 
=================================================
EX 3 : INSPECT TALLYING - LEADING SPACES
 
SOURCE STRING: --   Hello World                --
LEADING SPACES: 03
 
=================================================
EX 4 : INSPECT TALLYING - BEFORE/AFTER
 
SOURCE STRING: Hello World                   
CHARS BEFORE FIRST SPACE: 05
CHARS AFTER FIRST SPACE: 24
 
=================================================
EX 5 : INSPECT REPLACING - ALL
 
BEFORE: Hello World                   
AFTER:  HeLLo WorLd                   
 
=================================================
EX 6 : INSPECT REPLACING - LEADING
 
BEFORE: 000123456                     
AFTER:  ***123456                     
 
=================================================
EX 7 : INSPECT REPLACING - FIRST
 
BEFORE: Hello World                   
AFTER:  Hell0 World                   
 
=================================================
EX 8 : INSPECT REPLACING - WITH BEFORE/AFTER
 
BEFORE: aababcabcd                    
AFTER:  aabXbcXbcd                    
 
=================================================
EX 9 : INSPECT CONVERTING - LOWERCASE TO UPPER
 
BEFORE: hello world                   
AFTER:  HELLO WORLD                   
 
=================================================
EX 10: INSPECT CONVERTING - WITH AFTER
 
BEFORE: hello world                   
AFTER:  hello WORLD                   
 
```
