"""Port of ``search/search.cbl`` (program-id ``search-example``), paragraph for
paragraph.

The COBOL is the specification: every literal, every prompt and every blank line
below is reproduced byte for byte, and the parity harness proves it against the
compiled binary.
"""

from __future__ import annotations

from collections.abc import Sequence
from dataclasses import dataclass

from app.cobol_types import Pic9, accept_pic9
from app.tables import ITEM_KEYS, STORE, ItemRecord, KeyDirection, NoKeyRecord

# --- literals lifted verbatim from the COBOL --------------------------------
SEPARATOR = b"=================================================="
BANNER_KEYED = b"Searching keyed table using binary search."
BANNER_KEYED_ALL = b"Searching again with all required ids matching."
BANNER_SEQUENTIAL = b"Searching not keyed table using sequential search."
PROMPT_ID_1 = b"Enter id-1 to search for: "
PROMPT_ID_2 = b"Enter id-2 to search for: "
PROMPT_ID_3 = b"Enter id-3 to search for: "
PROMPT_ID = b"Enter id: "
NOT_FOUND = b"Item not found."
RECORD_FOUND = b" Record found:"
RULE_ITEM = b"----------------"
RULE_NO_KEY = b"---------------"
SPACE = b" "


@dataclass(frozen=True)
class KeyCondition:
    """One ``WHEN <key>(idx) = <value>`` term of a ``SEARCH ALL``."""

    field: str
    direction: KeyDirection
    value: Pic9


def _compare(record: ItemRecord, condition: KeyCondition) -> int:
    """Order ``record`` against the sought value along one declared key."""
    actual = getattr(record, condition.field).value
    sought = condition.value.value
    if actual == sought:
        return 0
    below = actual < sought if condition.direction == "ascending" else actual > sought
    return -1 if below else 1


def search_all_items(
    conditions: Sequence[KeyCondition], items: Sequence[ItemRecord] | None = None
) -> int | None:
    """``SEARCH ALL ws-item-table``: the binary search GnuCOBOL generates.

    The conditions are evaluated in declared key order; the first key that does
    not match decides which half is discarded, honouring that key's ASCENDING /
    DESCENDING declaration.  A row is a hit only when *every* supplied key
    matches, which is why a partial key match reports ``Item not found.``
    (ruling D-002).
    """
    table = STORE.items if items is None else items
    head, tail = -1, len(table)
    while head + 1 < tail:
        mid = (head + tail) // 2
        ordering = 0
        for condition in conditions:
            ordering = _compare(table[mid], condition)
            if ordering != 0:
                break
        if ordering == 0:
            return mid
        if ordering < 0:
            head = mid
        else:
            tail = mid
    return None


def search_no_key_items(sought: Pic9, items: Sequence[NoKeyRecord] | None = None) -> int | None:
    """``SEARCH ws-no-key-item-table``: sequential scan from ``idx-2 = 1``."""
    table = STORE.no_key_items if items is None else items
    for offset, record in enumerate(table):
        if record.no_key_id.value == sought.value:
            return offset
    return None


def keyed_conditions(id_1: Pic9, id_2: Pic9 | None = None, id_3: Pic9 | None = None) -> list[KeyCondition]:
    values = (id_1, id_2, id_3)
    return [
        KeyCondition(field, direction, value)
        for (field, direction), value in zip(ITEM_KEYS, values, strict=True)
        if value is not None
    ]


def display_found_item(record: ItemRecord) -> list[bytes]:
    """Port of the ``display-found-item`` paragraph."""
    return [
        RECORD_FOUND,
        RULE_ITEM,
        b"Item id-1: " + record.item_id_1.display(),
        b"Item id-2: " + record.item_id_2.display(),
        b"Item id-3: " + record.item_id_3.display(),
        b"Item Name: " + record.name,
        b"Item Date: " + record.date,
        SPACE,
    ]


def display_found_no_key_item(record: NoKeyRecord) -> list[bytes]:
    """Port of the inline ``WHEN`` block of the sequential search."""
    return [
        RECORD_FOUND,
        RULE_NO_KEY,
        b"   ws-no-key-id: " + record.no_key_id.display(),
        b"ws-no-key-value: " + record.no_key_value,
        SPACE,
    ]


def _accept_lines(stdin_bytes: bytes, count: int) -> list[bytes]:
    """Split piped stdin the way successive ``ACCEPT`` statements consume it.

    Exhausted input leaves the receiving field untouched, i.e. all zeros, so a
    missing line is modelled as an empty one.
    """
    lines = stdin_bytes.split(b"\n")
    if lines and lines[-1] == b"":
        lines.pop()
    return [lines[index] if index < len(lines) else b"" for index in range(count)]


def run_program(stdin_bytes: bytes) -> bytes:
    """Port of ``main-procedure``: the whole console session, as stdout bytes."""
    raw_1, raw_2, raw_3, raw_4, raw_5 = _accept_lines(stdin_bytes, 5)
    out: list[bytes] = []

    def line(text: bytes) -> None:
        out.append(text + b"\n")

    # --- binary search on ws-item-id-1 only
    line(SPACE)
    line(SEPARATOR)
    line(BANNER_KEYED)
    out.append(PROMPT_ID_1)  # DISPLAY ... WITH NO ADVANCING
    accept_id_1 = accept_pic9(raw_1)
    hit = search_all_items(keyed_conditions(accept_id_1))
    if hit is None:
        line(NOT_FOUND)
    else:
        for text in display_found_item(STORE.items[hit]):
            line(text)

    # --- binary search on all three declared keys
    line(SPACE)
    line(SEPARATOR)
    line(BANNER_KEYED_ALL)
    out.append(PROMPT_ID_1)
    accept_id_1 = accept_pic9(raw_2)
    out.append(PROMPT_ID_2)
    accept_id_2 = accept_pic9(raw_3)
    out.append(PROMPT_ID_3)
    accept_id_3 = accept_pic9(raw_4)
    hit = search_all_items(keyed_conditions(accept_id_1, accept_id_2, accept_id_3))
    if hit is None:
        line(NOT_FOUND)
    else:
        for text in display_found_item(STORE.items[hit]):
            line(text)

    # --- sequential search on the unkeyed table
    line(SPACE)
    line(SEPARATOR)
    line(BANNER_SEQUENTIAL)
    out.append(PROMPT_ID)
    accept_id_1 = accept_pic9(raw_5)
    seq_hit = search_no_key_items(accept_id_1)
    if seq_hit is None:
        line(NOT_FOUND)
    else:
        for text in display_found_no_key_item(STORE.no_key_items[seq_hit]):
            line(text)

    line(SPACE)
    return b"".join(out)
