"""Reconciliation harness: runs the compiled COBOL and the Python port over
identical stdin fixtures, diffs their stdout byte for byte, and exits non-zero
on any diff.

Without GnuCOBOL the harness SKIPs (exit code 2) — it never reports parity.
"""

from __future__ import annotations

import argparse
import sys
from collections.abc import Callable
from dataclasses import dataclass
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.search_program import run_program  # noqa: E402
from parity.fixtures import FIXTURES, Fixture  # noqa: E402
from parity.oracle import CobolToolchainMissing, cobc_available, run_cobol  # noqa: E402

EXIT_OK = 0
EXIT_DIFF = 1
EXIT_SKIPPED = 2

PortRunner = Callable[[bytes], bytes]


@dataclass
class FixtureResult:
    fixture: Fixture
    diffs: list[str]

    @property
    def ok(self) -> bool:
        return not self.diffs


def _diff_lines(fixture: Fixture, cobol_stdout: bytes, python_stdout: bytes) -> list[str]:
    cobol_lines = cobol_stdout.split(b"\n")
    python_lines = python_stdout.split(b"\n")
    diffs: list[str] = []
    if len(cobol_lines) != len(python_lines):
        diffs.append(f"line count cobol={len(cobol_lines)} python={len(python_lines)}")
    for index in range(max(len(cobol_lines), len(python_lines))):
        expected = cobol_lines[index] if index < len(cobol_lines) else b"<missing>"
        actual = python_lines[index] if index < len(python_lines) else b"<missing>"
        if expected != actual:
            diffs.append(f"line {index + 1}\n  cobol : {expected!r}\n  python: {actual!r}")
    return diffs


def reconcile(fixture: Fixture, port: PortRunner = run_program) -> FixtureResult:
    """Diff one fixture byte for byte. Raises ``CobolToolchainMissing`` if no cobc."""
    cobol_stdout = run_cobol(fixture.stdin_bytes)
    python_stdout = port(fixture.stdin_bytes)
    return FixtureResult(fixture, _diff_lines(fixture, cobol_stdout, python_stdout))


def reconcile_all(port: PortRunner = run_program) -> list[FixtureResult]:
    return [reconcile(fixture, port) for fixture in FIXTURES]


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--quiet", action="store_true", help="only print the summary")
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
            print(f"[{status}] {result.fixture.name} — {result.fixture.boundary}")
        for diff in result.diffs:
            print(f"       {diff}")

    print(f"\n{len(results)} fixtures, {total_diffs} diffs")
    return EXIT_OK if total_diffs == 0 else EXIT_DIFF


if __name__ == "__main__":
    raise SystemExit(main())
