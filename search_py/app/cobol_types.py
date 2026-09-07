"""COBOL data-type semantics used by ``search/search.cbl``.

Everything here operates on ``bytes``: COBOL applies its fixed-width layouts to
bytes, not to Python characters, so a multibyte input line must line up exactly
as GnuCOBOL sees it.  No floating point is used anywhere; ``PIC 9(n)`` values are
held as ``int`` plus their zero-padded display image.
"""

from __future__ import annotations

from dataclasses import dataclass

DIGITS = b"0123456789"
#: ``isspace()`` in the C locale, which is what GnuCOBOL's move routine uses.
C_WHITESPACE = b" \t\n\v\f\r"
DECIMAL_POINT = 0x2E  # '.'
NUMERIC_SEPARATOR = 0x2C  # ','


@dataclass(frozen=True)
class Pic9:
    """A ``PIC 9(n)`` display item: the integer value and its display image."""

    digits: bytes

    @property
    def value(self) -> int:
        return int(self.digits)

    def display(self) -> bytes:
        return self.digits

    def __str__(self) -> str:
        return self.digits.decode("latin-1")


def pic9(value: int, size: int) -> Pic9:
    """Build a ``PIC 9(size)`` item from an integer literal (as ``MOVE`` would)."""
    return Pic9(str(value % (10**size)).rjust(size, "0").encode("latin-1"))


def pic_x(text: str, size: int) -> bytes:
    """``PIC X(size)``: left-justified, space filled, truncated on overflow."""
    raw = text.encode("utf-8")
    return raw[:size].ljust(size, b" ")


def accept_pic9(line: bytes, size: int = 4) -> Pic9:
    """Reproduce ``ACCEPT <PIC 9(size)>`` from a piped stdin line.

    GnuCOBOL truncates the accepted line to the receiving field's byte size and
    then performs an alphanumeric-to-display move, which:

    * skips leading whitespace and one optional ``+``/``-`` sign (the sign is
      dropped by an unsigned field, so ``-2`` accepts as ``0002``);
    * right-aligns the digits that appear before the decimal point;
    * tolerates embedded spaces and thousands separators;
    * stores zeros when any other character is met, or when a second decimal
      point appears.

    Absent input (EOF) never reaches here; the caller supplies ``b""``, which
    yields zeros, exactly as the untouched working-storage field does.
    """
    buf = line[:size]
    out = bytearray(b"0" * size)
    length = len(buf)

    index = 0
    while index < length and buf[index] in C_WHITESPACE:
        index += 1
    if index < length and buf[index] in b"+-":
        index += 1

    count = 0
    scan = index
    while scan < length and buf[scan] != DECIMAL_POINT:
        if buf[scan] in DIGITS:
            count += 1
        scan += 1

    if count < size:
        target = size - count
    else:
        target = 0
        while count > size:
            count -= 1
            while index < length and buf[index] not in DIGITS:
                index += 1
            index += 1

    decimal_points = 0
    while index < length and target < size:
        char = buf[index]
        if char in DIGITS:
            out[target] = char
            target += 1
        elif char == DECIMAL_POINT:
            decimal_points += 1
            if decimal_points > 1:
                return Pic9(b"0" * size)
        elif char not in C_WHITESPACE and char != NUMERIC_SEPARATOR:
            return Pic9(b"0" * size)
        index += 1

    return Pic9(bytes(out))
