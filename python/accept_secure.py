#!/usr/bin/env python3
"""Port of accept/accept-secure.cbl: ACCEPT ... SECURE (masked password entry).

In a terminal the password is typed in a curses field that echoes '*'
like GnuCOBOL's SECURE clause. Otherwise ``getpass`` is used (no echo).
"""
import getpass
import sys

from cobol_screen import accept_field, at, is_interactive, wait_for_key

FIELD_SIZE = 10  # pic x(10)


def screen_mode(stdscr) -> None:
    at(stdscr, 1, 1, "Enter password: ")
    password = accept_field(stdscr, 1, 17, FIELD_SIZE, echo="mask")
    at(stdscr, 2, 4, "You entered: ")
    at(stdscr, 2, 17, password)
    wait_for_key(stdscr)


def main() -> int:
    if is_interactive():
        import curses

        curses.wrapper(screen_mode)
    else:
        if sys.stdin.isatty():
            password = getpass.getpass("Enter password: ")
        else:  # piped input: nothing to hide, avoid getpass' echo warning
            print("Enter password: ")
            password = sys.stdin.readline().rstrip("\n")
        print("   You entered: " + password[:FIELD_SIZE].ljust(FIELD_SIZE))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
