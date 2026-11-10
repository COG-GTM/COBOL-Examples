#!/usr/bin/env python3
"""Port of mouse/mouse_example.cbl: paint with the mouse using curses.

Hold the left mouse button and move to paint cells (rows 1-18) in the
current colour; number keys 0-7 pick the colour; Esc or Q exits.
Requires an interactive terminal with mouse reporting (xterm-compatible).
"""
import curses
import sys

from cobol_screen import at, color_pair, is_interactive

ESC = "\x1b"
DRAW_ROWS = 18
DRAW_COLS = 80


def paint_attr(color: int) -> int:
    """Background colour for a painted cell; reverse video on monochrome terminals."""
    return color_pair(7, color) if curses.has_colors() else curses.A_REVERSE


def paint(stdscr) -> None:
    curses.curs_set(0)
    if curses.has_colors():
        curses.start_color()
    curses.mousemask(curses.BUTTON1_PRESSED | curses.BUTTON1_RELEASED | curses.REPORT_MOUSE_POSITION)
    curses.mouseinterval(0)
    print("\033[?1003h", end="", flush=True)  # ask xterm to report mouse motion
    stdscr.keypad(True)
    stdscr.timeout(50)  # TIMEOUT AFTER 50 (COB_TIMEOUT_SCALE=3 -> ms)

    draw_color = 1
    clicked = False
    at(stdscr, 19, 1, "Current Draw color:")
    at(stdscr, 20, 1,
       "Esc to exit. Number keys to change cursor draw color. Left mouse down to draw current color.",
       color_pair(7, 1) | curses.A_BOLD)
    try:
        while True:
            at(stdscr, 19, 21, " ", paint_attr(draw_color))
            stdscr.refresh()
            try:
                key = stdscr.get_wch()
            except curses.error:
                continue  # timeout
            if key == ESC or (isinstance(key, str) and key.upper() == "Q"):
                break
            if isinstance(key, str) and key.isdigit():
                draw_color = min(int(key), 7)
                continue
            if key != curses.KEY_MOUSE:
                continue
            try:
                _id, x, y, _z, state = curses.getmouse()
            except curses.error:
                continue
            if state & curses.BUTTON1_PRESSED:
                clicked = True
            if state & curses.BUTTON1_RELEASED:
                clicked = False
            if clicked and y < DRAW_ROWS and x < DRAW_COLS:
                at(stdscr, y + 1, x + 1, " ", paint_attr(draw_color))
    finally:
        print("\033[?1003l", end="", flush=True)


def main() -> int:
    if not is_interactive():
        print("mouse_example.py needs an interactive terminal with mouse support; nothing to do.")
        return 0
    try:
        curses.wrapper(paint)
    except curses.error as exc:
        print(f"curses error: {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
