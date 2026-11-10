#!/usr/bin/env python3
"""Port of unstring/unstring.cbl: the UNSTRING verb.

``unstring()`` reproduces the COBOL semantics (multiple delimiters, ALL,
DELIMITER IN, COUNT IN, WITH POINTER, TALLYING IN and ON OVERFLOW). For
simple cases ``str.split`` is shown alongside as the idiomatic equivalent.
"""
from dataclasses import dataclass, field


@dataclass
class Delimiter:
    text: str
    all: bool = False  # DELIMITED BY ALL "x" swallows consecutive repeats


@dataclass
class UnstringResult:
    values: list = field(default_factory=list)
    delimiters: list = field(default_factory=list)  # DELIMITER IN
    counts: list = field(default_factory=list)  # COUNT IN
    pointer: int = 1  # WITH POINTER (1-based, like COBOL)
    tally: int = 0  # TALLYING IN
    overflow: bool = False  # ON OVERFLOW


def unstring(source: str, delimiters, num_fields: int, pointer: int = 1, tally: int = 0) -> UnstringResult:
    result = UnstringResult(pointer=pointer, tally=tally)
    if not 1 <= pointer <= len(source):
        result.overflow = True
        return result

    pos = pointer - 1
    for _ in range(num_fields):
        if pos >= len(source):
            break
        best = None
        for delim in delimiters:
            idx = source.find(delim.text, pos)
            if idx != -1 and (best is None or idx < best[0]):
                best = (idx, delim)
        if best is None:
            value, delim_text, pos = source[pos:], "", len(source)
        else:
            idx, delim = best
            value, delim_text = source[pos:idx], delim.text
            pos = idx + len(delim.text)
            while delim.all and source.startswith(delim.text, pos):
                pos += len(delim.text)
        result.values.append(value)
        result.delimiters.append(delim_text)
        result.counts.append(len(value))
        result.tally += 1

    result.pointer = pos + 1
    result.overflow = pos < len(source)
    return result


def pic_x(value: str, size: int) -> str:
    return value[:size].ljust(size)


def pic_9(value: int, digits: int) -> str:
    return str(abs(int(value)) % 10**digits).zfill(digits)


def move_to_pic_9(text: str, digits: int) -> int:
    """MOVE an alphanumeric to an unsigned numeric field (right-aligned digits)."""
    digits_only = "".join(ch for ch in text if ch.isdigit())
    return int(digits_only[-digits:] or 0)


def separator() -> None:
    print(" ")
    print("=================================================")


def example_1(source: str) -> None:
    separator()
    print("EX 1 : SIMPLE UNSTRING")
    print(" ")
    print("SOURCE STRING: " + source)
    # Idiomatic Python: str.split with a single-space separator.
    parts = source.split(" ") + ["", ""]
    print("PART1: " + pic_x(parts[0], 15))
    print("PART2: " + pic_x(parts[1], 15))


def example_2(source: str) -> None:
    separator()
    print("EX 2 : UNSTRING MULTIPLE TIMES INTO SAME DEST.")
    print(" ")
    print("SOURCE STRING: " + source)
    pointer = 1
    part_1 = " " * 15
    for _ in range(2):
        res = unstring(source, [Delimiter(" ", all=True)], 1, pointer)
        pointer = res.pointer
        print("ERROR: OVERFLOW" if res.overflow else "Successfully unstrung.")
        if res.values:
            part_1 = pic_x(res.values[0], 15)
        print("PART VALUE: " + part_1)
        print("POINTER: " + pic_9(pointer, 5))


def example_3(source: str) -> None:
    separator()
    print("EX 3 : UNSTRING INTO EXPLICIT FIELDS")
    print(" ")
    print("SOURCE STRING: " + source)
    res = unstring(source, [Delimiter(" ", all=True)], 2, 1)
    print("ERROR: OVERFLOW" if res.overflow else "Successfully unstrung.")
    print("PART1: " + pic_x(res.values[0], 15))
    print("PART2: " + pic_x(res.values[1], 15))
    print("POINTER: " + pic_9(res.pointer, 5))


def example_4(ws_delimiter: str) -> None:
    separator()
    print("EX 4 : UNSTRING WITH MULTIPLE DELIMITERS ")
    source = pic_x("A<B<CD>E%FG!HIJ|KL!MN>OP#QR!ST", 30)
    print(" ")
    print("SOURCE STRING: " + source)
    delims = [Delimiter("<", all=True), Delimiter(">"), Delimiter("!"), Delimiter(ws_delimiter)]
    pointer, fields_filled = 1, 0
    while pointer <= len(source):
        res = unstring(source, delims, 1, pointer, fields_filled)
        pointer, fields_filled = res.pointer, res.tally
        print(" ")
        print("VALUE: " + pic_x(res.values[0], 5))
        print("DELIMITER: " + pic_x(res.delimiters[0], 1))
        print("CHAR COUNT:" + pic_9(res.counts[0], 1))
        print("CURRENT POINTER: " + pic_9(pointer, 5))
        print("TOTAL FIELDS FILLED: " + pic_9(fields_filled, 2))
        print("-------------------------------------------")


def example_5(ws_delimiter: str) -> None:
    separator()
    print("EX 5 : UNSTRING WITH MULTIPLE DELIMITERS INTO MULTIPLE DESTINATIONS")
    source = pic_x("A<B<CD>EFG!HIJ|KLMN>O", 30)
    print(" ")
    print("SOURCE STRING: " + source)
    delims = [Delimiter("<", all=True), Delimiter(">", all=True), Delimiter("!"), Delimiter(ws_delimiter)]
    res = unstring(source, delims, 6)
    for idx in range(6):
        print(" ")
        print(f"STRING NUMBER: +{idx + 1:09d}")
        print("VALUE: " + pic_x(res.values[idx], 5))
        print("DELIMITER: " + pic_x(res.delimiters[idx], 1))
        print("CHAR COUNT:" + pic_9(res.counts[idx], 1))
        print("-------------------------------------------")
    print("TOTALS: ")
    print("FIELDS FILLED: " + pic_9(res.tally, 2))


def example_6() -> None:
    separator()
    print("EX 6 : UNSTRING FORMATTED NUMBER")
    print(" ")
    ws_source_num = f"${123456.12:,.2f}".rjust(11)  # pic $999,999.99
    print("SOURCE VALUE: " + ws_source_num)
    res = unstring(ws_source_num[1:], [Delimiter(","), Delimiter(".")], 3)
    for idx, value in enumerate(res.values, start=1):
        print(f"PART {idx}: " + pic_9(move_to_pic_9(value, 3), 3))
    print(" ")


def main() -> int:
    ws_delimiter = "|"
    source = pic_x("Hello World", 30)
    example_1(source)
    example_2(source)
    example_3(source)
    example_4(ws_delimiter)
    example_5(ws_delimiter)
    example_6()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
