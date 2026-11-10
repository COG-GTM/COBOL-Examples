#!/usr/bin/env python3
"""Port of display_test/display-test.cbl: positioned DISPLAY with attributes.

Uses curses for AT / LINE COLUMN, BLANK LINE, ERASE EOL, BELL and colours.
Without a terminal it prints an equivalent plain-text rendering.
"""
import curses

from cobol_screen import at, color_pair, is_interactive, wait_for_key

TEXT = "hello world"


def screen_mode(stdscr) -> None:
    curses.curs_set(0)
    if curses.has_colors():
        curses.start_color()
    at(stdscr, 5, 5, TEXT)  # AT 0505
    at(stdscr, 6, 5, TEXT)  # LINE 06 COLUMN 05
    stdscr.move(6, 0)  # WITH BLANK LINE clears the whole line first
    stdscr.clrtoeol()
    at(stdscr, 7, 5, TEXT)
    at(stdscr, 8, 5, TEXT)  # WITH ERASE EOL clears after the text
    stdscr.clrtoeol()
    at(stdscr, 9, 5, TEXT)  # WITH BELL
    curses.beep()
    at(stdscr, 10, 5, TEXT, color_pair(foreground=6, background=3))
    wait_for_key(stdscr)


def plain_fallback() -> None:
    lines = [""] * 10
    for row in range(5, 11):
        lines[row - 1] = " " * 4 + TEXT
    print("\n".join(lines))
    print("\a", end="")
    print("(no interactive terminal: colours/positioning shown via curses only)")


def main() -> int:
    if is_interactive():
        curses.wrapper(screen_mode)
    else:
        plain_fallback()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
