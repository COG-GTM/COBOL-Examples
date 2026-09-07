"""Python port of ``merge_sort/merge_sort_test.cbl`` (program-id ``merge-sort-example``).

The port is paragraph for paragraph: :func:`create_test_data`, :func:`merge_and_display_files`
and :func:`sort_and_display_file` mirror the COBOL paragraphs of the same name, and
:func:`main_procedure` mirrors ``main-procedure``.

Nothing here touches the filesystem: the four files the COBOL program reads and writes are
carried as in-memory text, so the legacy tree can never be written to.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Final

from app.cobol_types import (
    COMMENT_LENGTH,
    CONTRACT_ID_LENGTH,
    CUSTOMER_ID_LENGTH,
    FIRST_NAME_LENGTH,
    LAST_NAME_LENGTH,
    RECORD_LENGTH,
    join_records,
    split_records,
)
from app.record import CustomerRecord

STATUS_OK: Final = "00"

# Console literals, reproduced character for character from the COBOL DISPLAY statements.
MSG_CREATING: Final = "Creating test data files..."
MSG_MERGING: Final = "Merging and sorting files..."
MSG_SORTING: Final = "Sorting merged file on descending contract id...."
MSG_DONE: Final = "Done."
MSG_OPEN_OUTPUT_FAILED: Final = "Failed to open file for output: "
MSG_OPEN_MERGED_FAILED: Final = "Error opening merged output file: "
MSG_OPEN_SORTED_FAILED: Final = "Error opening sorted output file: "

# Bounds on a single service run. The COBOL program has no such limit; the service does,
# so a request cannot be amplified into unbounded memory (see README, "Limits").
MAX_RECORDS: Final = 20_000

# ``create-test-data``: the literal MOVEs of the COBOL paragraph, in write order.
TEST_DATA_EAST: Final[tuple[tuple[int, str, str, int, str], ...]] = (
    (1, "last-1", "first-1", 5423, "comment-1"),
    (5, "last-5", "first-5", 12323, "comment-5"),
    (10, "last-10", "first-10", 653, "comment-10"),
    (50, "last-50", "first-50", 5050, "comment-50"),
    (25, "last-25", "first-25", 7725, "comment-25"),
    (75, "last-75", "first-75", 1175, "comment-75"),
)
TEST_DATA_WEST: Final[tuple[tuple[int, str, str, int, str], ...]] = (
    (999, "last-999", "first-999", 1610, "comment-99"),
    (3, "last-03", "first-03", 3331, "comment-03"),
    (30, "last-30", "first-30", 8765, "comment-30"),
    (85, "last-85", "first-85", 4567, "comment-85"),
    (24, "last-24", "first-24", 247, "comment-24"),
)


class RecordLimitExceeded(ValueError):
    """Raised when a request carries more records than the service will process."""


@dataclass
class MergeSortRun:
    """Everything one execution of the program produced."""

    console: list[str] = field(default_factory=list)
    test_file_1: str = ""
    test_file_2: str = ""
    merge_output: str = ""
    sorted_contract_id: str = ""
    merged_records: list[CustomerRecord] = field(default_factory=list)
    sorted_records: list[CustomerRecord] = field(default_factory=list)
    file_status_1: str = STATUS_OK
    file_status_2: str = STATUS_OK
    file_status_merge: str = STATUS_OK
    file_status_sorted: str = STATUS_OK
    stopped_early: bool = False
    generated: bool = False

    def console_text(self) -> str:
        return "".join(line + "\n" for line in self.console)


def _move_record(
    customer_id: int | str,
    last_name: str,
    first_name: str,
    contract_id: int | str,
    comment: str,
) -> CustomerRecord:
    """The five MOVEs of one ``create-test-data`` block, into a 135 byte record area.

    ``PIC 9(5)`` receives a numeric literal right justified and zero filled; ``PIC X(n)``
    receives an alphanumeric literal left justified and space filled.
    """
    numeric_id = f"{customer_id:0{CUSTOMER_ID_LENGTH}d}" if isinstance(customer_id, int) else customer_id
    numeric_contract = (
        f"{contract_id:0{CONTRACT_ID_LENGTH}d}" if isinstance(contract_id, int) else contract_id
    )
    raw = (
        numeric_id[:CUSTOMER_ID_LENGTH]
        + last_name.ljust(LAST_NAME_LENGTH)[:LAST_NAME_LENGTH]
        + first_name.ljust(FIRST_NAME_LENGTH)[:FIRST_NAME_LENGTH]
        + numeric_contract[:CONTRACT_ID_LENGTH]
        + comment.ljust(COMMENT_LENGTH)[:COMMENT_LENGTH]
    )
    assert len(raw) == RECORD_LENGTH
    return CustomerRecord(raw)


def create_test_data(run: MergeSortRun) -> None:
    """Port of ``create-test-data``.

    The COBOL paragraph opens each test file for output, checks the file status, writes its
    literal rows and closes the file. In memory the open always succeeds, but the guard is
    ported so a non-"00" status stops the run exactly where the COBOL does.
    """
    run.console.append(MSG_CREATING)

    if run.file_status_1 != STATUS_OK:
        run.console.append(MSG_OPEN_OUTPUT_FAILED + run.file_status_1)
        run.stopped_early = True
        return
    run.test_file_1 = join_records([_move_record(*row).raw for row in TEST_DATA_EAST])

    if run.file_status_2 != STATUS_OK:
        run.console.append(MSG_OPEN_OUTPUT_FAILED + run.file_status_2)
        run.stopped_early = True
        return
    run.test_file_2 = join_records([_move_record(*row).raw for row in TEST_DATA_WEST])


def _read_input_file(text: str) -> list[CustomerRecord]:
    records = [CustomerRecord(raw) for raw in split_records(text)]
    if len(records) > MAX_RECORDS:
        raise RecordLimitExceeded(f"input carries more than {MAX_RECORDS} records")
    return records


def merge_and_display_files(run: MergeSortRun) -> None:
    """Port of ``merge-and-display-files``.

    ``merge fd-sorting-file on ascending key f-customer-id using fd-test-file-1
    fd-test-file-2 giving fd-merged-file`` — GnuCOBOL fully *sorts* the concatenation of
    the two inputs (it does not assume they arrive ordered, ruling D-001) and the ordering
    is stable, so equal keys keep file-1-then-file-2 order and their order within a file.
    """
    run.console.append(MSG_MERGING)

    records = _read_input_file(run.test_file_1) + _read_input_file(run.test_file_2)
    if len(records) > MAX_RECORDS:
        raise RecordLimitExceeded(f"input carries more than {MAX_RECORDS} records")
    merged = sorted(records, key=lambda record: record.customer_id_sort_value)

    run.merge_output = join_records([record.raw for record in merged])

    # ``open input fd-merged-file`` + status guard + the read/display loop.
    if run.file_status_merge != STATUS_OK:
        run.console.append(MSG_OPEN_MERGED_FAILED + run.file_status_merge)
        run.stopped_early = True
        return

    run.merged_records = [CustomerRecord(raw) for raw in split_records(run.merge_output)]
    run.console.extend(record.display_line() for record in run.merged_records)


def sort_and_display_file(run: MergeSortRun) -> None:
    """Port of ``sort-and-display-file``.

    ``sort ... on descending key f-customer-contract-id using fd-merged-file giving
    fd-sorted-contract-id``. The input is the *file* the merge just wrote, so the records
    are re-read through line sequential semantics before being sorted. Descending is a
    reversed key, not a reversed result: equal contract ids keep their merged-file order.
    """
    run.console.append(MSG_SORTING)

    records = _read_input_file(run.merge_output)
    ordered = sorted(records, key=lambda record: -record.contract_id_sort_value)

    run.sorted_contract_id = join_records([record.raw for record in ordered])

    if run.file_status_sorted != STATUS_OK:
        run.console.append(MSG_OPEN_SORTED_FAILED + run.file_status_sorted)
        run.stopped_early = True
        return

    run.sorted_records = [CustomerRecord(raw) for raw in split_records(run.sorted_contract_id)]
    run.console.extend(record.display_line() for record in run.sorted_records)


def main_procedure(run: MergeSortRun) -> MergeSortRun:
    """Port of ``main-procedure``: create, merge, sort, ``display "Done."``, ``stop run``."""
    create_test_data(run)
    if run.stopped_early:
        return run

    merge_and_display_files(run)
    if run.stopped_early:
        return run

    sort_and_display_file(run)
    if run.stopped_early:
        return run

    run.console.append(MSG_DONE)
    return run


def run_program() -> MergeSortRun:
    """The whole COBOL program, including its own ``create-test-data`` inputs."""
    return main_procedure(MergeSortRun(generated=True))


def run_with_inputs(test_file_1: str, test_file_2: str) -> MergeSortRun:
    """Run the merge/sort paragraphs over caller-supplied input files.

    ``create-test-data`` is skipped: the caller has already provided ``test-file-1.txt`` and
    ``test-file-2.txt``. This is the shape the parity oracle runs too, so both sides of the
    harness execute the same statements.
    """
    run = MergeSortRun(test_file_1=test_file_1, test_file_2=test_file_2)

    merge_and_display_files(run)
    if run.stopped_early:
        return run

    sort_and_display_file(run)
    if run.stopped_early:
        return run

    run.console.append(MSG_DONE)
    return run
