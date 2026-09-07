"""Port of ``RD r-test-report`` and ``main-procedure`` (docs/phase0-contracts.md §3).

Method names stay traceable to the COBOL verbs and paragraph names.
"""

from __future__ import annotations

from dataclasses import dataclass, field

from .cobol_types import (
    cobol_edit_zz9,
    cobol_move_alphanumeric,
    cobol_move_display_numeric,
    from_byte_string,
)
from .record import (
    F_TEST_MAJOR,
    F_TEST_NUM_COURSES,
    F_TEST_STUDENT_ID,
    F_TEST_STUDENT_NAME,
    RecordReader,
    TestRecord,
)

PAGE_LIMIT = 66
HEADING_LINE = 1
FIRST_DETAIL = 6
LAST_DETAIL = 42
FOOTING = 52

#: Physical distance between page origins in the emitted file (ruling D-003).
PAGE_ADVANCE = 65
DETAIL_LINES_PER_PAGE = LAST_DETAIL - FIRST_DETAIL + 1

HEADING_TEXT = "Customer Order Report"
HEADING_TEXT_COLUMN = 44
PAGE_LABEL = "PAGE"
PAGE_LABEL_COLUMN = 100
PAGE_COUNTER_COLUMN = 105

DETAIL_COLUMNS = {
    F_TEST_STUDENT_ID.name: 4,
    F_TEST_STUDENT_NAME.name: 15,
    F_TEST_MAJOR.name: 40,
    F_TEST_NUM_COURSES.name: 46,
}

MSG_STARTING = "Starting test report program."
MSG_INIT = "Init test report."
MSG_GENERATE = "Generate report line."
MSG_TERMINATE = "Terminate report."
MSG_DONE = "Done."


def _place(line: str, column: int, text: str) -> str:
    """Write ``text`` at a 1-based print column, space filling any gap."""
    return line.ljust(column - 1)[: column - 1] + text


@dataclass
class ReportPage:
    """One physical page of the print file."""

    number: int
    lines: list[str] = field(default_factory=list)

    def text(self) -> str:
        return "\n".join(self.lines)

    def display_lines(self) -> list[str]:
        """The page decoded back to text for JSON transport."""
        return [from_byte_string(line) for line in self.lines]


@dataclass
class ReportRun:
    """Everything the COBOL job produced: the print file plus its console output."""

    pages: list[ReportPage]
    console: list[str]
    detail_count: int
    record_count: int

    def report_byte_string(self) -> str:
        """The print file exactly as COBOL would write it, one character per byte."""
        lines: list[str] = []
        for page in self.pages:
            lines.extend(page.lines)
        lines.append("")  # trailing physical line, ruling D-003
        return "\n".join(lines) + "\n"

    def report_bytes(self) -> bytes:
        return self.report_byte_string().encode("latin-1")

    def report_text(self) -> str:
        return from_byte_string(self.report_byte_string())


class ReportWriter:
    """``INITIATE`` / ``GENERATE`` / ``TERMINATE`` for ``r-test-report``."""

    def __init__(self) -> None:
        self.page_counter = 0
        self.line_counter = 0
        self.pages: list[ReportPage] = []
        self.detail_count = 0

    # INITIATE r-test-report
    def initiate(self) -> None:
        self.page_counter = 0
        self.line_counter = 0
        self.pages = []
        self.detail_count = 0

    def _start_page(self) -> ReportPage:
        self.page_counter += 1
        page = ReportPage(number=self.page_counter, lines=[""] * PAGE_ADVANCE)
        self.pages.append(page)
        self.line_counter = 0
        if self.page_counter == 1:
            self._report_header(page)
        return page

    # 01 report-header TYPE REPORT HEADING
    def _report_header(self, page: ReportPage) -> None:
        page.lines[HEADING_LINE - 1] = _place("", HEADING_TEXT_COLUMN, HEADING_TEXT)
        second = _place("", PAGE_LABEL_COLUMN, PAGE_LABEL)
        second = _place(second, PAGE_COUNTER_COLUMN, cobol_edit_zz9(self.page_counter))
        page.lines[HEADING_LINE] = second

    # GENERATE report-line  (01 report-line TYPE DETAIL LINE PLUS 1)
    def generate_report_line(self, record: TestRecord) -> None:
        if not self.pages or self.line_counter >= DETAIL_LINES_PER_PAGE:
            self._start_page()
        page = self.pages[-1]
        line = _place(
            "",
            DETAIL_COLUMNS[F_TEST_STUDENT_ID.name],
            cobol_move_display_numeric(record.student_id_raw, F_TEST_STUDENT_ID.width),
        )
        line = _place(
            line,
            DETAIL_COLUMNS[F_TEST_STUDENT_NAME.name],
            cobol_move_alphanumeric(record.student_name_raw, F_TEST_STUDENT_NAME.width),
        )
        line = _place(
            line,
            DETAIL_COLUMNS[F_TEST_MAJOR.name],
            cobol_move_alphanumeric(record.major_raw, F_TEST_MAJOR.width),
        )
        line = _place(
            line,
            DETAIL_COLUMNS[F_TEST_NUM_COURSES.name],
            cobol_move_display_numeric(record.num_courses_raw, F_TEST_NUM_COURSES.width),
        )
        page.lines[FIRST_DETAIL - 1 + self.line_counter] = line.rstrip()
        self.line_counter += 1
        self.detail_count += 1

    # TERMINATE r-test-report
    def terminate(self) -> None:
        if not self.pages:
            self._start_page()


def run_report(input_text: str, *, strict_eof: bool = False) -> ReportRun:
    """``main-procedure``: open, INITIATE, read/GENERATE loop, TERMINATE, close.

    ``strict_eof`` suppresses the duplicated final detail line described by ruling
    D-001. It defaults to ``False`` so the port matches the COBOL, and the parity
    harness never enables it.
    """
    console = [MSG_STARTING, MSG_INIT]
    reader = RecordReader(input_text)
    writer = ReportWriter()
    writer.initiate()

    record_count = 0
    while not reader.eof:
        reader.read()
        if reader.eof and strict_eof:
            break
        if not reader.eof:
            record_count += 1
        console.append(MSG_GENERATE)
        writer.generate_report_line(reader.record)

    console.append(MSG_TERMINATE)
    writer.terminate()
    console.append(MSG_DONE)

    return ReportRun(
        pages=writer.pages,
        console=console,
        detail_count=writer.detail_count,
        record_count=record_count,
    )
