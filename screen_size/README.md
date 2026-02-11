# Getting screen size example


Getting the number of lines and columns of the current display can be helpful when formatting 
the output to the screen. ```get_screen_size.cbl``` shows two different examples on how 
to do this. Note that the usage of both methods puts the program into "Screen Mode" which 
requires text locations specified for display statements or the use of the ```SCREEN SECTION.```



**Basic example of syntax**

```
    accept ws-num-lines from lines 
    accept ws-num-cols from columns 

    *> OR:

    call 'CBL_GET_SRC_SIZE' using ws-num-lines ws-num-cols
```

## How to Compile

```bash
cobc -x get_screen_size.cbl
```

## How to Run

```bash
./get_screen_size
```

The program enters screen mode and displays the current terminal dimensions. It waits for you to resize the terminal and press Enter between each method to see updated values.

## Prerequisites

- GnuCOBOL (`cobc`)
- A terminal that supports ncurses
- Interactive (requires user input)

**Example of program output:**

```
Using 'ACCEPT ... FROM LINES' and 'ACCEPT ... FROM COLUMNS' to get screen size:
-------------------------------------------------------------
Current screen size:
Columns: 099
  Lines: 033

Resize and press enter to continue


Using 'CBL_GET_SCR_SIZE' to get screen size:
-------------------------------------------------------------
Current screen size:
Columns: 099
  Lines: 033

Resize and press enter to continue

Done.

```
