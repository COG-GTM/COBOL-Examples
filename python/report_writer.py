#!/usr/bin/env python3
"""Port of report_writer/report_test.cbl: REPORT WRITER via f-strings.

Reproduces the RD layout: PAGE LIMIT 66, report heading on lines 1-2,
detail lines 6-42, one detail line per input record.
"""
import argparse
from dataclasses import dataclass
from pathlib import Path

DEFAULT_INPUT = Path(__file__).resolve().parent.parent / "report_writer" / "input.txt"

PAGE_LIMIT = 66
FIRST_DETAIL = 6
LAST_DETAIL = 42


@dataclass
class StudentRecord:
    student_id: int  # pic 9(6)
    student_name: str  # pic x(20)
    major: str  # pic xxx
    num_courses: int  # pic 99

    @classmethod
    def from_line(cls, line: str) -> "StudentRecord":
        line = line.rstrip("\n").ljust(31)
        return cls(int(line[0:6] or 0), line[6:26], line[26:29], int(line[29:31] or 0))


def place(columns) -> str:
    """Build a print line from (1-based column, text) pairs."""
    line = ""
    for column, text in columns:
        line = line.ljust(column - 1) + text
    return line


def report_heading(page_counter: int) -> list:
    return [
        place([(44, f"{'Customer Order Report':<21}")]),
        place([(100, "PAGE"), (105, f"{page_counter:>3}")]),  # pic zz9
    ]


def detail_line(rec: StudentRecord) -> str:
    return place([
        (4, f"{rec.student_id:06d}"),
        (15, f"{rec.student_name:<20.20}"),
        (40, f"{rec.major:<3.3}"),
        (46, f"{rec.num_courses:02d}"),
    ])


class Report:
    """INITIATE / GENERATE / TERMINATE for a single RD."""

    def __init__(self):
        self.pages = []
        self.page_counter = 0
        self._new_page()

    def _new_page(self) -> None:
        self.page_counter += 1
        # TYPE REPORT HEADING prints once, at the top of the first page.
        heading = report_heading(self.page_counter) if self.page_counter == 1 else []
        self.pages.append(heading)

    def generate(self, rec: StudentRecord) -> None:
        page = self.pages[-1]
        if len(page) >= LAST_DETAIL:
            self._new_page()
            page = self.pages[-1]
        while len(page) < FIRST_DETAIL - 1:
            page.append("")
        page.append(detail_line(rec))

    def terminate(self) -> str:
        lines = []
        for page in self.pages:
            lines.extend(page + [""] * (PAGE_LIMIT - len(page)))
        return "\n".join(lines) + "\n"


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--input", type=Path, default=DEFAULT_INPUT, help="fixed-width input file")
    parser.add_argument("--output", type=Path, default=Path("report.txt"), help="report file to write")
    args = parser.parse_args(argv)

    print("Starting test report program.")
    with args.input.open() as input_file:
        print("Init test report.")
        report = Report()
        for line in input_file:
            if not line.strip():
                continue
            print("Generate report line.")
            report.generate(StudentRecord.from_line(line))
        print("Terminate report.")
        args.output.write_text(report.terminate())
    print("Done.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
