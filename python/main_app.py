#!/usr/bin/env python3
"""Port of sub_program/main_app.cbl: CALL ... BY CONTENT / BY REFERENCE / CANCEL.

The called program lives in the separate module ``sub_app.py``.
"""
import copy
import importlib

import sub_app
from sub_app import PicX


def read_line(prompt: str) -> str:
    try:
        return input(prompt)
    except EOFError:
        return ""


def display_message(item_1: PicX, item_2: PicX) -> None:
    print(" ")
    print("-----------------------------------------------")
    print(f"Main app: {item_1}{item_2}")


def main() -> int:
    sub = sub_app
    ws_item_1, ws_item_2 = PicX(10), PicX(10)

    print(" ")
    ws_item_1.set(read_line("Enter value for #1: "))
    ws_item_2.set(read_line("Enter value for #2: "))
    display_message(ws_item_1, ws_item_2)

    # BY CONTENT: the sub-program gets copies, so our values are unchanged.
    print("Calling sub program by content:")
    sub.sub_app(copy.copy(ws_item_1), copy.copy(ws_item_2))
    display_message(ws_item_1, ws_item_2)

    # BY REFERENCE: the sub-program can modify our values; its
    # WORKING-STORAGE (module globals) kept the values from the last call.
    print("Second call of sub program should retain WS values.")
    print("Calling sub program by reference:")
    sub.sub_app(ws_item_1, ws_item_2)
    display_message(ws_item_1, ws_item_2)

    # CANCEL: re-initialise the module so WORKING-STORAGE is reset.
    print("Cancelling sub program")
    sub = importlib.reload(sub)
    print("Calling sub program. WS values should be reset:")
    sub.sub_app(ws_item_1, ws_item_2)
    display_message(ws_item_1, ws_item_2)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
