#!/usr/bin/env python3
"""Port of numval_test/numval_test.cbl: convert text to a number (NUMVAL)."""
import re
from decimal import Decimal

_NUMVAL_RE = re.compile(
    r"^\s*(?P<lead>[+-])?\s*(?P<num>\d+(?:\.\d*)?|\.\d+)\s*(?P<trail>[+-]|CR|DB)?\s*$",
    re.IGNORECASE,
)


def numval(text: str) -> Decimal:
    """FUNCTION NUMVAL: leading/trailing sign (+, -, CR, DB) and spaces allowed.

    GnuCOBOL returns 0 for invalid input, so this does too.
    """
    match = _NUMVAL_RE.match(text)
    if not match or (match["lead"] and match["trail"]):
        return Decimal(0)
    value = Decimal(match["num"])
    sign = (match["lead"] or match["trail"] or "").upper()
    return -value if sign in ("-", "CR", "DB") else value


def accept_pic_9(text: str, digits: int) -> int:
    """ACCEPT into PIC 9(digits): GnuCOBOL keeps the integer part's leftmost
    digits and yields 0 for non-numeric input."""
    integer_part = text.strip().split(".")[0]
    if not integer_part.isdigit() or not integer_part.isascii():
        return 0
    return int(integer_part[:digits])


def format_comp2(value: float) -> str:
    """DISPLAY of a COMP-2 (double) the way GnuCOBOL prints it."""
    text = repr(float(value))
    text = text.removesuffix(".0")
    return text.replace("e", "E")


def read_line(prompt: str) -> str:
    try:
        return input(prompt)
    except EOFError:
        return ""


def main() -> int:
    ws_x_val = read_line("Enter first number: ")[:10]
    ws_9_val = accept_pic_9(read_line("Enter second number: "), 10)

    # COMPUTE runs in decimal arithmetic; the result lands in a COMP-2 (float).
    ws_total = float(numval(ws_x_val) + ws_9_val)

    print("Total: " + format_comp2(ws_total))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
