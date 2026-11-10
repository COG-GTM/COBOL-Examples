#!/usr/bin/env python3
"""Port of screen_size/get_screen_size.cbl: read the terminal size.

ACCEPT ... FROM LINES/COLUMNS -> ``stdscr.getmaxyx()`` (curses), and
CBL_GET_SCR_SIZE -> ``shutil.get_terminal_size()``. Each is sampled three
times so you can resize the terminal between samples. Terminal-dependent:
without a TTY the sizes come from $LINES/$COLUMNS or the 80x24 default.
"""
import curses
import shutil

from cobol_screen import at, is_interactive, read_line


def display_screen_size(stdscr, lines: int, cols: int) -> None:
    at(stdscr, 2, 1, "-" * 61)
    at(stdscr, 3, 1, "Current screen size: ")
    at(stdscr, 4, 1, f"Columns: {cols:>3}")
    at(stdscr, 5, 1, f"  Lines: {lines:>3}")
    at(stdscr, 7, 1, "Resize and press enter to continue")
    stdscr.refresh()
    while stdscr.get_wch() not in ("\n", "\r", curses.KEY_ENTER):
        pass  # KEY_RESIZE and other keys are ignored, like ACCEPT OMITTED


def screen_mode(stdscr) -> None:
    stdscr.erase()
    at(stdscr, 1, 1, "Using 'ACCEPT ... FROM LINES' and 'ACCEPT ... FROM COLUMNS' to get screen size:")
    for _ in range(3):
        curses.update_lines_cols()
        display_screen_size(stdscr, curses.LINES, curses.COLS)
    stdscr.erase()
    at(stdscr, 1, 1, "Using 'CBL_GET_SCR_SIZE' to get screen size:")
    for _ in range(3):
        size = shutil.get_terminal_size()
        display_screen_size(stdscr, size.lines, size.columns)
    at(stdscr, 9, 1, "Done.")
    stdscr.refresh()
    curses.napms(500)


def main() -> int:
    if is_interactive():
        curses.wrapper(screen_mode)
        return 0
    print("(no interactive terminal: using shutil.get_terminal_size)")
    for title in ("Using 'ACCEPT ... FROM LINES' and 'ACCEPT ... FROM COLUMNS' to get screen size:",
                  "Using 'CBL_GET_SCR_SIZE' to get screen size:"):
        print(title)
        for _ in range(3):
            size = shutil.get_terminal_size()
            print("-" * 61)
            print("Current screen size: ")
            print(f"Columns: {size.columns:>3}")
            print(f"  Lines: {size.lines:>3}")
            read_line("Resize and press enter to continue")
            print()
    print("Done.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
