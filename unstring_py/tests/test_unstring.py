"""Unit tests for the UNSTRING engine and the program port.

Every expectation here was first observed in GnuCOBOL output; the parity harness
(``tests/test_parity.py``) is what keeps them honest.
"""

from __future__ import annotations

from decimal import Decimal

from app.cobol_types import (
    display_index,
    display_unsigned,
    format_source_num,
    move_to_numeric_display,
    move_to_source_num,
    to_byte_string,
)
from app.program import (
    DEFAULT_MULTI_DELIM_SOURCE,
    NO_OVERFLOW_MESSAGE,
    OVERFLOW_MESSAGE,
    run_program,
)
from app.unstring import Delimiter, Receiver, UnstringResult, unstring


def unstring_all(
    source: str,
    delimiters: tuple[Delimiter, ...],
    receivers: tuple[Receiver, ...],
    pointer: int = 1,
    tallying: int = 0,
) -> UnstringResult:
    return unstring(source, delimiters, receivers, pointer=pointer, tallying=tallying)


def test_simple_split_pads_receivers():
    result = unstring_all("Hello World".ljust(30), (Delimiter(" "),), (Receiver(15), Receiver(15)))
    assert [field.value for field in result.fields] == ["Hello".ljust(15), "World".ljust(15)]


def test_all_collapses_repeated_delimiters():
    result = unstring_all("A<<<B".ljust(30), (Delimiter("<", all_=True),), (Receiver(5), Receiver(5)))
    assert [field.value for field in result.fields] == ["A    ", "B    "]
    assert result.fields[0].delimiter_run == "<<<"


def test_bare_delimiter_yields_empty_token():
    result = unstring_all("A!!B", (Delimiter("!"),), (Receiver(5), Receiver(5), Receiver(5)))
    assert [field.examined for field in result.fields] == ["A", "", "B"]


def test_leading_delimiter_gives_zero_count():
    result = unstring_all("<A", (Delimiter("<"),), (Receiver(5, count_width=1, delimiter_width=1),))
    assert result.fields[0].count == "0"
    assert result.fields[0].delimiter == "<"


def test_no_delimiter_leaves_delimiter_in_as_spaces():
    result = unstring_all("ABC", (Delimiter("|"),), (Receiver(5, count_width=1, delimiter_width=1),))
    assert result.fields[0].delimiter == " "
    assert result.pointer == 4
    assert not result.overflow


def test_token_wider_than_destination_truncates_but_count_is_full_length():
    result = unstring_all("ABCDEFGHIJKL<", (Delimiter("<"),), (Receiver(5, count_width=1),))
    assert result.fields[0].value == "ABCDE"
    # PIC 9 holds one digit: 12 characters are reported as "2" (ruling D-004).
    assert result.fields[0].count == "2"


def test_overflow_when_receivers_run_out():
    result = unstring_all("A|B|C", (Delimiter("|"),), (Receiver(5),))
    assert result.overflow
    assert result.pointer == 3


def test_pointer_past_end_raises_overflow_without_transfer():
    result = unstring_all("ABC", (Delimiter("|"),), (Receiver(5),), pointer=4)
    assert result.overflow
    assert result.fields == []
    assert result.pointer == 4


def test_tallying_accumulates_rather_than_resets():
    result = unstring_all("A|B", (Delimiter("|"),), (Receiver(5), Receiver(5)), tallying=7)
    assert result.tallying == 9


def test_numeric_receiver_right_justifies_with_zero_fill():
    assert move_to_numeric_display("12", 3) == "012"
    assert move_to_numeric_display("1234", 3) == "234"


def test_display_helpers_match_gnucobol_formats():
    assert display_unsigned(7, 5) == "00007"
    assert display_index(1) == "+000000001"


def test_numeric_edited_truncates_toward_zero():
    assert move_to_source_num(Decimal("987654.999")) == Decimal("987654.99")
    assert format_source_num(Decimal("987654.999")) == "$987,654.99"
    # High-order digits beyond the PICTURE are dropped by the MOVE (ruling D-007).
    assert format_source_num(Decimal("91234567.89")) == "$234,567.89"
    assert format_source_num(Decimal("0")) == "$000,000.00"


def test_program_reproduces_the_documented_transcript():
    program = run_program()
    text = program.stdout_text()
    assert "EX 1 : SIMPLE UNSTRING" in text
    assert OVERFLOW_MESSAGE in text
    assert NO_OVERFLOW_MESSAGE in text
    assert "PART 3: 012" in text
    assert program.examples[3].source.startswith(DEFAULT_MULTI_DELIM_SOURCE)


def test_example_two_state_leaks_into_example_three():
    """The examples share one working storage; example 2 leaves ws-part-2 alone."""
    program = run_program(simple_source="OnlyOne")
    passes = program.examples[1].stats["passes"]
    assert isinstance(passes, list)
    assert passes[0]["overflow"] is False


def test_multibyte_input_is_measured_in_bytes():
    program = run_program(simple_source="שלום world")
    # Two-byte characters consume two of the PIC X(30) positions.
    assert len(program.ws.source_str) == 30
    assert to_byte_string("שלום") in program.examples[0].lines[4]
