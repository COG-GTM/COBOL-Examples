"""COBOL data-item semantics used by ``unstring-example``.

Every helper here mirrors one storage/move rule of the legacy program. COBOL applies
its PICTURE layouts to *bytes*, so text entering the port is round-tripped through
Latin-1 (``to_byte_string``) and one Python character is one byte for the whole
pipeline; JSON views decode back with ``from_byte_string``.

No floating point is used anywhere: ``PIC $999,999.99`` is carried as ``Decimal``.
"""

from __future__ import annotations

from decimal import ROUND_DOWN, Decimal

#: ``01 ws-source-str pic x(30).``
SOURCE_LEN = 30
#: ``05 ws-part-1 / ws-part-2 pic x(15).``
PART_LEN = 15
#: ``10 ws-single-dest-str / ws-multi-dest-str pic x(5).``
DEST_LEN = 5
#: ``05 ws-multi-dest-info occurs 6 times.``
MULTI_OCCURS = 6
#: ``01 ws-dest-num pic 999 occurs 3 times.``
DEST_NUM_LEN = 3


def to_byte_string(text: str) -> str:
    """Present ``text`` as one-character-per-byte, the way GnuCOBOL sees it."""
    return text.encode("utf-8").decode("latin-1")


def from_byte_string(value: str) -> str:
    """Inverse of :func:`to_byte_string`; invalid sequences are kept verbatim."""
    return value.encode("latin-1").decode("utf-8", errors="replace")


def move_to_alphanumeric(value: str, width: int) -> str:
    """``MOVE`` into ``PIC X(width)``: left justified, space filled, right truncated."""
    return value[:width].ljust(width)


def move_to_byte_source(text: str, width: int = SOURCE_LEN) -> str:
    """``MOVE`` free text into ``PIC X(width)`` after byte normalisation."""
    return move_to_alphanumeric(to_byte_string(text), width)


def move_to_numeric_display(value: str, width: int) -> str:
    """``MOVE`` alphanumeric into ``PIC 9(width)``.

    The sender is treated as an unsigned integer: right justified, zero filled, and
    truncated on the *left* when it is too long. Spaces become ``0`` exactly as
    GnuCOBOL's display-numeric move does.
    """
    digits = value.strip()
    if len(digits) > width:
        digits = digits[len(digits) - width :]
    return "".join("0" if char == " " else char for char in digits.rjust(width, "0"))


def move_to_unsigned_int(value: int, digits: int) -> int:
    """``MOVE`` an integer into ``PIC 9(digits)``: high-order truncation, no sign."""
    return int(abs(value) % (10**digits))


def display_unsigned(value: int, digits: int) -> str:
    """``DISPLAY`` of a ``PIC 9(n)``/``PIC 9(n) COMP`` item: zero-padded digits."""
    return f"{move_to_unsigned_int(value, digits):0{digits}d}"


def display_index(value: int) -> str:
    """``DISPLAY`` of an ``INDEXED BY`` item: GnuCOBOL prints ``+000000001``."""
    sign = "-" if value < 0 else "+"
    return f"{sign}{abs(value):09d}"


def move_to_source_num(value: Decimal) -> Decimal:
    """``MOVE`` into ``PIC $999,999.99``.

    The ``PICTURE`` holds six integer digits and two decimals with **no** ``ROUNDED``
    anywhere in the program, so the value truncates toward zero (ruling D-006) and
    high-order digits beyond six are dropped (ruling D-007).
    """
    truncated = value.quantize(Decimal("0.01"), rounding=ROUND_DOWN)
    return truncated.copy_abs() % Decimal(1000000)


def format_source_num(value: Decimal) -> str:
    """Render ``PIC $999,999.99``: fixed ``$``, comma grouping, unsuppressed zeros."""
    stored = move_to_source_num(value)
    units = int(stored)
    cents = int((stored - units) * 100)
    return f"${units // 1000:03d},{units % 1000:03d}.{cents:02d}"
