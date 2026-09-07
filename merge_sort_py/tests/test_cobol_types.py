"""Record geometry and PIC semantics (contracts D-002, D-004, D-005, D-006, D-007)."""

from __future__ import annotations

import pytest

from app.cobol_types import (
    RECORD_LENGTH,
    display_key_value,
    from_byte_string,
    join_records,
    read_record,
    split_records,
    to_byte_string,
    write_record,
)
from app.record import CustomerRecord


def test_record_is_135_bytes():
    assert RECORD_LENGTH == 135


def test_short_line_is_space_padded_and_long_line_truncated():
    assert read_record("00042") == "00042" + " " * 130
    assert read_record("x" * 200) == "x" * 135
    assert read_record("") == " " * 135


def test_trailing_cr_is_stripped_but_a_trailing_tab_is_data():
    assert read_record("abc\r") == "abc".ljust(135)
    assert read_record("abc\t") == "abc\t".ljust(135)


def test_write_strips_only_trailing_spaces():
    assert write_record("abc" + " " * 132) == "abc"
    assert write_record(" " * 135) == ""
    assert write_record("abc\t" + " " * 131) == "abc\t"


def test_split_and_join_round_trip():
    text = join_records(["a".ljust(135), "b".ljust(135)])
    assert text == "a\nb\n"
    assert split_records(text) == ["a".ljust(135), "b".ljust(135)]
    assert split_records("") == []


def test_empty_line_becomes_an_all_blank_record():
    assert split_records("\n") == [" " * 135]


def test_bytes_not_characters():
    hebrew = to_byte_string("בנק")
    assert len(hebrew) == 6  # three two-byte characters
    assert from_byte_string(hebrew) == "בנק"


@pytest.mark.parametrize(
    ("key", "expected"),
    [
        ("00000", 0),
        (" 0000", 0),
        ("@0000", 0),
        ("p0000", 0),
        ("00012", 12),
        ("   12", 12),
        ("0000:", 10),
        ("0001*", 4),
        ("ABCDE", 190121),
        ("abcde", 545673),
        ("0000/", 4294967295),
        ("     ", 4294967280),
        ("\xff\xff\xff\xff\xff", 100000),
        ("\x00\x00\x00\x00\x00", -100000),
    ],
)
def test_display_key_value_matches_the_binary(key, expected):
    """Every expectation here was read off the compiled GnuCOBOL binary (D-002)."""
    assert display_key_value(key) == expected


def test_record_rejects_a_wrong_length_area():
    with pytest.raises(ValueError):
        CustomerRecord("too short")


def test_json_view_trims_but_raw_does_not():
    record = CustomerRecord("00001" + "last".ljust(50) + "first".ljust(50) + "00002" + "c".ljust(25))
    assert record.last_name == "last".ljust(50)
    assert record.as_json()["last_name"] == "last"
    assert record.as_json()["raw"] == record.raw
    assert record.display_line() == record.raw
    assert record.file_line() == "00001" + "last".ljust(50) + "first".ljust(50) + "00002" + "c"
