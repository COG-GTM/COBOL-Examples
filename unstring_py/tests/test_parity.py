"""The parity gate.

Runs the reconciliation harness inside the test suite: every fixture must diff to zero
bytes against GnuCOBOL, and a deliberately broken port must make the harness fail.
Without ``cobc`` these tests SKIP — they never pass silently.
"""

from __future__ import annotations

from collections.abc import Callable
from decimal import Decimal

import pytest

from app.program import ExampleView, Program
from parity.fixtures import FIXTURES
from parity.harness import EXIT_DIFF, EXIT_OK, EXIT_SKIPPED, main, reconcile, reconcile_all
from parity.oracle import cobc_available

pytestmark = pytest.mark.skipif(not cobc_available(), reason="GnuCOBOL 'cobc' is not installed")


@pytest.mark.parametrize("fixture", FIXTURES, ids=lambda fixture: fixture.name)
def test_fixture_matches_cobol_byte_for_byte(fixture):
    result = reconcile(fixture)
    assert result.ok, "\n".join(result.diffs)


def test_harness_reports_zero_diffs_over_every_fixture():
    results = reconcile_all()
    assert sum(len(result.diffs) for result in results) == 0
    assert len(results) == len(FIXTURES)


def test_harness_exits_zero():
    assert main(["--quiet"]) == EXIT_OK


def test_harness_skips_without_cobc(monkeypatch):
    monkeypatch.setattr("parity.harness.cobc_available", lambda: False)
    assert main([]) == EXIT_SKIPPED


class _RoundingRegression(Program):
    """Seeded regression: round the amount half-up instead of truncating (D-006)."""

    def example_6_formatted_number(self) -> ExampleView:
        self.source_num = Decimal(self.source_num).quantize(Decimal("1"))
        return super().example_6_formatted_number()


class _TruncationRegression(Program):
    """Seeded regression: keep six characters in the PIC X(5) destinations."""

    def example_4_multiple_delimiters(self) -> ExampleView:
        view = super().example_4_multiple_delimiters()
        self.console = [line.replace("VALUE: ", "VALUE:  ") for line in self.console]
        return view


def _broken_port(cls: type[Program]) -> Callable[..., Program]:
    def runner(**kwargs: object) -> Program:
        return cls(**kwargs).run()  # type: ignore[arg-type]

    return runner


@pytest.mark.parametrize("regression", [_RoundingRegression, _TruncationRegression])
def test_seeded_regression_is_caught_by_the_gate(regression):
    results = reconcile_all(port=_broken_port(regression))
    assert sum(len(result.diffs) for result in results) > 0, "the gate has no teeth"


def test_seeded_regression_makes_the_harness_exit_non_zero(monkeypatch):
    monkeypatch.setattr("parity.harness.run_program", _broken_port(_RoundingRegression))
    assert main(["--quiet"]) == EXIT_DIFF
