"""Fixtures for the reconciliation harness.

Each fixture is the complete stdin of one console session: five ``ACCEPT``
answers (binary search id-1; then id-1/id-2/id-3; then the sequential id).
Between them they cross every boundary documented in
``docs/phase0-contracts.md``: exact match, partial key match, no match, the
first and last table entries, blank input, non-numeric input, the ``PIC 9(4)``
truncation and sign rules, and premature end of input.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class Fixture:
    name: str
    boundary: str
    stdin_text: str

    @property
    def stdin_bytes(self) -> bytes:
        return self.stdin_text.encode("utf-8")


def _session(*answers: str, trailing_newline: bool = True) -> str:
    text = "\n".join(answers)
    return text + "\n" if trailing_newline else text


FIXTURES: tuple[Fixture, ...] = (
    Fixture(
        "first_entry",
        "boundary: first table occurrence, all searches hit",
        _session("1", "1", "101", "500", "1"),
    ),
    Fixture(
        "last_entry",
        "boundary: last table occurrence, all searches hit",
        _session("3", "3", "103", "498", "3"),
    ),
    Fixture(
        "middle_entry",
        "exact match on the middle occurrence (binary-search midpoint)",
        _session("2", "2", "102", "499", "2"),
    ),
    Fixture(
        "partial_key_id3_wrong",
        "partial key match: id-1 and id-2 match, descending id-3 does not",
        _session("2", "2", "102", "999", "9"),
    ),
    Fixture(
        "partial_key_id2_wrong",
        "partial key match: id-1 and id-3 match, id-2 does not",
        _session("2", "2", "999", "499", "9"),
    ),
    Fixture(
        "partial_key_crossed_rows",
        "keys taken from two different rows (1 / 102 / 500)",
        _session("1", "1", "102", "500", "9"),
    ),
    Fixture(
        "no_match",
        "no match in either table",
        _session("9", "9", "9", "9", "9"),
    ),
    Fixture(
        "zero_ids",
        "id 0000: below the first key of both tables",
        _session("0", "0", "0", "0", "0"),
    ),
    Fixture(
        "above_last_key",
        "id 9999: above the last key of the keyed table",
        _session("9999", "9999", "9999", "9999", "9999"),
    ),
    Fixture(
        "blank_input",
        "blank lines accept as 0000",
        _session("", "", "", "", ""),
    ),
    Fixture(
        "blank_and_spaces",
        "whitespace-only input accepts as 0000",
        _session("   ", " \t ", "", "  ", " "),
    ),
    Fixture(
        "non_numeric",
        "wholly non-numeric input",
        _session("abc", "xyz", "??", "!!", "zz"),
    ),
    Fixture(
        "digit_then_letters",
        "digits followed by letters: field fills before the letter is reached",
        _session("3abc", "1abc", "101x", "500y", "1zz"),
    ),
    Fixture(
        "letters_then_digit",
        "leading letter aborts the move and stores zeros",
        _session("a1", "a1", "a101", "a500", "a1"),
    ),
    Fixture(
        "signed_input",
        "signs are consumed and dropped by the unsigned field",
        _session("-2", "+2", "+102", "-499", "-2"),
    ),
    Fixture(
        "decimal_and_separator",
        "decimal point ends the integer part; ',' is a numeric separator",
        _session("1.9", "1.9", "1,01", "5,00", "2.5"),
    ),
    Fixture(
        "embedded_spaces",
        "embedded spaces are skipped inside the field",
        _session("1 ", " 2", "1 02", "4 99", " 3"),
    ),
    Fixture(
        "overflow_digits",
        "input longer than PIC 9(4) is truncated to the first four bytes",
        _session("12345", "10002", "10102", "40499", "30001"),
    ),
    Fixture(
        "leading_zeros",
        "explicitly zero-padded input",
        _session("0003", "0001", "0101", "0500", "0002"),
    ),
    Fixture(
        "multibyte_input",
        "multibyte characters are truncated by byte, not by character",
        _session("é1", "1é", "101é", "500é", "אב"),
    ),
    Fixture(
        "short_session_eof",
        "stdin ends early: the remaining ACCEPTs leave their fields at zero",
        _session("1", "1"),
    ),
    Fixture(
        "no_trailing_newline",
        "final answer without a terminating newline",
        _session("2", "2", "102", "499", "2", trailing_newline=False),
    ),
    Fixture(
        "empty_stdin",
        "no input at all",
        "",
    ),
)
