"""COBOL data semantics for the ``merge-sort-example`` record layout.

Everything in this module was derived from ``merge_sort/merge_sort_test.cbl`` and then
confirmed empirically against GnuCOBOL 3.1.2 (see ``docs/phase0-contracts.md``).

Two rules drive the whole port:

* COBOL applies the record layout to **bytes**, not Python characters. Every value that
  crosses the layout is carried as a *byte string*: a ``str`` whose characters are all
  ``U+0000``-``U+00FF``, i.e. one character per byte (Latin-1 round trip).
* ``PIC 9(5)`` fields are compared by GnuCOBOL with ``cob_decimal_set_display``, which is
  **not** ``int()``. See :func:`display_key_value`.
"""

from __future__ import annotations

from typing import Final

RECORD_LENGTH: Final = 135

CUSTOMER_ID_OFFSET: Final = 0
CUSTOMER_ID_LENGTH: Final = 5
LAST_NAME_OFFSET: Final = 5
LAST_NAME_LENGTH: Final = 50
FIRST_NAME_OFFSET: Final = 55
FIRST_NAME_LENGTH: Final = 50
CONTRACT_ID_OFFSET: Final = 105
CONTRACT_ID_LENGTH: Final = 5
COMMENT_OFFSET: Final = 110
COMMENT_LENGTH: Final = 25

_UINT32_MASK: Final = 0xFFFFFFFF


def to_byte_string(text: str) -> str:
    """Return ``text`` as a byte string: one Python character per encoded byte."""
    return text.encode("utf-8").decode("latin-1")


def from_byte_string(value: str) -> str:
    """Inverse of :func:`to_byte_string`, tolerating bytes that are not valid UTF-8."""
    return value.encode("latin-1").decode("utf-8", errors="replace")


def to_bytes(value: str) -> bytes:
    return value.encode("latin-1")


def read_record(line: str) -> str:
    """Apply ``ORGANIZATION IS LINE SEQUENTIAL`` read semantics to one input line.

    GnuCOBOL drops a trailing carriage return, truncates anything past the record length
    and space-fills short lines into the 135 byte record area.
    """
    if line.endswith("\r"):
        line = line[:-1]
    if len(line) > RECORD_LENGTH:
        return line[:RECORD_LENGTH]
    return line.ljust(RECORD_LENGTH, " ")


def write_record(record: str) -> str:
    """Apply ``ORGANIZATION IS LINE SEQUENTIAL`` write semantics to one record.

    Trailing *spaces* are removed (and only spaces — a trailing tab is data). An all-blank
    record is therefore written as an empty line.
    """
    return record.rstrip(" ")


def split_records(text: str) -> list[str]:
    """Split raw input file text into 135 byte record areas.

    ``text`` must already be a byte string (see :func:`to_byte_string`). A trailing newline
    terminates the last record rather than starting an empty one, matching the file the
    COBOL program itself writes.
    """
    if not text:
        return []
    body = text[:-1] if text.endswith("\n") else text
    return [read_record(line) for line in body.split("\n")]


def join_records(records: list[str]) -> str:
    """Render records as a line sequential file body (each line newline-terminated)."""
    return "".join(write_record(record) + "\n" for record in records)


def display_key_value(field: str) -> int:
    """Value GnuCOBOL compares two ``PIC 9(n)`` DISPLAY fields by.

    This mirrors ``cob_decimal_set_display`` in libcob, *including* its handling of bytes
    that are not digits, which the SORT/MERGE key comparison inherits:

    * a leading ``0xFF`` byte makes the field compare as ``10**n`` (above any digit value);
    * a leading ``0x00`` byte makes it compare as ``-(10**n)`` (below any digit value);
    * otherwise leading bytes whose **low nibble is zero** are skipped as "leading zeros"
      (so ``'0'``, ``' '``, ``'@'``, ``'P'``, ``'`'`` and ``'p'`` are all leading zeros);
    * the remaining bytes are accumulated as ``n = n * 10 + (byte - 0x30)`` in a 32 bit
      **unsigned** register, so a byte below ``'0'`` (e.g. ``' '``, ``'-'``, ``'/'``)
      wraps the accumulator to a huge value and sorts last.

    Ruling D-002 keeps this behaviour rather than parsing the field with ``int()``.
    """
    size = len(field)
    if size == 0:
        return 0
    first = ord(field[0])
    if first == 0xFF:
        return int(10**size)
    if first == 0x00:
        return -int(10**size)

    index = 0
    remaining = size
    while remaining > 1 and (ord(field[index]) & 0x0F) == 0:
        index += 1
        remaining -= 1

    value = 0
    for position in range(index, size):
        value = (value * 10 + (ord(field[position]) - 0x30)) & _UINT32_MASK
    return value
