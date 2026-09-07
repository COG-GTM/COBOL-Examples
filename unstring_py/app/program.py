"""Paragraph-for-paragraph port of ``unstring/unstring.cbl`` (``unstring-example``).

``main-procedure`` is a single straight-line paragraph in the COBOL, so the port keeps
one function per commented ``EXAMPLE n`` block, run in the same order over one shared
``WorkingStorage`` — the examples deliberately observe each other's leftovers (ruling
D-002), so the state must be shared, not rebuilt.

Every ``DISPLAY`` is appended to ``Program.console`` in source order; ``stdout_bytes()``
reproduces the program's output byte for byte, which is what the parity harness diffs.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from decimal import Decimal

from app.cobol_types import (
    DEST_LEN,
    DEST_NUM_LEN,
    MULTI_OCCURS,
    PART_LEN,
    SOURCE_LEN,
    display_index,
    display_unsigned,
    format_source_num,
    from_byte_string,
    move_to_alphanumeric,
    move_to_byte_source,
    move_to_source_num,
    move_to_unsigned_int,
)
from app.unstring import Delimiter, ReceivedField, Receiver, unstring

RULE = "================================================="
DASHES = "-------------------------------------------"

#: ``move "Hello World" to ws-source-str``
DEFAULT_SIMPLE_SOURCE = "Hello World"
#: ``move "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST" to ws-source-str``
DEFAULT_MULTI_DELIM_SOURCE = "A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST"
#: ``move "A<B<CD>EFG!HIJ|KLMN>O" to ws-source-str``
DEFAULT_MULTI_DEST_SOURCE = "A<B<CD>EFG!HIJ|KLMN>O"
#: ``01 ws-delimiter pic x value '|'.``
DEFAULT_DELIMITER = "|"
#: ``move 123456.12 to ws-source-num``
DEFAULT_SOURCE_NUM = Decimal("123456.12")

OVERFLOW_MESSAGE = "ERROR: OVERFLOW"
NO_OVERFLOW_MESSAGE = "Successfully unstrung."


@dataclass
class MultiEntry:
    """One occurrence of ``ws-multi-dest-info``."""

    dest_str: str = " " * DEST_LEN
    delimiter: str = " "
    char_count: str = "0"


@dataclass
class WorkingStorage:
    """The program's ``WORKING-STORAGE SECTION``, initialised as GnuCOBOL does."""

    source_str: str = " " * SOURCE_LEN
    part_1: str = " " * PART_LEN
    part_2: str = " " * PART_LEN
    delimiter: str = DEFAULT_DELIMITER
    single_fields_filled: int = 0
    single_dest_str: str = " " * DEST_LEN
    single_delimiter: str = " "
    single_char_count: str = "0"
    multi_fields_filled: int = 0
    multi: list[MultiEntry] = field(default_factory=lambda: [MultiEntry() for _ in range(MULTI_OCCURS)])
    pointer: int = 0
    source_num: Decimal = Decimal(0)
    dest_num: list[str] = field(default_factory=lambda: ["0" * DEST_NUM_LEN for _ in range(DEST_NUM_LEN)])


@dataclass
class ExampleView:
    """JSON-facing view of one example: its console lines plus its statistics."""

    number: int
    title: str
    source: str
    lines: list[str] = field(default_factory=list)
    stats: dict[str, object] = field(default_factory=dict)
    overflow: bool = False


class Program:
    """Runs the six examples over one shared working storage."""

    def __init__(
        self,
        simple_source: str = DEFAULT_SIMPLE_SOURCE,
        multi_delim_source: str = DEFAULT_MULTI_DELIM_SOURCE,
        multi_dest_source: str = DEFAULT_MULTI_DEST_SOURCE,
        delimiter: str = DEFAULT_DELIMITER,
        source_num: Decimal = DEFAULT_SOURCE_NUM,
    ) -> None:
        self.simple_source = move_to_byte_source(simple_source)
        self.multi_delim_source = move_to_byte_source(multi_delim_source)
        self.multi_dest_source = move_to_byte_source(multi_dest_source)
        self.delimiter = move_to_alphanumeric(delimiter, 1)
        self.source_num = source_num
        self.ws = WorkingStorage(delimiter=self.delimiter)
        self.console: list[str] = []
        self.examples: list[ExampleView] = []
        self._current: ExampleView | None = None

    # ------------------------------------------------------------------ display

    def _display(self, text: str = " ") -> None:
        """``DISPLAY``: one line on stdout, and on the current example's transcript."""
        self.console.append(text)
        if self._current is not None:
            self._current.lines.append(text)

    def _begin(self, number: int, title: str, source: str = "") -> ExampleView:
        view = ExampleView(number=number, title=title, source=from_byte_string(source))
        self.examples.append(view)
        self._current = view
        return view

    # ---------------------------------------------------------------- procedure

    def run(self) -> Program:
        """``main-procedure``."""
        self.ws.source_str = self.simple_source
        self.example_1_simple_unstring()
        self.example_2_multiple_times_same_dest()
        self.example_3_explicit_fields()
        self.example_4_multiple_delimiters()
        self.example_5_multiple_destinations()
        self.example_6_formatted_number()
        return self

    def example_1_simple_unstring(self) -> ExampleView:
        view = self._begin(1, "SIMPLE UNSTRING", self.ws.source_str)
        self._display()
        self._display(RULE)
        self._display("EX 1 : SIMPLE UNSTRING")
        self._display()
        self._display("SOURCE STRING: " + self.ws.source_str)

        result = unstring(
            self.ws.source_str,
            (Delimiter(" "),),
            (Receiver(PART_LEN), Receiver(PART_LEN)),
        )
        self._store_parts(result.fields)

        self._display("PART1: " + self.ws.part_1)
        self._display("PART2: " + self.ws.part_2)
        view.stats = {"part_1": self.ws.part_1, "part_2": self.ws.part_2}
        return view

    def example_2_multiple_times_same_dest(self) -> ExampleView:
        self.ws.pointer = 1
        view = self._begin(2, "UNSTRING MULTIPLE TIMES INTO SAME DEST.", self.ws.source_str)
        self._display()
        self._display(RULE)
        self._display("EX 2 : UNSTRING MULTIPLE TIMES INTO SAME DEST.")
        self._display()
        self._display("SOURCE STRING: " + self.ws.source_str)

        passes: list[dict[str, object]] = []
        for _ in range(2):
            result = unstring(
                self.ws.source_str,
                (Delimiter(" ", all_=True),),
                (Receiver(PART_LEN),),
                pointer=self.ws.pointer,
            )
            if result.fields:
                self.ws.part_1 = result.fields[0].value
            self.ws.pointer = result.pointer
            self._display(OVERFLOW_MESSAGE if result.overflow else NO_OVERFLOW_MESSAGE)
            self._display("PART VALUE: " + self.ws.part_1)
            self._display("POINTER: " + display_unsigned(self.ws.pointer, 5))
            passes.append(
                {
                    "overflow": result.overflow,
                    "message": OVERFLOW_MESSAGE if result.overflow else NO_OVERFLOW_MESSAGE,
                    "part_value": self.ws.part_1,
                    "pointer": self.ws.pointer,
                }
            )
            view.overflow = view.overflow or result.overflow
        view.stats = {"passes": passes}
        return view

    def example_3_explicit_fields(self) -> ExampleView:
        view = self._begin(3, "UNSTRING INTO EXPLICIT FIELDS", self.ws.source_str)
        self._display()
        self._display(RULE)
        self._display("EX 3 : UNSTRING INTO EXPLICIT FIELDS")
        self.ws.pointer = 1
        self._display()
        self._display("SOURCE STRING: " + self.ws.source_str)

        result = unstring(
            self.ws.source_str,
            (Delimiter(" ", all_=True),),
            (Receiver(PART_LEN), Receiver(PART_LEN)),
            pointer=self.ws.pointer,
        )
        self._store_parts(result.fields)
        self.ws.pointer = result.pointer
        self._display(OVERFLOW_MESSAGE if result.overflow else NO_OVERFLOW_MESSAGE)
        self._display("PART1: " + self.ws.part_1)
        self._display("PART2: " + self.ws.part_2)
        self._display("POINTER: " + display_unsigned(self.ws.pointer, 5))
        view.overflow = result.overflow
        view.stats = {
            "part_1": self.ws.part_1,
            "part_2": self.ws.part_2,
            "pointer": self.ws.pointer,
            "overflow": result.overflow,
            "message": OVERFLOW_MESSAGE if result.overflow else NO_OVERFLOW_MESSAGE,
        }
        return view

    def example_4_multiple_delimiters(self) -> ExampleView:
        view = self._begin(4, "UNSTRING WITH MULTIPLE DELIMITERS")
        self._display()
        self._display(RULE)
        self._display("EX 4 : UNSTRING WITH MULTIPLE DELIMITERS ")

        self.ws.pointer = 1
        self.ws.source_str = self.multi_delim_source
        view.source = from_byte_string(self.ws.source_str)
        self._display()
        self._display("SOURCE STRING: " + self.ws.source_str)

        delimiters = (
            Delimiter("<", all_=True),
            Delimiter(">"),
            Delimiter("!"),
            Delimiter(self.ws.delimiter),
        )
        iterations: list[dict[str, object]] = []
        while self.ws.pointer <= SOURCE_LEN:
            result = unstring(
                self.ws.source_str,
                delimiters,
                (Receiver(DEST_LEN, delimiter_width=1, count_width=1),),
                pointer=self.ws.pointer,
                tallying=self.ws.single_fields_filled,
            )
            if result.fields:
                received = result.fields[0]
                self.ws.single_dest_str = received.value
                self.ws.single_delimiter = received.delimiter or " "
                self.ws.single_char_count = received.count or "0"
            self.ws.pointer = result.pointer
            self.ws.single_fields_filled = move_to_unsigned_int(result.tallying, 2)

            self._display()
            self._display("VALUE: " + self.ws.single_dest_str)
            self._display("DELIMITER: " + self.ws.single_delimiter)
            self._display("CHAR COUNT:" + self.ws.single_char_count)
            self._display("CURRENT POINTER: " + display_unsigned(self.ws.pointer, 5))
            self._display("TOTAL FIELDS FILLED: " + display_unsigned(self.ws.single_fields_filled, 2))
            self._display(DASHES)
            examined = result.fields[0].examined if result.fields else ""
            iterations.append(
                {
                    "value": self.ws.single_dest_str,
                    "delimiter": self.ws.single_delimiter,
                    "char_count": self.ws.single_char_count,
                    "examined_length": len(examined),
                    "truncated": len(examined) > DEST_LEN,
                    "count_truncated": len(examined) > 9,
                    "pointer": self.ws.pointer,
                    "fields_filled": self.ws.single_fields_filled,
                }
            )
        view.stats = {"iterations": iterations}
        return view

    def example_5_multiple_destinations(self) -> ExampleView:
        view = self._begin(5, "UNSTRING WITH MULTIPLE DELIMITERS INTO MULTIPLE DESTINATIONS")
        self._display()
        self._display(RULE)
        self._display("EX 5 : UNSTRING WITH MULTIPLE DELIMITERS INTO MULTIPLE DESTINATIONS")

        self.ws.source_str = self.multi_dest_source
        view.source = from_byte_string(self.ws.source_str)
        self._display()
        self._display("SOURCE STRING: " + self.ws.source_str)

        delimiters = (
            Delimiter("<", all_=True),
            Delimiter(">", all_=True),
            Delimiter("!"),
            Delimiter(self.ws.delimiter),
        )
        result = unstring(
            self.ws.source_str,
            delimiters,
            tuple(Receiver(DEST_LEN, delimiter_width=1, count_width=1) for _ in range(MULTI_OCCURS)),
            tallying=self.ws.multi_fields_filled,
        )
        for index, received in enumerate(result.fields):
            entry = self.ws.multi[index]
            entry.dest_str = received.value
            entry.delimiter = received.delimiter or " "
            entry.char_count = received.count or "0"
        self.ws.multi_fields_filled = move_to_unsigned_int(result.tallying, 2)

        entries: list[dict[str, object]] = []
        for index in range(1, MULTI_OCCURS + 1):
            entry = self.ws.multi[index - 1]
            self._display()
            self._display("STRING NUMBER: " + display_index(index))
            self._display("VALUE: " + entry.dest_str)
            self._display("DELIMITER: " + entry.delimiter)
            self._display("CHAR COUNT:" + entry.char_count)
            self._display(DASHES)
            examined = result.fields[index - 1].examined if index <= len(result.fields) else ""
            entries.append(
                {
                    "index": index,
                    "value": entry.dest_str,
                    "delimiter": entry.delimiter,
                    "char_count": entry.char_count,
                    "filled": index <= len(result.fields),
                    "examined_length": len(examined),
                    "truncated": len(examined) > DEST_LEN,
                }
            )

        self._display("TOTALS: ")
        self._display("FIELDS FILLED: " + display_unsigned(self.ws.multi_fields_filled, 2))
        view.overflow = result.overflow
        view.stats = {
            "entries": entries,
            "fields_filled": self.ws.multi_fields_filled,
            "table_overflow": result.overflow,
        }
        return view

    def example_6_formatted_number(self) -> ExampleView:
        view = self._begin(6, "UNSTRING FORMATTED NUMBER")
        self._display()
        self._display(RULE)
        self._display("EX 6 : UNSTRING FORMATTED NUMBER")
        self._display()

        # The MOVE is what constrains the value to the PICTURE (D-006/D-007); working
        # storage holds the stored value, not the sender.
        self.ws.source_num = move_to_source_num(self.source_num)
        edited = format_source_num(self.ws.source_num)
        view.source = edited
        self._display("SOURCE VALUE: " + edited)

        result = unstring(
            edited[1:],  # ws-source-num(2:) skips the '$'
            (Delimiter(","), Delimiter(".")),
            tuple(Receiver(DEST_NUM_LEN, numeric=True) for _ in range(3)),
        )
        for index, received in enumerate(result.fields):
            self.ws.dest_num[index] = received.value

        for index in range(1, 4):
            self._display(f"PART {index}: " + self.ws.dest_num[index - 1])
        self._display()
        view.stats = {
            "edited_value": edited,
            "parts": list(self.ws.dest_num),
            "amount": str(self.ws.source_num),
        }
        return view

    # ------------------------------------------------------------------ helpers

    def _store_parts(self, fields: list[ReceivedField]) -> None:
        if len(fields) > 0:
            self.ws.part_1 = fields[0].value
        if len(fields) > 1:
            self.ws.part_2 = fields[1].value

    def stdout_text(self) -> str:
        return "".join(line + "\n" for line in self.console)

    def stdout_bytes(self) -> bytes:
        return self.stdout_text().encode("latin-1")


def run_program(
    simple_source: str = DEFAULT_SIMPLE_SOURCE,
    multi_delim_source: str = DEFAULT_MULTI_DELIM_SOURCE,
    multi_dest_source: str = DEFAULT_MULTI_DEST_SOURCE,
    delimiter: str = DEFAULT_DELIMITER,
    source_num: Decimal = DEFAULT_SOURCE_NUM,
) -> Program:
    return Program(
        simple_source=simple_source,
        multi_delim_source=multi_delim_source,
        multi_dest_source=multi_dest_source,
        delimiter=delimiter,
        source_num=source_num,
    ).run()


__all__ = [
    "DEFAULT_DELIMITER",
    "DEFAULT_MULTI_DEST_SOURCE",
    "DEFAULT_MULTI_DELIM_SOURCE",
    "DEFAULT_SIMPLE_SOURCE",
    "DEFAULT_SOURCE_NUM",
    "ExampleView",
    "Program",
    "run_program",
]
