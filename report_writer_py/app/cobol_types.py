"""COBOL data-item semantics used by the ``report-test`` port.

Every rule here is documented in ``docs/phase0-contracts.md`` §4.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class PictureField:
    """A fixed-width DISPLAY field taken from an FD record layout."""

    name: str
    start: int  # 1-based column within the record
    width: int
    numeric: bool

    @property
    def end(self) -> int:
        return self.start + self.width - 1

    def extract(self, record_area: str) -> str:
        return record_area[self.start - 1 : self.end]


def to_byte_string(text: str) -> str:
    """Reinterpret text as the bytes COBOL would read, one character per byte.

    GnuCOBOL applies the record layout to *bytes*, so a multibyte character shifts every
    later field. Latin-1 round-tripping keeps the byte positions intact while letting the
    rest of the port work with ordinary strings.
    """
    return text.encode("utf-8").decode("latin-1")


def from_byte_string(byte_string: str) -> str:
    """Inverse of :func:`to_byte_string`; undecodable bytes are replaced, not dropped."""
    return byte_string.encode("latin-1").decode("utf-8", errors="replace")


def pad_record_area(line: str, length: int) -> str:
    """LINE SEQUENTIAL read: short records are space filled to the record length."""
    return line[:length].ljust(length)


def cobol_move_display_numeric(value: str, width: int) -> str:
    """Move a DISPLAY numeric field into a report item of the same PIC 9(n).

    GnuCOBOL copies the bytes and turns spaces into ``0``; any other byte is passed
    through untouched (``"AB C12"`` prints as ``"AB0C12"``).
    """
    return value[:width].ljust(width).replace(" ", "0")


def cobol_move_alphanumeric(value: str, width: int) -> str:
    """Move an X(n) field: left justified, space filled, truncated on the right."""
    return value[:width].ljust(width)


def cobol_edit_zz9(value: int) -> str:
    """PIC ZZ9 editing: 3 characters, right justified, leading zeros suppressed."""
    return str(value)[-3:].rjust(3)
