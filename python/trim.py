#!/usr/bin/env python3
"""Port of trim/trim.cbl: the TRIM intrinsic function (LEADING/TRAILING)."""


def pic_x(value: str, size: int) -> str:
    """MOVE into a PIC X(size) field: truncate or pad with spaces."""
    return value[:size].ljust(size)


def trim(value: str, mode: str = "both") -> str:
    """COBOL FUNCTION TRIM only removes spaces (not tabs/newlines)."""
    if mode == "leading":
        return value.lstrip(" ")
    if mode == "trailing":
        return value.rstrip(" ")
    return value.strip(" ")


def main() -> int:
    ws_test_string_1 = pic_x("    hello world       ", 30)
    stars = pic_x("*" * 30, 30)

    print("--" + ws_test_string_1 + "--")
    print("--" + trim(ws_test_string_1) + "--")
    print("--" + trim(ws_test_string_1, "leading") + "--")
    print("--" + trim(ws_test_string_1, "trailing") + "--")

    # Moving a trimmed value back into a PIC X(30) re-pads it with spaces.
    for mode in (None, "both", "leading", "trailing"):
        print(stars)
        value = ws_test_string_1 if mode is None else trim(ws_test_string_1, mode)
        print(pic_x(value, 30))

    print("--" + "    String literal    " + "--")
    print("--" + trim("   String literal    ") + "--")
    print("--" + trim("     String literal   ", "leading") + "--")
    print("--" + trim("   String literal    ", "trailing") + "--")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
