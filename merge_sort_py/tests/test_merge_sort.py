"""The ported paragraphs (contracts D-001, D-003, D-008, D-010)."""

from __future__ import annotations

import pytest

from app.merge_sort import (
    MAX_RECORDS,
    MSG_CREATING,
    MSG_DONE,
    MSG_MERGING,
    MSG_OPEN_MERGED_FAILED,
    MSG_OPEN_OUTPUT_FAILED,
    MSG_OPEN_SORTED_FAILED,
    MSG_SORTING,
    MergeSortRun,
    RecordLimitExceeded,
    run_program,
    run_with_inputs,
)


def record(customer_id: str, tag: str, contract_id: str) -> str:
    return customer_id + tag.ljust(50) + tag.ljust(50) + contract_id + tag.ljust(25)


def file_of(*records: str) -> str:
    return "".join(line.rstrip(" ") + "\n" for line in records)


def test_generated_run_matches_the_programs_shape():
    run = run_program()
    assert len(run.merged_records) == 11
    assert len(run.sorted_records) == 11
    assert [r.customer_id for r in run.merged_records] == [
        "00001",
        "00003",
        "00005",
        "00010",
        "00024",
        "00025",
        "00030",
        "00050",
        "00075",
        "00085",
        "00999",
    ]
    assert [r.contract_id for r in run.sorted_records] == [
        "12323",
        "08765",
        "07725",
        "05423",
        "05050",
        "04567",
        "03331",
        "01610",
        "01175",
        "00653",
        "00247",
    ]


def test_console_literals_and_order():
    run = run_program()
    assert run.console[0] == MSG_CREATING
    assert run.console[1] == MSG_MERGING
    assert run.console[13] == MSG_SORTING
    assert run.console[-1] == MSG_DONE
    assert all(len(line) == 135 for line in run.console[2:13])


def test_no_deduplication_and_stable_merge_order():
    """D-003: equal customer ids keep file-1 rows first, in input order."""
    file_1 = file_of(record("00007", "A1", "00100"), record("00007", "A2", "00100"))
    file_2 = file_of(record("00007", "B1", "00100"), record("00007", "B2", "00100"))
    run = run_with_inputs(file_1, file_2)

    assert len(run.merged_records) == 4
    assert [r.last_name.rstrip() for r in run.merged_records] == ["A1", "A2", "B1", "B2"]
    # Descending on an all-equal contract id must not reverse the group.
    assert [r.last_name.rstrip() for r in run.sorted_records] == ["A1", "A2", "B1", "B2"]


def test_descending_sort_keeps_merged_order_within_equal_contract_ids():
    file_1 = file_of(
        record("00001", "c1", "00500"),
        record("00002", "c2", "00900"),
        record("00003", "c3", "00500"),
    )
    run = run_with_inputs(file_1, "")
    assert [r.last_name.rstrip() for r in run.sorted_records] == ["c2", "c1", "c3"]


def test_empty_inputs_produce_an_empty_run_not_an_error():
    run = run_with_inputs("", "")
    assert run.merged_records == []
    assert run.sorted_records == []
    assert run.merge_output == ""
    assert run.stopped_early is False
    assert run.console[-1] == MSG_DONE


def test_unsorted_inputs_are_fully_sorted():
    """D-001: MERGE's inputs are not pre-sorted and GnuCOBOL sorts them anyway."""
    file_1 = file_of(record("00050", "e", "00001"), record("00010", "f", "00002"))
    run = run_with_inputs(file_1, "")
    assert [r.customer_id for r in run.merged_records] == ["00010", "00050"]


def test_blank_key_sorts_to_the_top_of_the_ascending_merge():
    """D-002: an all-blank PIC 9(5) keys as 4294967280."""
    file_1 = file_of(record("     ", "blank", "00001"), record("99999", "nines", "00002"))
    run = run_with_inputs(file_1, "")
    assert [r.last_name.rstrip() for r in run.merged_records] == ["nines", "blank"]


@pytest.mark.parametrize(
    ("field", "message"),
    [
        ("file_status_1", MSG_OPEN_OUTPUT_FAILED),
        ("file_status_2", MSG_OPEN_OUTPUT_FAILED),
    ],
)
def test_create_test_data_open_guards_stop_the_run(field, message):
    """D-008: every open guard in the source has a counterpart here."""
    from app.merge_sort import main_procedure

    run = MergeSortRun()
    setattr(run, field, "35")
    main_procedure(run)
    assert run.stopped_early is True
    assert run.console[-1] == message + "35"
    assert MSG_DONE not in run.console


def test_merge_and_sort_open_guards_stop_the_run():
    run = MergeSortRun(file_status_merge="37")
    run_with_inputs_run = run
    from app.merge_sort import merge_and_display_files

    merge_and_display_files(run_with_inputs_run)
    assert run.stopped_early is True
    assert run.console[-1] == MSG_OPEN_MERGED_FAILED + "37"

    run2 = MergeSortRun(file_status_sorted="39")
    from app.merge_sort import sort_and_display_file

    sort_and_display_file(run2)
    assert run2.stopped_early is True
    assert run2.console[-1] == MSG_OPEN_SORTED_FAILED + "39"


def test_record_limit_is_enforced():
    """D-010: a service-only limit, which rejects rather than truncating."""
    oversized = file_of(*[record("00001", "x", "00001")] * (MAX_RECORDS + 1))
    with pytest.raises(RecordLimitExceeded):
        run_with_inputs(oversized, "")


def test_short_and_long_lines_are_padded_and_truncated():
    run = run_with_inputs("00042short\n" + "0" * 5 + "z" * 200 + "\n", "")
    assert len(run.merged_records) == 2
    assert all(len(r.raw) == 135 for r in run.merged_records)
