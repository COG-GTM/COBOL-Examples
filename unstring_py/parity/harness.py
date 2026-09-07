"""Reconciliation harness: runs the COBOL and the Python port over identical fixtures,
diffs stdout **byte for byte**, and exits non-zero on any diff.

Without GnuCOBOL the harness SKIPs (exit code 2) — it never reports parity.
"""

from __future__ import annotations

import argparse
import sys
from collections.abc import Callable
from dataclasses import dataclass, field
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.program import Program, run_program  # noqa: E402
from parity.fixtures import FIXTURES, Fixture  # noqa: E402
from parity.oracle import CobolToolchainMissing, cobc_available, run_legacy, run_probe  # noqa: E402

EXIT_OK = 0
EXIT_DIFF = 1
EXIT_SKIPPED = 2

PortRunner = Callable[..., Program]


@dataclass
class FixtureResult:
    fixture: Fixture
    diffs: list[str] = field(default_factory=list)

    @property
    def ok(self) -> bool:
        return not self.diffs


def _diff_bytes(fixture: Fixture, cobol: bytes, python: bytes) -> list[str]:
    cobol_lines = cobol.split(b"\n")
    python_lines = python.split(b"\n")
    diffs: list[str] = []
    if len(cobol_lines) != len(python_lines):
        diffs.append(f"line count cobol={len(cobol_lines)} python={len(python_lines)}")
    for index in range(max(len(cobol_lines), len(python_lines))):
        expected = cobol_lines[index] if index < len(cobol_lines) else b"<missing>"
        actual = python_lines[index] if index < len(python_lines) else b"<missing>"
        if expected != actual:
            diffs.append(f"line {index + 1}\n         cobol : {expected!r}\n         python: {actual!r}")
    return diffs


def cobol_stdout(fixture: Fixture) -> bytes:
    """Oracle output for one fixture: the legacy binary, or the generated probe."""
    if fixture.legacy:
        return run_legacy()
    return run_probe(
        fixture.simple_source,
        fixture.multi_delim_source,
        fixture.multi_dest_source,
        fixture.delimiter,
        fixture.amount,
    )


def python_stdout(fixture: Fixture, port: PortRunner | None = None) -> bytes:
    # Resolved at call time so a seeded-regression port can be substituted.
    port = port or run_program
    return port(
        simple_source=fixture.simple_source,
        multi_delim_source=fixture.multi_delim_source,
        multi_dest_source=fixture.multi_dest_source,
        delimiter=fixture.delimiter,
        source_num=fixture.amount,
    ).stdout_bytes()


def reconcile(fixture: Fixture, port: PortRunner | None = None) -> FixtureResult:
    """Diff one fixture. Raises ``CobolToolchainMissing`` when ``cobc`` is absent."""
    return FixtureResult(fixture, _diff_bytes(fixture, cobol_stdout(fixture), python_stdout(fixture, port)))


def reconcile_all(port: PortRunner | None = None) -> list[FixtureResult]:
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
