#!/usr/bin/env python3
"""Port of search/search.cbl: SEARCH ALL (binary) and SEARCH (sequential)."""
import bisect
from dataclasses import dataclass


@dataclass(frozen=True)
class Item:
    id_1: int  # ascending key
    id_2: int  # ascending key
    id_3: int  # descending key
    name: str
    date: str

    @property
    def sort_key(self):
        # ASCENDING KEY id-1, id-2 / DESCENDING KEY id-3
        return (self.id_1, self.id_2, -self.id_3)


@dataclass(frozen=True)
class NoKeyItem:
    id: int
    value: str


ITEM_TABLE = [
    Item(1, 101, 500, "test item 1", "2021/01/01"),
    Item(2, 102, 499, "test item 2", "2021/02/02"),
    Item(3, 103, 498, "test item 3", "2021/03/03"),
]
assert ITEM_TABLE == sorted(ITEM_TABLE, key=lambda i: i.sort_key), "SEARCH ALL needs sorted keys"

NO_KEY_ITEM_TABLE = [
    NoKeyItem(2, "Value of id 2."),
    NoKeyItem(3, "Value of id 3."),
    NoKeyItem(1, "Value of id 1."),
]


def search_all_by_id_1(table, id_1):
    """Binary search on the leading key (SEARCH ALL ... WHEN id-1 = x)."""
    keys = [item.id_1 for item in table]
    idx = bisect.bisect_left(keys, id_1)
    return table[idx] if idx < len(keys) and keys[idx] == id_1 else None


def search_all_by_keys(table, id_1, id_2, id_3):
    """Binary search on the full compound key (id-1, id-2 asc; id-3 desc)."""
    keys = [item.sort_key for item in table]
    target = (id_1, id_2, -id_3)
    idx = bisect.bisect_left(keys, target)
    return table[idx] if idx < len(keys) and keys[idx] == target else None


def search_sequential(table, wanted_id):
    """Sequential SEARCH: no key, no ordering required."""
    return next((item for item in table if item.id == wanted_id), None)


def accept_id(prompt: str) -> int:
    """ACCEPT into PIC 9(4)."""
    try:
        text = input(prompt)
    except EOFError:
        text = ""
    integer_part = text.strip().split(".")[0]
    if not integer_part.isdigit() or not integer_part.isascii():
        return 0
    return int(integer_part[:4])


def display_found_item(item: Item) -> None:
    print(" Record found:")
    print("----------------")
    print(f"Item id-1: {item.id_1:04d}")
    print(f"Item id-2: {item.id_2:04d}")
    print(f"Item id-3: {item.id_3:04d}")
    print(f"Item Name: {item.name:<16}")
    print(f"Item Date: {item.date}")
    print(" ")


def banner(title: str) -> None:
    print(" ")
    print("==================================================")
    print(title)


def main() -> int:
    banner("Searching keyed table using binary search.")
    item = search_all_by_id_1(ITEM_TABLE, accept_id("Enter id-1 to search for: "))
    display_found_item(item) if item else print("Item not found.")

    banner("Searching again with all required ids matching.")
    id_1 = accept_id("Enter id-1 to search for: ")
    id_2 = accept_id("Enter id-2 to search for: ")
    id_3 = accept_id("Enter id-3 to search for: ")
    item = search_all_by_keys(ITEM_TABLE, id_1, id_2, id_3)
    display_found_item(item) if item else print("Item not found.")

    banner("Searching not keyed table using sequential search.")
    found = search_sequential(NO_KEY_ITEM_TABLE, accept_id("Enter id: "))
    if found:
        print(" Record found:")
        print("---------------")
        print(f"   ws-no-key-id: {found.id:04d}")
        print(f"ws-no-key-value: {found.value:<25}")
        print(" ")
    else:
        print("Item not found.")

    print(" ")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
