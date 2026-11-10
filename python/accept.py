#!/usr/bin/env python3
"""Port of accept/accept.cbl: ACCEPT in line mode and in screen mode.

Screen mode (curses) shows ACCEPT ... TIMEOUT, AUTO-SKIP, NO-ECHO and UPPER.
Without an interactive terminal the screen part falls back to reading lines
from stdin and applying the same clauses, so the script can run in a pipe.
"""
from cobol_screen import accept_field, at, is_interactive, read_line, wait_for_key

FIELD_SIZE = 16  # pic x(16)


def pic_x(value: str) -> str:
    return value[:FIELD_SIZE].ljust(FIELD_SIZE)


def screen_mode(stdscr) -> None:
    at(stdscr, 1, 1, "Enter value or wait 3 seconds: ")
    value = accept_field(stdscr, 1, 32, FIELD_SIZE, timeout_s=3)
    at(stdscr, 2, 1, "You entered: ")
    at(stdscr, 2, 14, value)
    at(stdscr, 3, 1, "Enter 16 chars to auto skip: ")
    value = accept_field(stdscr, 3, 30, FIELD_SIZE, auto_skip=True)
    at(stdscr, 4, 1, "You entered: ")
    at(stdscr, 4, 14, value)
    at(stdscr, 5, 1, "Enter a value (no echo): ")
    value = accept_field(stdscr, 5, 26, FIELD_SIZE, echo="none")
    at(stdscr, 6, 1, "You entered: ")
    at(stdscr, 6, 14, value)
    at(stdscr, 7, 1, "Enter a value: ")
    value = accept_field(stdscr, 7, 16, FIELD_SIZE, upper=True)
    at(stdscr, 8, 1, "You entered: ")
    at(stdscr, 8, 14, value)
    wait_for_key(stdscr)  # GnuCOBOL waits before leaving screen mode


def line_mode_fallback() -> None:
    print("(no interactive terminal: screen-mode ACCEPTs read from stdin)")
    print("You entered: " + pic_x(read_line("Enter value or wait 3 seconds: ")))
    print("You entered: " + pic_x(read_line("Enter 16 chars to auto skip: ")))
    read = read_line("Enter a value (no echo): ")
    print()
    print("You entered: " + pic_x(read))
    print("You entered: " + pic_x(read_line("Enter a value: ").upper()))


def main() -> int:
    value = pic_x(read_line("Simple accept. Enter a value: "))
    print("You entered: " + value)
    print("Press any key to enter screen mode.")
    if is_interactive():
        import curses

        read_line()
        curses.wrapper(screen_mode)
    else:
        read_line()
        line_mode_fallback()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
