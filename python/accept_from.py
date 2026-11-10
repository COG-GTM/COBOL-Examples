#!/usr/bin/env python3
"""Port of accept/accept_from.cbl: the ACCEPT ... FROM special registers.

COMMAND-LINE / ARGUMENT-* -> sys.argv, ENVIRONMENT -> os.environ,
DATE / DAY / TIME / DAY-OF-WEEK -> datetime, USER NAME -> getpass.getuser,
CONSOLE -> input(), LINES / COLUMNS and CBL_GET_SCR_SIZE -> curses and
shutil.get_terminal_size (terminal-dependent).
"""
import getpass
import os
import shutil
import sys
from datetime import datetime

from cobol_screen import at, is_interactive, read_line, wait_for_key

FIELD_SIZE = 50  # pic x(50)
ENV_KEY = "COB_TEST_ENV_KEY"


def pic_x(value) -> str:
    return str(value)[:FIELD_SIZE].ljust(FIELD_SIZE)


def show(label: str, value) -> None:
    print(f"accept from {label}: " + pic_x(value))


def pic_9(value: int, digits: int = 3) -> str:
    return str(value % 10**digits).zfill(digits)


def screen_part(stdscr) -> None:
    lines, cols = stdscr.getmaxyx()
    at(stdscr, 2, 1, "accept from lines: ")
    at(stdscr, 2, 20, pic_x(lines))
    at(stdscr, 3, 1, "accept from columns: ")
    at(stdscr, 3, 22, pic_x(cols))
    at(stdscr, 4, 1, "Using CBL_GET_SCR_SIZE instead: ")
    size = shutil.get_terminal_size()
    at(stdscr, 5, 1, "Num lines: ")
    at(stdscr, 5, 14, pic_9(size.lines))
    at(stdscr, 6, 1, "Num cols: ")
    at(stdscr, 6, 14, pic_9(size.columns))
    wait_for_key(stdscr)


def main(argv=None) -> int:
    args = sys.argv[1:] if argv is None else argv
    print(" ")
    print("ACCEPT... FROM... Example Program")
    print("---------------------------------")
    print("Pass command line parameters to demo that feature")
    print(" ")

    show("command-line", " ".join(args))
    show("argument-number", f"{len(args):09d}")
    for arg in args:
        show("argument-value", arg)

    print("Before environment setting set:")
    show("environment", os.environ.get(ENV_KEY, ""))
    show("exception status", "0000")
    os.environ[ENV_KEY] = "NOW SET!"  # SET ENVIRONMENT
    print("After environment setting set:")
    show("environment", os.environ.get(ENV_KEY, ""))

    now = datetime.now()
    show("date", now.strftime("%y%m%d"))
    show("date yyyymmdd", now.strftime("%Y%m%d"))
    show("day", now.strftime("%y%j"))
    show("day yyyyddd", now.strftime("%Y%j"))
    show("time", now.strftime("%H%M%S") + f"{now.microsecond // 10000:02d}")
    show("day-of-week", now.isoweekday())  # 1 = Monday ... 7 = Sunday
    show("user name", getpass.getuser())

    show("console", read_line("Enter value: "))
    print("Press enter to enter screen mode.")
    read_line()

    if is_interactive():
        import curses

        curses.wrapper(screen_part)
    else:
        size = shutil.get_terminal_size()
        print("(no interactive terminal: reporting shutil.get_terminal_size)")
        show("lines", size.lines)
        show("columns", size.columns)
        print("Using CBL_GET_SCR_SIZE instead: ")
        print("Num lines: " + pic_9(size.lines))
        print("Num cols: " + pic_9(size.columns))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
