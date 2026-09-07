"""The two working-storage tables of ``search-example`` and their key metadata.

``setup-test-data`` is ported ``MOVE`` for ``MOVE``; the in-memory store below is
the whole "database" the program has, kept behind a tiny adapter so a real store
could replace it without touching the search logic.
"""

from __future__ import annotations

from dataclasses import dataclass
from typing import Literal

from app.cobol_types import Pic9, pic9, pic_x

KeyDirection = Literal["ascending", "descending"]


@dataclass(frozen=True)
class ItemRecord:
    """One occurrence of ``ws-item-table`` (a 34-byte fixed-width record)."""

    item_id_1: Pic9  # ws-item-id-1  pic 9(4)
    item_id_2: Pic9  # ws-item-id-2  pic 9(4)
    item_id_3: Pic9  # ws-item-id-3  pic 9(4)
    name: bytes  # ws-item-name  pic x(16)
    date: bytes  # ws-item-date  group, 10 bytes: 9(4) '/' 99 '/' 99

    def raw(self) -> bytes:
        return (
            self.item_id_1.display()
            + self.item_id_2.display()
            + self.item_id_3.display()
            + self.name
            + self.date
        )


@dataclass(frozen=True)
class NoKeyRecord:
    """One occurrence of ``ws-no-key-item-table`` (a 29-byte fixed-width record)."""

    no_key_id: Pic9  # ws-no-key-id     pic 9(4)
    no_key_value: bytes  # ws-no-key-value  pic x(25)

    def raw(self) -> bytes:
        return self.no_key_id.display() + self.no_key_value


def _item_date(year: int, month: int, day: int) -> bytes:
    """``ws-item-date`` is a fixed-width group with ``filler`` slashes, not a date."""
    return f"{year:04d}/{month:02d}/{day:02d}".encode("latin-1")


#: ``ws-item-table``: ascending key ws-item-id-1, ws-item-id-2; descending ws-item-id-3.
ITEM_KEYS: tuple[tuple[str, KeyDirection], ...] = (
    ("item_id_1", "ascending"),
    ("item_id_2", "ascending"),
    ("item_id_3", "descending"),
)


def setup_test_data() -> tuple[list[ItemRecord], list[NoKeyRecord]]:
    """Port of the ``setup-test-data`` paragraph."""
    items = [
        ItemRecord(pic9(1, 4), pic9(101, 4), pic9(500, 4), pic_x("test item 1", 16), _item_date(2021, 1, 1)),
        ItemRecord(pic9(2, 4), pic9(102, 4), pic9(499, 4), pic_x("test item 2", 16), _item_date(2021, 2, 2)),
        ItemRecord(pic9(3, 4), pic9(103, 4), pic9(498, 4), pic_x("test item 3", 16), _item_date(2021, 3, 3)),
    ]
    no_key_items = [
        NoKeyRecord(pic9(2, 4), pic_x("Value of id 2.", 25)),
        NoKeyRecord(pic9(3, 4), pic_x("Value of id 3.", 25)),
        NoKeyRecord(pic9(1, 4), pic_x("Value of id 1.", 25)),
    ]
    return items, no_key_items


class TableStore:
    """In-memory adapter over the two tables (the program's system of record)."""

    def __init__(self) -> None:
        self._items, self._no_key_items = setup_test_data()

    @property
    def items(self) -> list[ItemRecord]:
        return self._items

    @property
    def no_key_items(self) -> list[NoKeyRecord]:
        return self._no_key_items


STORE = TableStore()
