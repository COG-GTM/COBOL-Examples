#!/usr/bin/env python3
"""Port of read_command_args/read_specific_cmd_line_args.cbl.

ACCEPT ... FROM ARGUMENT-NUMBER / DISPLAY ... UPON ARGUMENT-NUMBER /
ACCEPT ... FROM ARGUMENT-VALUE map to ``len(sys.argv)`` and ``sys.argv[i]``.
"""
import sys


def main(argv=None) -> int:
    args = sys.argv[1:] if argv is None else argv
    ws_num_args = len(args)
    for ws_counter in range(1, ws_num_args + 1):
        ws_cmd_args = args[ws_counter - 1][:99].ljust(99)  # pic x(99)
        print(ws_cmd_args)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
