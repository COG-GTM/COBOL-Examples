"""The ``f-customer-record-*`` layout shared by every FD/SD in the COBOL program."""

from __future__ import annotations

from dataclasses import dataclass

from app.cobol_types import (
    COMMENT_LENGTH,
    COMMENT_OFFSET,
    CONTRACT_ID_LENGTH,
    CONTRACT_ID_OFFSET,
    CUSTOMER_ID_LENGTH,
    CUSTOMER_ID_OFFSET,
    FIRST_NAME_LENGTH,
    FIRST_NAME_OFFSET,
    LAST_NAME_LENGTH,
    LAST_NAME_OFFSET,
    RECORD_LENGTH,
    display_key_value,
    from_byte_string,
    write_record,
)


@dataclass(frozen=True)
class CustomerRecord:
    """A 135 byte record area.

    ``raw`` is a byte string (one character per byte) of exactly ``RECORD_LENGTH``
    characters. Field accessors slice it; nothing is ever trimmed before being written back
    out, because ``PIC X(n)`` padding is part of the record (ruling D-004).
    """

    raw: str

    def __post_init__(self) -> None:
        if len(self.raw) != RECORD_LENGTH:
            raise ValueError(f"record must be {RECORD_LENGTH} bytes, got {len(self.raw)}")

    @property
    def customer_id(self) -> str:
        return self.raw[CUSTOMER_ID_OFFSET : CUSTOMER_ID_OFFSET + CUSTOMER_ID_LENGTH]

    @property
    def last_name(self) -> str:
        return self.raw[LAST_NAME_OFFSET : LAST_NAME_OFFSET + LAST_NAME_LENGTH]

    @property
    def first_name(self) -> str:
        return self.raw[FIRST_NAME_OFFSET : FIRST_NAME_OFFSET + FIRST_NAME_LENGTH]

    @property
    def contract_id(self) -> str:
        return self.raw[CONTRACT_ID_OFFSET : CONTRACT_ID_OFFSET + CONTRACT_ID_LENGTH]

    @property
    def comment(self) -> str:
        return self.raw[COMMENT_OFFSET : COMMENT_OFFSET + COMMENT_LENGTH]

    @property
    def customer_id_sort_value(self) -> int:
        """Key used by ``merge ... on ascending key f-customer-id``."""
        return display_key_value(self.customer_id)

    @property
    def contract_id_sort_value(self) -> int:
        """Key used by ``sort ... on descending key f-customer-contract-id``."""
        return display_key_value(self.contract_id)

    def display_line(self) -> str:
        """What ``display f-customer-record-...`` puts on the console: all 135 bytes."""
        return self.raw

    def file_line(self) -> str:
        """What ``write`` puts in a line sequential file: trailing spaces removed."""
        return write_record(self.raw)

    def as_json(self) -> dict[str, object]:
        """Human view for the API. Text fields are trimmed here and *only* here."""
        return {
            "customer_id": from_byte_string(self.customer_id),
            "last_name": from_byte_string(self.last_name).rstrip(" "),
            "first_name": from_byte_string(self.first_name).rstrip(" "),
            "contract_id": from_byte_string(self.contract_id),
            "comment": from_byte_string(self.comment).rstrip(" "),
            "raw": from_byte_string(self.raw),
        }
