#!/usr/bin/env python3
"""Port of display_timing/display_timing.cbl: time two ways of drawing a screen.

Fills a 20x80 area with '@' 100 times per run, 10 runs per method, timing
each run with ``time.perf_counter`` (COBOL used ACCEPT FROM TIME, which only
has 1/100 s resolution). Method 1 mirrors ``DISPLAY ... AT ws-screen-position``
(one call with a packed position), method 2 mirrors ``LINE r COLUMN c``
(move, then write). Without a terminal the drawing goes to an off-screen
character buffer so the timings still run.
"""
import argparse
import time

from cobol_screen import is_interactive, read_line

MAX_ROWS = 20
MAX_COLS = 80


class BufferScreen:
    """Off-screen stand-in for a curses window."""

    def __init__(self):
        self.cells = [[" "] * MAX_COLS for _ in range(MAX_ROWS)]
        self.y = self.x = 0

    def addstr(self, y, x, text):
        self.cells[y][x] = text

    def move(self, y, x):
        self.y, self.x = y, x

    def addch(self, ch):
        self.cells[self.y][self.x] = ch

    def refresh(self):
        pass


def draw_at_position(screen, refreshes: int) -> None:
    for _ in range(refreshes):
        for row in range(MAX_ROWS):
            for col in range(MAX_COLS):
                screen.addstr(row, col, "@")
        screen.refresh()


def draw_line_column(screen, refreshes: int) -> None:
    for _ in range(refreshes):
        for row in range(MAX_ROWS):
            for col in range(MAX_COLS):
                screen.move(row, col)
                screen.addch("@")
        screen.refresh()


def time_runs(method, screen, runs: int, refreshes: int, report) -> list:
    diffs = []
    for run in range(1, runs + 1):
        start = time.perf_counter()
        method(screen, refreshes)
        diffs.append(time.perf_counter() - start)
        report(f"run {run:2d}: {diffs[-1]:8.3f} s")
    return diffs


def summary(name: str, diffs) -> list:
    lines = [f"{name}:"]
    lines += [f"  run {i:2d}: {d:8.3f} s" for i, d in enumerate(diffs, start=1)]
    lines.append(f"  total : {sum(diffs):8.3f} s")
    lines.append(f"  avg   : {sum(diffs) / len(diffs):8.3f} s")
    return lines


def run_curses(runs: int, refreshes: int) -> list:
    import curses

    results = []

    def body(stdscr):
        curses.curs_set(0)
        max_y, max_x = stdscr.getmaxyx()
        if max_y < MAX_ROWS + 2 or max_x < MAX_COLS:
            raise SystemExit(f"terminal must be at least {MAX_COLS}x{MAX_ROWS + 2}")

        def report(text):
            stdscr.addstr(MAX_ROWS + 1, 0, text)
            stdscr.refresh()

        for name, method in (("AT position", draw_at_position), ("LINE/COLUMN", draw_line_column)):
            stdscr.erase()
            results.append((name, time_runs(method, stdscr, runs, refreshes, report)))

    curses.wrapper(body)
    return results


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--runs", type=int, default=10, help="timed runs per method (default 10)")
    parser.add_argument("--refreshes", type=int, default=100, help="screen fills per run (default 100)")
    args = parser.parse_args(argv)

    read_line("Press enter to start...")
    if is_interactive():
        results = run_curses(args.runs, args.refreshes)
    else:
        print("(no interactive terminal: drawing to an off-screen buffer)")
        results = [
            (name, time_runs(method, BufferScreen(), args.runs, args.refreshes, lambda _t: None))
            for name, method in (("AT position", draw_at_position), ("LINE/COLUMN", draw_line_column))
        ]
    for name, diffs in results:
        print("\n".join(summary(name, diffs)))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
