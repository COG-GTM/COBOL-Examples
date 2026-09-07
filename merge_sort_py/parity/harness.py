"""Reconciliation harness: runs GnuCOBOL and the Python port over identical fixtures,
diffs every emitted byte field by field, and exits non-zero on any diff.

Without GnuCOBOL the harness SKIPs (exit code 2) — it never reports parity.
"""

from __future__ import annotations

import argparse
import sys
from collections.abc import Callable
from dataclasses import dataclass
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.cobol_types import (  # noqa: E402
    COMMENT_OFFSET,
    CONTRACT_ID_OFFSET,
    CUSTOMER_ID_OFFSET,
    FIRST_NAME_OFFSET,
    LAST_NAME_OFFSET,
    to_bytes,
)
from app.merge_sort import MergeSortRun, run_program, run_with_inputs  # noqa: E402
from parity.fixtures import FIXTURES, Fixture  # noqa: E402
from parity.oracle import (  # noqa: E402
    CobolResult,
    CobolToolchainMissing,
    cobc_available,
    run_cobol_program,
    run_cobol_with_inputs,
)

EXIT_OK = 0
EXIT_DIFF = 1
EXIT_SKIPPED = 2

PROGRAM_FIXTURE = "program_default_data"

ProgramRunner = Callable[[], MergeSortRun]
InputsRunner = Callable[[str, str], MergeSortRun]

_FIELDS: tuple[tuple[str, int, int], ...] = (
    ("f-customer-id", CUSTOMER_ID_OFFSET, 5),
    ("f-customer-last-name", LAST_NAME_OFFSET, 50),
    ("f-customer-first-name", FIRST_NAME_OFFSET, 50),
    ("f-customer-contract-id", CONTRACT_ID_OFFSET, 5),
    ("f-customer-comment", COMMENT_OFFSET, 25),
)


@dataclass
class FixtureResult:
    name: str
    boundary: str
    diffs: list[str]

    @property
    def ok(self) -> bool:
        return not self.diffs


def _diff_record_line(label: str, index: int, expected: bytes, actual: bytes) -> list[str]:
    """Diff one emitted line. Record-shaped lines are compared field by field."""
    diffs: list[str] = []
    padded_expected = expected.ljust(135)
    padded_actual = actual.ljust(135)
    for name, offset, length in _FIELDS:
        want = padded_expected[offset : offset + length]
        got = padded_actual[offset : offset + length]
        if want != got:
            diffs.append(f"{label} line {index + 1} {name}: cobol={want!r} python={got!r}")
    if not diffs:
        diffs.append(f"{label} line {index + 1}: cobol={expected!r} python={actual!r}")
    return diffs


def _diff_stream(label: str, expected: bytes, actual: bytes) -> list[str]:
    if expected == actual:
        return []
    expected_lines = expected.split(b"\n")
    actual_lines = actual.split(b"\n")
    diffs: list[str] = []
    if len(expected_lines) != len(actual_lines):
        diffs.append(f"{label}: line count cobol={len(expected_lines)} python={len(actual_lines)}")
    for index in range(max(len(expected_lines), len(actual_lines))):
        want = expected_lines[index] if index < len(expected_lines) else b"<missing>"
        got = actual_lines[index] if index < len(actual_lines) else b"<missing>"
        if want != got:
            diffs.extend(_diff_record_line(label, index, want, got))
    return diffs


def _compare(cobol: CobolResult, port: MergeSortRun, *, include_inputs: bool) -> list[str]:
    diffs: list[str] = []
    if include_inputs:
        diffs += _diff_stream("test-file-1.txt", cobol.test_file_1, to_bytes(port.test_file_1))
        diffs += _diff_stream("test-file-2.txt", cobol.test_file_2, to_bytes(port.test_file_2))
    diffs += _diff_stream("merge-output.txt", cobol.merge_output, to_bytes(port.merge_output))
    diffs += _diff_stream(
        "sorted-contract-id.txt", cobol.sorted_contract_id, to_bytes(port.sorted_contract_id)
    )
    diffs += _diff_stream("console", cobol.console, to_bytes(port.console_text()))
    return diffs


def reconcile_program(port: ProgramRunner = run_program) -> FixtureResult:
    """Diff the *real* legacy program, inputs included, against ``run_program()``."""
    cobol = run_cobol_program()
    return FixtureResult(
        PROGRAM_FIXTURE,
        "the legacy program's own create-test-data rows, run unmodified",
        _compare(cobol, port(), include_inputs=True),
    )


def reconcile(fixture: Fixture, port: InputsRunner = run_with_inputs) -> FixtureResult:
    """Diff one fixture against the stand-in oracle."""
    cobol = run_cobol_with_inputs(fixture.test_file_1, fixture.test_file_2)
    result = port(
        fixture.test_file_1.decode("latin-1"),
        fixture.test_file_2.decode("latin-1"),
    )
    return FixtureResult(fixture.name, fixture.boundary, _compare(cobol, result, include_inputs=False))


def reconcile_all(
    program_port: ProgramRunner = run_program,
    inputs_port: InputsRunner = run_with_inputs,
) -> list[FixtureResult]:
    results = [reconcile_program(program_port)]
    results += [reconcile(fixture, inputs_port) for fixture in FIXTURES]
    return results


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--quiet", action="store_true", help="only print diffs and the summary")
    args = parser.parse_args(argv)

    if not cobc_available():
        print("SKIPPED: GnuCOBOL 'cobc' not found; parity was NOT verified", file=sys.stderr)
        return EXIT_SKIPPED

    try:
        results = reconcile_all()
    except CobolToolchainMissing as exc:  # pragma: no cover - guarded above
        print(f"SKIPPED: {exc}", file=sys.stderr)
        return EXIT_SKIPPED

    total_diffs = 0
    for result in results:
        total_diffs += len(result.diffs)
        if not args.quiet or result.diffs:
            status = "OK  " if result.ok else "DIFF"
            print(f"[{status}] {result.name} — {result.boundary}")
        for diff in result.diffs:
            print(f"       {diff}")

    print(f"\n{len(results)} fixtures, {total_diffs} diffs")
    return EXIT_OK if total_diffs == 0 else EXIT_DIFF


if __name__ == "__main__":
    raise SystemExit(main())
