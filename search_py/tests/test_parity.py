"""The parity gate: the Python port must match GnuCOBOL byte for byte.

Without GnuCOBOL these tests SKIP — they never pass silently.
"""

from __future__ import annotations

import random
import string

import pytest

from app.cobol_types import Pic9
from app.search_program import run_program
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
def test_seeded_regression_is_caught_by_the_harness(monkeypatch: pytest.MonkeyPatch) -> None:
    """Replace the ACCEPT rules with naive int parsing and the gate must fail."""

    def naive_accept(line: bytes, size: int = 4) -> Pic9:
        text = line.decode("latin-1").strip()
        value = int(text) if text.lstrip("+-").isdigit() else 0
        return Pic9(str(abs(value) % 10**size).rjust(size, "0").encode("latin-1"))

    monkeypatch.setattr("app.search_program.accept_pic9", naive_accept)
    diffing = [reconcile(fixture, run_program) for fixture in FIXTURES]
    assert any(not result.ok for result in diffing), "seeded regression was not detected"


@requires_cobc
def test_seeded_regression_in_the_record_image_is_caught() -> None:
    """Trim PIC X(16) padding — a plausible port bug — and the gate must fail."""

    def broken_port(stdin_bytes: bytes) -> bytes:
        return run_program(stdin_bytes).replace(b"test item 3     ", b"test item 3")

    diffing = [reconcile(fixture, broken_port) for fixture in FIXTURES]
    assert any(not result.ok for result in diffing), "seeded regression was not detected"


@requires_cobc
def test_parity_over_randomised_sessions() -> None:
    """Differential check beyond the curated fixtures, over arbitrary console input.

    Seeded so a failure is reproducible; the alphabet spans digits, letters,
    signs, separators, whitespace and multibyte characters, and answer lengths
    straddle the four-byte PIC 9(4) boundary.
    """
    rng = random.Random(20260907)
    alphabet = list(string.digits + "0123 abz.,+- \t") + ["é", "א"]
    for case in range(12):
        answers = [
            "".join(rng.choice(alphabet) for _ in range(rng.randint(0, 7))) for _ in range(rng.randint(0, 5))
        ]
        text = "".join(answer + "\n" for answer in answers)
        fixture = Fixture(f"random_{case}", "randomised differential case", text)
        result = reconcile(fixture)
        assert result.ok, "\n".join(result.diffs)


def test_harness_skips_without_cobol_toolchain(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setattr("parity.harness.cobc_available", lambda: False)
    assert main([]) == EXIT_SKIPPED
