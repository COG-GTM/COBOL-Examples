"""The parity gate: the Python port must match GnuCOBOL byte for byte.

Without GnuCOBOL these tests SKIP — they never pass silently.
"""

from __future__ import annotations

import random
import string

import pytest

from app.report_writer import ReportRun, run_report
from parity.fixtures import FIXTURES, Fixture
from parity.harness import EXIT_SKIPPED, main, reconcile
from parity.oracle import cobc_available

requires_cobc = pytest.mark.skipif(not cobc_available(), reason="GnuCOBOL 'cobc' is not installed")


@requires_cobc
@pytest.mark.parametrize("fixture", FIXTURES, ids=lambda f: f.name)
def test_parity_with_cobol(fixture: Fixture) -> None:
    result = reconcile(fixture)
    assert result.ok, "\n".join(result.diffs)


@requires_cobc
def test_seeded_regression_is_caught_by_the_harness() -> None:
    """Swap the display-numeric move for int reformatting and the gate must fail."""

    def broken_port(input_text: str) -> ReportRun:
        run = run_report(input_text)
        for page in run.pages:
            page.lines = [line.replace("000000", "     0") for line in page.lines]
        return run

    diffing = [reconcile(fixture, broken_port) for fixture in FIXTURES]
    assert any(not result.ok for result in diffing), "seeded regression was not detected"


@requires_cobc
def test_parity_over_randomised_records() -> None:
    """Differential check beyond the curated fixtures, over arbitrary record bytes.

    Seeded so a failure is reproducible; the alphabet spans digits, letters, spaces and
    multibyte characters, and record lengths straddle the 31-byte layout.
    """
    rng = random.Random(20260907)
    alphabet = string.digits + string.ascii_letters + "   " + "אשé€"
    for case in range(5):
        rows = [
            "".join(rng.choice(alphabet) for _ in range(rng.randint(0, 40)))
            for _ in range(rng.randint(1, 45))
        ]
        text = "".join(row + "\n" for row in rows)
        fixture = Fixture(f"random_{case}", "randomised differential case", text)
        result = reconcile(fixture)
        assert result.ok, "\n".join(result.diffs)


def test_harness_skips_without_cobol_toolchain(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setattr("parity.harness.cobc_available", lambda: False)
    assert main([]) == EXIT_SKIPPED
