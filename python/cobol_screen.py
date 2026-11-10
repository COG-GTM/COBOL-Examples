"""Shared helpers for the curses-based ports of COBOL SCREEN I/O programs.

COBOL ``DISPLAY ... AT LLCC`` / ``LINE n COLUMN m`` use 1-based positions;
curses uses 0-based (y, x). COBOL colour numbers 0-7 differ from curses'
numbering, so ``COBOL_COLORS`` maps between them.
"""
from __future__ import annotations

import curses
import os
import sys

# COBOL: 0 black 1 blue 2 green 3 cyan 4 red 5 magenta 6 brown/yellow 7 white
COBOL_COLORS = [
    curses.COLOR_BLACK,
    curses.COLOR_BLUE,
    curses.COLOR_GREEN,
    curses.COLOR_CYAN,
    curses.COLOR_RED,
    curses.COLOR_MAGENTA,
    curses.COLOR_YELLOW,
    curses.COLOR_WHITE,
]


def is_interactive() -> bool:
    """True when curses can take over a real terminal.

    Set ``COBOL_DEMO_NO_CURSES=1`` to force the plain line-mode fallback.
    """
    if os.environ.get("COBOL_DEMO_NO_CURSES"):
        return False
    return sys.stdin.isatty() and sys.stdout.isatty() and bool(os.environ.get("TERM"))


def at(stdscr, line: int, column: int, text: str, attr: int = 0) -> None:
    """DISPLAY text AT line/column (1-based), clipped to the window."""
    max_y, max_x = stdscr.getmaxyx()
    y, x = line - 1, column - 1
    if 0 <= y < max_y and 0 <= x < max_x:
        text = text[: max_x - x]
        try:
            stdscr.addstr(y, x, text, attr)
        except curses.error:
            pass  # writing the bottom-right cell raises after drawing


_pairs = {}


def color_pair(foreground: int, background: int) -> int:
    """curses attribute for a COBOL FOREGROUND-COLOR/BACKGROUND-COLOR pair."""
    if not curses.has_colors():
        return 0
    key = (foreground, background)
    if key not in _pairs:
        number = len(_pairs) + 1
        if number >= curses.COLOR_PAIRS:
            return 0
        curses.init_pair(number, COBOL_COLORS[foreground % 8], COBOL_COLORS[background % 8])
        _pairs[key] = number
    return curses.color_pair(_pairs[key])


def accept_field(stdscr, line: int, column: int, size: int, *, echo: str = "text",
                 upper: bool = False, auto_skip: bool = False, timeout_s: float | None = None) -> str:
    """ACCEPT field AT line/column with the common COBOL screen clauses.

    echo: "text" (normal), "none" (NO-ECHO) or "mask" (SECURE, shows '*').
    Returns the value padded to ``size`` like a PIC X(size) field.
    """
    value = ""
    stdscr.keypad(True)
    stdscr.timeout(int(timeout_s * 1000) if timeout_s else -1)
    curses.curs_set(1)
    while True:
        shown = {"text": value, "none": "", "mask": "*" * len(value)}[echo]
        at(stdscr, line, column, shown.ljust(size)[:size])
        stdscr.move(line - 1, min(column - 1 + len(shown), stdscr.getmaxyx()[1] - 1))
        stdscr.refresh()
        key = stdscr.get_wch() if _has_input(stdscr) else None
        if key is None:  # TIMEOUT expired
            break
        stdscr.timeout(-1)
        if key in ("\n", "\r", curses.KEY_ENTER):
            break
        if key in (curses.KEY_BACKSPACE, "\b", "\x7f"):
            value = value[:-1]
        elif isinstance(key, str) and key.isprintable() and len(value) < size:
            value += key.upper() if upper else key
            if auto_skip and len(value) == size:
                break
    stdscr.timeout(-1)
    return value.ljust(size)


def _has_input(stdscr) -> bool:
    """Peek for a key honouring the current timeout without losing it."""
    try:
        key = stdscr.get_wch()
    except curses.error:
        return False
    if isinstance(key, int):
        curses.ungetch(key)
    else:
        curses.unget_wch(key)
    return True


def wait_for_key(stdscr) -> None:
    """ACCEPT OMITTED: wait for any key."""
    stdscr.timeout(-1)
    stdscr.refresh()
    stdscr.get_wch()


def read_line(prompt: str = "") -> str:
    try:
        return input(prompt)
    except EOFError:
        return ""
