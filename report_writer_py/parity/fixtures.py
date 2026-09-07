"""Fixtures for the reconciliation harness.

Every documented boundary in ``docs/phase0-contracts.md`` must be crossed: the empty
file (which still emits a detail line, ruling D-001), the first page, an exactly full
page, the page rollover, several pages, short records, and the display-numeric edge
cases of §4.
"""

from __future__ import annotations

from dataclasses import dataclass

from app.report_writer import DETAIL_LINES_PER_PAGE


def make_record(student_id: str, name: str, major: str, courses: str) -> str:
    return f"{student_id[:6]:<6}{name[:20]:<20}{major[:3]:<3}{courses[:2]:<2}"


def _sequence(count: int) -> str:
    rows = [make_record(f"{i:06d}", f"Student {i}", "PHY", f"{i % 100:02d}") for i in range(1, count + 1)]
    return "".join(row + "\n" for row in rows)


@dataclass(frozen=True)
class Fixture:
    name: str
    boundary: str
    input_text: str


#: One detail line short of a full page, exactly full, and one past the rollover.
_PAGE = DETAIL_LINES_PER_PAGE

FIXTURES: tuple[Fixture, ...] = (
    Fixture("empty", "empty input file; detail emitted from the unloaded record area (D-001)", ""),
    Fixture("single", "first detail line on page 1", _sequence(1)),
    Fixture(
        "repo_input",
        "the repository's own input.txt content",
        _sequence(0) + "".join(make_record("003345", "Test Name2", "PHY", "12") + "\n" for _ in range(7)),
    ),
    Fixture(
        "page_minus_one",
        f"{_PAGE - 2} records -> {_PAGE - 1} details, one short of a full page",
        _sequence(_PAGE - 2),
    ),
    Fixture("page_exact", f"{_PAGE - 1} records -> {_PAGE} details, page exactly full", _sequence(_PAGE - 1)),
    Fixture(
        "page_rollover", f"{_PAGE} records -> {_PAGE + 1} details, first line of page 2", _sequence(_PAGE)
    ),
    Fixture("three_pages", "spans three physical pages", _sequence(2 * _PAGE + 5)),
    Fixture(
        "short_and_ragged",
        "short line-sequential records padded to 31 bytes (D-007)",
        "000001Alice\n000002Bob                 MTH\n000003\n",
    ),
    Fixture(
        "display_numeric_edges",
        "spaces and non-digits inside PIC 9 fields (§4)",
        "AB C12Bob                 X Y9 \n12 456Carol               ZZZ7 \n      Dana                ART  \n",
    ),
    Fixture(
        "wide_values",
        "fields filled to their full width",
        make_record("999999", "X" * 20, "ZZZ", "99") + "\n",
    ),
    Fixture("blank_line", "an entirely blank input record", "\n000007Grace               BIO09\n"),
    Fixture(
        "embedded_spaces",
        "leading and trailing spaces inside the X(20)/XXX fields must survive verbatim",
        "000001  Alice             MTH07\n000002Bob                  Y 09\n",
    ),
    Fixture(
        "multibyte_utf8",
        "multibyte UTF-8 in the name field: COBOL applies the layout to bytes, not characters",
        "000003שלום עולם          HEB04\n000004Renée               FRA05\n",
    ),
)
