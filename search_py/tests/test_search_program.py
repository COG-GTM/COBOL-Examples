"""Unit tests for the port, keyed to the rulings in docs/phase0-contracts.md."""

from __future__ import annotations

import pytest

from app.cobol_types import accept_pic9, pic9, pic_x
from app.search_program import (
    display_found_item,
    keyed_conditions,
    run_program,
    search_all_items,
    search_no_key_items,
)
from app.tables import STORE


@pytest.mark.parametrize(
    ("raw", "expected"),
    [
        (b"", b"0000"),
        (b"1", b"0001"),
        (b"0003", b"0003"),
        (b"   ", b"0000"),
        (b"abc", b"0000"),
        (b"a1", b"0000"),  # D-006.5: leading letter aborts the move
        (b"1abc", b"0001"),  # D-006.5: field already full when 'a' is reached
        (b"12a45", b"0000"),
        (b"12345", b"1234"),  # D-006.1: first four bytes, not the low-order digits
        (b"  12345", b"0012"),
        (b"-2", b"0002"),  # D-006.2: sign consumed, dropped by the unsigned field
        (b"+1234", b"0123"),
        (b"1 2", b"0012"),  # D-006.4: embedded spaces skipped
        (b"12,34", b"0123"),
        (b"1.9", b"0001"),  # D-006.3: digits before the decimal point
        (b"0.9", b"0000"),
        (b"\xc3\xa91", b"0000"),  # D-011: multibyte input is bytes, not characters
    ],
)
def test_accept_pic9(raw: bytes, expected: bytes) -> None:
    assert accept_pic9(raw).display() == expected


def test_pic_x_pads_and_truncates() -> None:
    assert pic_x("test item 1", 16) == b"test item 1     "
    assert pic_x("Value of id 1.", 25) == b"Value of id 1.           "
    assert pic_x("way too long for the field", 8) == b"way too "


def test_pic9_is_never_floating_point() -> None:
    assert isinstance(pic9(500, 4).value, int)
    assert pic9(500, 4).display() == b"0500"


@pytest.mark.parametrize(("sought", "row"), [(1, 0), (2, 1), (3, 2)])
def test_search_all_single_key_finds_each_row(sought: int, row: int) -> None:
    assert search_all_items(keyed_conditions(pic9(sought, 4))) == row


@pytest.mark.parametrize("sought", [0, 4, 9, 9999])
def test_search_all_single_key_misses(sought: int) -> None:
    assert search_all_items(keyed_conditions(pic9(sought, 4))) is None


@pytest.mark.parametrize(
    ("ids", "row"),
    [((1, 101, 500), 0), ((2, 102, 499), 1), ((3, 103, 498), 2)],
)
def test_search_all_full_key_finds_each_row(ids: tuple[int, int, int], row: int) -> None:
    conditions = keyed_conditions(*(pic9(value, 4) for value in ids))
    assert search_all_items(conditions) == row


@pytest.mark.parametrize(
    "ids",
    [(2, 102, 999), (2, 999, 499), (9, 102, 499), (1, 101, 499), (1, 102, 500), (3, 103, 500), (2, 101, 499)],
)
def test_partial_key_match_is_not_found(ids: tuple[int, int, int]) -> None:
    """D-002: every supplied key must match the same occurrence."""
    conditions = keyed_conditions(*(pic9(value, 4) for value in ids))
    assert search_all_items(conditions) is None


@pytest.mark.parametrize(("sought", "row"), [(2, 0), (3, 1), (1, 2)])
def test_sequential_search_returns_first_match_in_table_order(sought: int, row: int) -> None:
    """D-005: the unkeyed table is scanned in table order, not key order."""
    assert search_no_key_items(pic9(sought, 4)) == row


def test_sequential_search_misses() -> None:
    assert search_no_key_items(pic9(4, 4)) is None


def test_display_found_item_keeps_pic_x_padding() -> None:
    """D-009: trailing spaces are part of the record image."""
    lines = display_found_item(STORE.items[2])
    assert lines[5] == b"Item Name: test item 3     "
    assert lines[6] == b"Item Date: 2021/03/03"  # D-008: a fixed-width string
    assert lines[1] == b"----------------"  # D-007: 16 dashes here...


def test_sequential_rule_is_one_dash_shorter() -> None:
    """D-007: ...and 15 in the sequential block; preserved, not tidied."""
    from app.search_program import display_found_no_key_item

    assert display_found_no_key_item(STORE.no_key_items[0])[1] == b"---------------"


def test_run_program_shape() -> None:
    out = run_program(b"3\n2\n102\n499\n1\n")
    assert out.startswith(b" \n==================================================\n")
    assert b"Enter id-1 to search for:  Record found:\n" in out
    assert out.endswith(b"ws-no-key-value: Value of id 1.           \n \n \n")


def test_run_program_not_found_uses_cobol_wording() -> None:
    out = run_program(b"9\n9\n9\n9\n9\n")
    assert out.count(b"Item not found.\n") == 3
