"""Fixtures for the reconciliation harness.

Every boundary documented in ``docs/phase0-contracts.md`` is crossed here: no delimiter
at all, a leading delimiter, a trailing delimiter, adjacent delimiters (empty tokens), a
token wider than ``PIC X(5)``, a token longer than the ``PIC 9`` ``COUNT IN`` can hold,
more tokens than the ``OCCURS 6`` table, an empty source, a full-width (30 byte) source,
and a pointer that starts beyond the end of the field.

The ``legacy`` fixture is the only one whose oracle is the untouched
``unstring/unstring.cbl`` binary; the rest run against the generated probe (see
``parity/oracle.py``), which mirrors it statement for statement.
"""

from __future__ import annotations

from dataclasses import dataclass
from decimal import Decimal

from app.program import (
    DEFAULT_DELIMITER,
    DEFAULT_MULTI_DELIM_SOURCE,
    DEFAULT_MULTI_DEST_SOURCE,
    DEFAULT_SIMPLE_SOURCE,
    DEFAULT_SOURCE_NUM,
)


@dataclass(frozen=True)
class Fixture:
    name: str
    boundary: str
    simple_source: str = DEFAULT_SIMPLE_SOURCE
    multi_delim_source: str = DEFAULT_MULTI_DELIM_SOURCE
    multi_dest_source: str = DEFAULT_MULTI_DEST_SOURCE
    delimiter: str = DEFAULT_DELIMITER
    amount: Decimal = DEFAULT_SOURCE_NUM
    #: ``legacy`` fixtures are diffed against the unmodified program binary.
    legacy: bool = False


FIXTURES: tuple[Fixture, ...] = (
    Fixture("legacy", "the unmodified program's own literals, diffed against its binary", legacy=True),
    Fixture(
        "no_delimiter",
        "no delimiter anywhere in any source: one token, DELIMITER IN is spaces",
        simple_source="HelloWorld",
        multi_delim_source="ABCDEFGHIJ",
        multi_dest_source="ABCDEFGHIJ",
    ),
    Fixture(
        "leading_delimiter",
        "source starts with a delimiter: first token is empty, COUNT IN 0",
        simple_source=" Hello World",
        multi_delim_source="<A>B!C|D",
        multi_dest_source="!A<B>C|D",
    ),
    Fixture(
        "trailing_delimiter",
        "source ends with a delimiter before the PIC X(30) padding",
        simple_source="Hello World ",
        multi_delim_source="A<B>C!D|",
        multi_dest_source="A<B>C!D|",
    ),
    Fixture(
        "adjacent_delimiters",
        "adjacent delimiters: ALL '<' collapses, bare '!'/'|' yield empty tokens",
        simple_source="Hello  World",
        multi_delim_source="A<<B!!C||D>>E",
        multi_dest_source="A<<B!!C||D>>E",
    ),
    Fixture(
        "token_longer_than_dest",
        "tokens wider than PIC X(5) are truncated on the right while COUNT IN keeps the full length",
        simple_source="Supercalifragilistic Expialidocious",
        multi_delim_source="ABCDEFGH<IJKLMNOP>QRSTUV",
        multi_dest_source="ABCDEFGH<IJKLMNOP>QRSTUV",
    ),
    Fixture(
        "count_wraps_past_nine",
        "a 12 character token: COUNT IN is PIC 9 and keeps only the last digit",
        multi_delim_source="ABCDEFGHIJKL<MN",
        multi_dest_source="ABCDEFGHIJKL<MN",
    ),
    Fixture(
        "more_tokens_than_table",
        "nine tokens into the OCCURS 6 table: the tail is dropped and TALLYING stops at 06",
        multi_delim_source="A<B<C<D<E<F<G<H<I",
        multi_dest_source="A<B>C!D|E<F>G!H|I",
    ),
    Fixture(
        "empty_source",
        "empty source: PIC X(30) of spaces, one all-space token, COUNT IN 30 wraps to 0",
        simple_source="",
        multi_delim_source="",
        multi_dest_source="",
    ),
    Fixture(
        "full_width_source",
        "all 30 bytes used, no trailing padding, and EX2's second pass starts past the field",
        simple_source="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123",
        multi_delim_source="A<BCDEFGHIJKLMNOPQRSTUVWXYZ012",
        multi_dest_source="A<B>C!D|EFGHIJKLMNOPQRSTUVWXYZ",
    ),
    Fixture(
        "source_longer_than_field",
        "input longer than PIC X(30) is truncated by the MOVE before UNSTRING sees it",
        simple_source="Hello World and then some more text past thirty",
        multi_delim_source="A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST!UV!WX",
        multi_dest_source="A<B<CD>EFG!HIJ|KLMN>OPQRSTUVWXYZ!!!",
    ),
    Fixture(
        "runtime_delimiter",
        "ws-delimiter changed from '|' to ';': the delimiter is data, not a literal",
        multi_delim_source="A;B<C;D>E;F",
        multi_dest_source="A;B<C;D>E;F",
        delimiter=";",
    ),
    Fixture(
        "amount_zero",
        "PIC $999,999.99 at zero: unsuppressed zeros, parts 000/000/000",
        amount=Decimal("0"),
    ),
    Fixture(
        "amount_truncates_cents",
        "third decimal is truncated toward zero, not rounded (D-006)",
        amount=Decimal("987654.999"),
    ),
    Fixture(
        "amount_overflows_picture",
        "more than six integer digits: high-order digits are dropped by the MOVE (D-007)",
        amount=Decimal("91234567.89"),
    ),
    Fixture(
        "amount_max",
        "the largest value the PICTURE holds",
        amount=Decimal("999999.99"),
    ),
)
