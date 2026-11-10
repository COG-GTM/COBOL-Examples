#!/usr/bin/env python3
"""Port of comp_test/comp_test.cbl: COMP (binary) vs DISPLAY numeric fields.

COBOL stores ``PIC 999 COMP`` as a binary integer and ``PIC 999`` as three
ASCII digits, yet both DISPLAY as three digits. ``PIC ZZ9`` suppresses
leading zeros. Pass ``--explain`` to also print the underlying storage.
"""
import argparse
import struct


def pic_9(value: int, digits: int = 3) -> str:
    """PIC 9(n): zero-padded, high-order digits truncated like COBOL MOVE."""
    return str(abs(int(value)) % 10**digits).zfill(digits)


def pic_z(value: int, digits: int = 3) -> str:
    """PIC Z(n-1)9: leading zeros replaced by spaces."""
    return str(abs(int(value)) % 10**digits).rjust(digits)


def accept_pic_9(text: str, digits: int = 3) -> int:
    """ACCEPT into PIC 9(n) (GnuCOBOL keeps the leftmost integer digits)."""
    integer_part = text.strip().split(".")[0]
    if not integer_part.isdigit() or not integer_part.isascii():
        return 0
    return int(integer_part[:digits])


def comp_storage(value: int) -> bytes:
    """PIC 999 COMP occupies a 2-byte big-endian binary field in GnuCOBOL."""
    return struct.pack(">H", value)


def display_storage(value: int) -> bytes:
    return pic_9(value).encode("ascii")


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--explain", action="store_true", help="show raw storage bytes")
    args = parser.parse_args(argv)

    ws_comp_val = 12
    ws_comp_val = int(pic_9(ws_comp_val * 2))  # MULTIPLY ... GIVING, truncated to 999
    print("COMP: " + pic_9(ws_comp_val))

    ws_disp_val = ws_comp_val
    print("DISP:" + pic_9(ws_disp_val))

    ws_dyn_disp_val = ws_comp_val
    print("DYNA:" + pic_z(ws_dyn_disp_val))

    try:
        raw = input("INPUT: ")
    except EOFError:
        raw = ""
    ws_input = accept_pic_9(raw)
    print("INPUT: " + pic_9(ws_input))

    ws_comp_val = ws_input
    print("COMP: " + pic_9(ws_comp_val))

    if args.explain:
        print()
        print(f"COMP    storage of {pic_9(ws_comp_val)}: {comp_storage(ws_comp_val).hex(' ')} (binary)")
        print(f"DISPLAY storage of {pic_9(ws_comp_val)}: {display_storage(ws_comp_val).hex(' ')} (ASCII digits)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
