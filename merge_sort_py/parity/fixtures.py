"""Fixtures for the reconciliation harness.

Every fixture names the boundary it crosses; between them they cover each rule and each
ruling in ``docs/phase0-contracts.md``. Fixtures are ``bytes`` because the record layout is
applied to bytes.
"""

from __future__ import annotations

from dataclasses import dataclass

RECORD_LENGTH = 135


@dataclass(frozen=True)
class Fixture:
    name: str
    boundary: str
    test_file_1: bytes
    test_file_2: bytes


def record(
    customer_id: bytes,
    last_name: bytes,
    first_name: bytes,
    contract_id: bytes,
    comment: bytes,
) -> bytes:
    """Build a 135 byte record area from raw field bytes, then write it line sequential."""
    raw = (
        customer_id.ljust(5)[:5]
        + last_name.ljust(50)[:50]
        + first_name.ljust(50)[:50]
        + contract_id.ljust(5)[:5]
        + comment.ljust(25)[:25]
    )
    assert len(raw) == RECORD_LENGTH
    return raw.rstrip(b" ")


def file_of(*records: bytes) -> bytes:
    return b"".join(line + b"\n" for line in records)


def _simple(customer_id: bytes, tag: bytes, contract_id: bytes) -> bytes:
    return record(customer_id, tag, b"first-" + tag, contract_id, b"c-" + tag)


_LEADING_ZERO_NIBBLE = (b"0", b" ", b"@", b"P", b"`", b"p")

FIXTURES: tuple[Fixture, ...] = (
    Fixture(
        name="single_record",
        boundary="one record, one empty file",
        test_file_1=file_of(_simple(b"00001", b"solo", b"00042")),
        test_file_2=b"",
    ),
    Fixture(
        name="empty_both",
        boundary="zero records: both inputs empty (empty result, not an error)",
        test_file_1=b"",
        test_file_2=b"",
    ),
    Fixture(
        name="empty_second_file",
        boundary="MERGE with one empty USING file",
        test_file_1=file_of(
            _simple(b"00009", b"a", b"00009"),
            _simple(b"00001", b"b", b"00001"),
        ),
        test_file_2=b"",
    ),
    Fixture(
        name="unsorted_inputs",
        boundary="D-001: MERGE inputs are not pre-sorted, GnuCOBOL sorts them anyway",
        test_file_1=file_of(
            _simple(b"00050", b"e1", b"05050"),
            _simple(b"00025", b"e2", b"07725"),
            _simple(b"00075", b"e3", b"01175"),
        ),
        test_file_2=file_of(
            _simple(b"00999", b"w1", b"01610"),
            _simple(b"00003", b"w2", b"03331"),
        ),
    ),
    Fixture(
        name="duplicate_ids_across_files",
        boundary="D-003: equal MERGE keys keep file-1-before-file-2 order",
        test_file_1=file_of(
            _simple(b"00007", b"A1", b"00100"),
            _simple(b"00007", b"A2", b"00100"),
            _simple(b"00003", b"A3", b"00200"),
        ),
        test_file_2=file_of(
            _simple(b"00007", b"B1", b"00100"),
            _simple(b"00007", b"B2", b"00300"),
            _simple(b"00003", b"B3", b"00200"),
        ),
    ),
    Fixture(
        name="duplicate_contract_ids",
        boundary="D-003: equal DESCENDING SORT keys keep merged-file order, not reversed",
        test_file_1=file_of(
            _simple(b"00001", b"c1", b"00500"),
            _simple(b"00002", b"c2", b"00500"),
            _simple(b"00003", b"c3", b"00500"),
            _simple(b"00004", b"c4", b"00900"),
        ),
        test_file_2=file_of(_simple(b"00005", b"c5", b"00500")),
    ),
    Fixture(
        name="leading_zero_nibble_keys",
        boundary="D-002: bytes whose low nibble is 0 are skipped as leading zeros",
        test_file_1=file_of(
            *(_simple(byte + b"0000", b"lz-" + byte, byte + b"0000") for byte in _LEADING_ZERO_NIBBLE)
        ),
        test_file_2=file_of(_simple(b"00001", b"lz-one", b"00001")),
    ),
    Fixture(
        name="non_numeric_keys",
        boundary="D-002: letters and punctuation in PIC 9(5), including accumulator wrap",
        test_file_1=file_of(
            _simple(b"ABCDE", b"k-alpha", b"ABCDE"),
            _simple(b"abcde", b"k-lower", b"abcde"),
            _simple(b"0000:", b"k-colon", b"0000:"),
            _simple(b"0001*", b"k-star", b"0001*"),
            _simple(b"   12", b"k-sp12", b"   12"),
            _simple(b"00012", b"k-0012", b"00012"),
        ),
        test_file_2=file_of(
            _simple(b"+0012", b"k-plus", b"+0012"),
            _simple(b"-0001", b"k-minus", b"-0001"),
            _simple(b"     ", b"k-blank", b"     "),
            _simple(b"0000 ", b"k-trailsp", b"0000 "),
            _simple(b"0000/", b"k-slash", b"0000/"),
        ),
    ),
    Fixture(
        name="sentinel_bytes",
        boundary="D-002: a leading 0x00 compares below and a leading 0xFF above every digit value",
        test_file_1=file_of(
            _simple(b"\x00\x00\x00\x00\x00", b"nul", b"\x00\x00\x00\x00\x00"),
            _simple(b"\xff\xff\xff\xff\xff", b"ff", b"\xff\xff\xff\xff\xff"),
            _simple(b"\xffbcde", b"ff-lead", b"\xffbcde"),
            _simple(b"0000\xff", b"ff-trail", b"0000\xff"),
            _simple(b"99999", b"nines", b"99999"),
            _simple(b"abcde", b"alpha", b"abcde"),
        ),
        test_file_2=b"",
    ),
    Fixture(
        name="short_and_ragged_lines",
        boundary="D-005: lines shorter than 135 bytes, an empty line, and a line longer than 135",
        test_file_1=(
            b"00042short\n"
            b"\n"
            + _simple(b"00008", b"exact", b"00008")
            + b"\n"
            + _simple(b"00003", b"toolong", b"00003")
            + b"Z" * 40
            + b"\n"
        ),
        test_file_2=b"     \n",
    ),
    Fixture(
        name="padding_is_data",
        boundary="D-004: leading and trailing spaces inside PIC X(50) survive the round trip",
        test_file_1=file_of(
            record(b"00001", b"  leading", b"trailing  ", b"00001", b"  spaced  "),
            record(b"00002", b"", b"", b"00002", b""),
        ),
        test_file_2=b"",
    ),
    Fixture(
        name="trailing_tab_and_cr",
        boundary="D-006: trailing spaces stripped on write; a trailing tab is data; a trailing CR is dropped",
        test_file_1=(
            b"00001" + b"tab".ljust(50) + b"x".ljust(50) + b"00001" + b"c\t" + b"\n"
            b"00002" + b"cr".ljust(50) + b"x".ljust(50) + b"00002" + b"c\r" + b"\n"
            b"00003" + b" " * 50 + b" " * 50 + b"00003" + b" " * 25 + b"\n"
        ),
        test_file_2=b"",
    ),
    Fixture(
        name="multibyte_utf8",
        boundary="D-007: multibyte UTF-8 shifts the byte layout; the port must not slice characters",
        test_file_1=file_of(
            record(b"00001", "בנק לאומי".encode(), "שלום".encode(), b"00001", "עברית".encode()),
            record(b"00002", "café".encode(), "naïve".encode(), b"00002", "€uro".encode()),
        ),
        test_file_2=file_of(record(b"00003", b"ascii", b"ascii", b"00003", b"ascii")),
    ),
    Fixture(
        name="large_stable_run",
        boundary="stability at scale: 1500 records sharing one key on both sort passes",
        test_file_1=file_of(*(_simple(b"00001", b"t%05d" % index, b"00001") for index in range(1000))),
        test_file_2=file_of(*(_simple(b"00001", b"u%05d" % index, b"00001") for index in range(500))),
    ),
)


FIXTURES_BY_NAME = {fixture.name: fixture for fixture in FIXTURES}
