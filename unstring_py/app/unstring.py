"""Port of the COBOL ``UNSTRING`` verb as executed by GnuCOBOL 3.x.

This is the heart of the migration: every form used by ``unstring/unstring.cbl``
(``DELIMITED BY``, ``ALL``, ``OR``, ``COUNT IN``, ``DELIMITER IN``, ``TALLYING IN``,
``WITH POINTER``, ``ON OVERFLOW``) is reproduced here, and the parity harness pins it
byte for byte against the compiled program.
"""

from __future__ import annotations

from dataclasses import dataclass, field

from app.cobol_types import move_to_alphanumeric, move_to_numeric_display


@dataclass(frozen=True)
class Delimiter:
    """One ``DELIMITED BY [ALL] <value>`` phrase."""

    value: str
    all_: bool = False


@dataclass(frozen=True)
class Receiver:
    """One ``INTO`` receiving field with its optional ``DELIMITER IN``/``COUNT IN``."""

    width: int
    #: ``PIC 9(n)`` receivers take the numeric-display move (``ws-dest-num``).
    numeric: bool = False
    delimiter_width: int | None = None
    count_width: int | None = None


@dataclass
class ReceivedField:
    """What one receiving field observed during the statement."""

    #: The receiving field after the move, padded/truncated to its PICTURE.
    value: str
    #: Characters examined before the delimiter, before any truncation.
    examined: str
    #: ``COUNT IN`` as stored (a ``PIC 9`` count truncates to its last digit).
    count: str | None
    #: ``DELIMITER IN`` as stored; spaces when the source ran out first.
    delimiter: str | None
    #: The full delimiter run consumed, which ``ALL`` collapses to one occurrence.
    delimiter_run: str


@dataclass
class UnstringResult:
    """Observable outcome of one ``UNSTRING`` statement."""

    fields: list[ReceivedField] = field(default_factory=list)
    pointer: int = 1
    tallying: int = 0
    overflow: bool = False


def _match_delimiter(source: str, index: int, delimiters: tuple[Delimiter, ...]) -> Delimiter | None:
    """First delimiter (in the order written) matching ``source`` at ``index``."""
    for delimiter in delimiters:
        if delimiter.value and source.startswith(delimiter.value, index):
            return delimiter
    return None


def unstring(
    source: str,
    delimiters: tuple[Delimiter, ...],
    receivers: tuple[Receiver, ...],
    pointer: int = 1,
    tallying: int = 0,
) -> UnstringResult:
    """Execute one ``UNSTRING`` statement over ``source``.

    ``pointer`` is 1-based (``WITH POINTER``) and is returned advanced past the last
    character examined. ``tallying`` is *added to*, never reset, matching ``TALLYING IN``.
    """
    result = UnstringResult(pointer=pointer, tallying=tallying)
    length = len(source)

    # An out-of-range pointer raises OVERFLOW before any data is transferred.
    if pointer < 1 or pointer > length:
        result.overflow = True
        return result

    position = pointer
    for receiver in receivers:
        if position > length:
            break

        start = position - 1
        cursor = start
        hit: Delimiter | None = None
        while cursor < length:
            hit = _match_delimiter(source, cursor, delimiters)
            if hit is not None:
                break
            cursor += 1

        examined = source[start:cursor]
        if hit is None:
            delimiter_run = ""
            position = length + 1
        else:
            run_end = cursor + len(hit.value)
            if hit.all_:
                while source.startswith(hit.value, run_end):
                    run_end += len(hit.value)
            delimiter_run = source[cursor:run_end]
            position = run_end + 1

        if receiver.numeric:
            stored = move_to_numeric_display(examined, receiver.width)
        else:
            stored = move_to_alphanumeric(examined, receiver.width)

        count: str | None = None
        if receiver.count_width is not None:
            count = move_to_numeric_display(str(len(examined)), receiver.count_width)

        delimiter_value: str | None = None
        if receiver.delimiter_width is not None:
            # DELIMITER IN receives one occurrence of the delimiter, or spaces when the
            # source ended without one.
            occurrence = hit.value if hit is not None else ""
            delimiter_value = move_to_alphanumeric(occurrence, receiver.delimiter_width)

        result.fields.append(
            ReceivedField(
                value=stored,
                examined=examined,
                count=count,
                delimiter=delimiter_value,
                delimiter_run=delimiter_run,
            )
        )
        result.tallying += 1

    result.pointer = position
    # Characters left over with no receiving field to take them: OVERFLOW.
    result.overflow = result.overflow or position <= length
    return result
