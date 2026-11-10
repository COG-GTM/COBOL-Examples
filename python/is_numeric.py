#!/usr/bin/env python3
"""Port of is_numeric/is_numeric.cbl: the IS NUMERIC class test."""
import re

FIELD_SIZE = 10  # pic x(10)


def is_numeric(value: str) -> bool:
    """COBOL IS NUMERIC on alphanumeric data: every character is 0-9.

    str.isdigit() would also accept characters such as '²', so a regex is used.
    """
    return re.fullmatch(r"[0-9]+", value) is not None


def pic_x(value: str, size: int = FIELD_SIZE) -> str:
    return value[:size].ljust(size)


def pic_x_justified_right(value: str, size: int = FIELD_SIZE) -> str:
    return value[-size:].rjust(size) if value else " " * size


def read_line(prompt: str) -> str:
    try:
        return input(prompt)
    except EOFError:
        return ""


def report(value: str) -> None:
    print(value + (" is numeric!" if is_numeric(value) else " is not numeric."))


def process_plain() -> None:
    # Trailing spaces from the PIC X(10) padding make this fail.
    report(pic_x(read_line("(plain) Enter a value: ")))


def process_zero_fill() -> None:
    value = pic_x_justified_right(read_line("(right justify, zero fill) Enter another value: "))
    # INSPECT ... REPLACING LEADING SPACES BY '0'
    stripped = value.lstrip(" ")
    report("0" * (len(value) - len(stripped)) + stripped)


def process_trim() -> None:
    report(pic_x(read_line("(trim) Enter a third value: ")).strip(" "))


def main() -> int:
    process_plain()
    process_zero_fill()
    process_trim()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
