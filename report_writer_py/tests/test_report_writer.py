"""Unit tests for the port, citing the rulings in docs/phase0-contracts.md."""

from __future__ import annotations

from app.cobol_types import cobol_edit_zz9, cobol_move_display_numeric
from app.record import RECORD_LENGTH, TestRecord
from app.report_writer import (
    DETAIL_LINES_PER_PAGE,
    MSG_DONE,
    MSG_GENERATE,
    MSG_STARTING,
    PAGE_ADVANCE,
    run_report,
)
from parity.fixtures import make_record


def test_record_is_padded_to_31_bytes() -> None:
    record = TestRecord.from_line("000001Alice")
    assert len(record.record_area) == RECORD_LENGTH
    assert record.student_name == "Alice"
    assert record.num_courses_raw == "  "
    assert record.num_courses == 0


def test_display_numeric_move_keeps_non_digits_and_zero_fills_spaces() -> None:
    assert cobol_move_display_numeric("AB C12", 6) == "AB0C12"
    assert cobol_move_display_numeric("      ", 6) == "000000"
    assert cobol_move_display_numeric("9 ", 2) == "90"


def test_page_counter_uses_zz9_editing() -> None:
    assert cobol_edit_zz9(1) == "  1"
    assert cobol_edit_zz9(12) == " 12"
    assert cobol_edit_zz9(123) == "123"


def test_detail_column_positions() -> None:
    run = run_report(make_record("000042", "Alice", "MTH", "07") + "\n")
    line = run.pages[0].lines[5]
    assert line[3:9] == "000042"
    assert line[14:34] == "Alice".ljust(20)
    assert line[39:42] == "MTH"
    assert line[45:47] == "07"


def test_report_heading_only_on_first_page() -> None:
    run = run_report("".join(make_record(f"{i:06d}", f"N{i}", "PHY", "01") + "\n" for i in range(60)))
    assert run.pages[0].lines[0].strip() == "Customer Order Report"
    assert run.pages[0].lines[1][99:103] == "PAGE"
    assert run.pages[0].lines[1][104:107] == "  1"
    assert all(not line.strip() for line in run.pages[1].lines[:5])


def test_d001_final_record_is_generated_twice() -> None:
    run = run_report(make_record("000001", "Alice", "MTH", "07") + "\n")
    assert run.record_count == 1
    assert run.detail_count == 2
    assert run.console.count(MSG_GENERATE) == 2
    assert run.console[0] == MSG_STARTING
    assert run.console[-1] == MSG_DONE


def test_d001_empty_input_still_emits_the_unloaded_record_area() -> None:
    run = run_report("")
    assert run.record_count == 0
    assert run.detail_count == 1
    assert run.pages[0].lines[5] == "   000000" + " " * 36 + "00"


def test_strict_eof_opt_in_suppresses_the_duplicate() -> None:
    text = make_record("000001", "Alice", "MTH", "07") + "\n"
    assert run_report(text, strict_eof=True).detail_count == 1
    assert run_report(text).detail_count == 2


def test_page_rollover_boundary() -> None:
    exactly_full = "".join(
        make_record(f"{i:06d}", f"N{i}", "PHY", "01") + "\n" for i in range(DETAIL_LINES_PER_PAGE - 1)
    )
    run = run_report(exactly_full)
    assert run.detail_count == DETAIL_LINES_PER_PAGE
    assert len(run.pages) == 1

    one_more = exactly_full + make_record("999999", "Overflow", "BIO", "02") + "\n"
    run = run_report(one_more)
    assert len(run.pages) == 2
    assert run.pages[1].lines[5].strip().startswith("999999")


def test_physical_line_count_is_65_per_page_plus_one() -> None:
    run = run_report("")
    lines = run.report_text().split("\n")
    assert len(lines) == PAGE_ADVANCE * len(run.pages) + 2  # trailing blank line + final newline
