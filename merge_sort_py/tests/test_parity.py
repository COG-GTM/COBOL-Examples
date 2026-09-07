"""The parity gate.

These tests are the migration's evidence: they run real compiled GnuCOBOL and diff it against the
port byte for byte. Without ``cobc`` they SKIP — they never pass silently.
"""

from __future__ import annotations

import subprocess
import sys
from pathlib import Path

import pytest

from app.merge_sort import MergeSortRun, run_with_inputs
from app.record import CustomerRecord
from parity import harness
from parity.fixtures import FIXTURES, FIXTURES_BY_NAME
from parity.oracle import cobc_available, run_cobol_program, run_cobol_with_inputs

pytestmark = pytest.mark.skipif(not cobc_available(), reason="GnuCOBOL 'cobc' is not installed")

HARNESS = Path(harness.__file__)


def test_the_unmodified_legacy_program_matches_the_port():
    result = harness.reconcile_program()
    assert result.diffs == [], "\n".join(result.diffs)


@pytest.mark.parametrize("fixture", FIXTURES, ids=lambda fixture: fixture.name)
def test_fixture_matches_the_oracle_byte_for_byte(fixture):
    result = harness.reconcile(fixture)
    assert result.diffs == [], f"{fixture.boundary}\n" + "\n".join(result.diffs)


def test_stand_in_oracle_reproduces_the_real_program():
    """The stand-in only omits create-test-data; over the same rows it must be identical."""
    real = run_cobol_program()
    stand_in = run_cobol_with_inputs(real.test_file_1, real.test_file_2)
    assert stand_in.merge_output == real.merge_output
    assert stand_in.sorted_contract_id == real.sorted_contract_id
    # The real program's console additionally carries the create-test-data line.
    assert stand_in.console == real.console.replace(b"Creating test data files...\n", b"", 1)


def test_harness_cli_reports_zero_diffs_and_exits_zero():
    completed = subprocess.run(
        [sys.executable, str(HARNESS)],
        capture_output=True,
        text=True,
        check=False,
        cwd=HARNESS.parents[1],
    )
    assert completed.returncode == 0, completed.stdout + completed.stderr
    assert f"{len(FIXTURES) + 1} fixtures, 0 diffs" in completed.stdout


def test_seeded_regression_is_caught():
    """Prove the gate has teeth: break the port and the harness must report diffs.

    The regression is the mistake this migration was most likely to make — treating a DESCENDING
    key as ``reverse=True`` over an *unstable* order, here modelled by reversing tie groups.
    """

    def broken(test_file_1: str, test_file_2: str) -> MergeSortRun:
        run = run_with_inputs(test_file_1, test_file_2)
        run.sorted_records = list(reversed(run.sorted_records))
        run.sorted_contract_id = "".join(record.file_line() + "\n" for record in run.sorted_records)
        return run

    result = harness.reconcile(FIXTURES_BY_NAME["duplicate_contract_ids"], broken)
    assert result.diffs, "the harness failed to notice a reversed sort order"
    assert any("sorted-contract-id.txt" in diff for diff in result.diffs)


def test_seeded_key_regression_is_caught():
    """A subtler regression: sorting PIC 9(5) as text instead of as a converted number."""

    def broken(test_file_1: str, test_file_2: str) -> MergeSortRun:
        run = run_with_inputs(test_file_1, test_file_2)
        records = list(run.merged_records)
        records.sort(key=lambda record: record.customer_id)  # naive text ordering
        run.merged_records = records
        run.merge_output = "".join(record.file_line() + "\n" for record in records)
        return run

    result = harness.reconcile(FIXTURES_BY_NAME["non_numeric_keys"], broken)
    assert result.diffs, "the harness failed to notice a text-ordered PIC 9(5) key"
    assert any("f-customer-id" in diff for diff in result.diffs)


def test_padding_regression_is_caught():
    """Stripping PIC X(n) padding before writing must be caught (D-004)."""

    def broken(test_file_1: str, test_file_2: str) -> MergeSortRun:
        run = run_with_inputs(test_file_1, test_file_2)
        stripped = [
            CustomerRecord(
                record.customer_id
                + record.last_name.strip().ljust(50)
                + record.first_name.ljust(50)
                + record.contract_id
                + record.comment
            )
            for record in run.merged_records
        ]
        run.merged_records = stripped
        run.merge_output = "".join(record.file_line() + "\n" for record in stripped)
        return run

    result = harness.reconcile(FIXTURES_BY_NAME["padding_is_data"], broken)
    assert result.diffs, "the harness failed to notice stripped X(n) padding"


def test_fixtures_cross_every_documented_boundary():
    boundaries = " ".join(fixture.boundary for fixture in FIXTURES)
    for ruling in ("D-001", "D-002", "D-003", "D-004", "D-005", "D-006", "D-007"):
        assert ruling in boundaries, f"no fixture crosses {ruling}"


def test_harness_skips_when_cobc_is_missing(monkeypatch, tmp_path):
    """A missing toolchain must SKIP (exit 2), never report parity."""
    empty_path = tmp_path / "empty-bin"
    empty_path.mkdir()
    monkeypatch.setenv("PATH", str(empty_path))
    completed = subprocess.run(
        [sys.executable, str(HARNESS)],
        capture_output=True,
        text=True,
        check=False,
        cwd=HARNESS.parents[1],
        env={"PATH": str(empty_path), "PYTHONPATH": str(HARNESS.parents[1])},
    )
    assert completed.returncode == harness.EXIT_SKIPPED
    assert "SKIPPED" in completed.stderr
    assert "0 diffs" not in completed.stdout
