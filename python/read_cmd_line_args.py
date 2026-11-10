#!/usr/bin/env python3
"""Port of read_command_args/read_cmd_line_args.cbl: ACCEPT ... FROM COMMAND-LINE."""
import sys


def main(argv=None) -> int:
    args = sys.argv[1:] if argv is None else argv
    ws_cmd_args = " ".join(args)[:256].ljust(256)  # pic x(256)

    print(" ")
    print("Pass arg '--test' for special message")
    print("Full command line args: " + ws_cmd_args)

    # INSPECT FUNCTION LOWER-CASE(...) TALLYING ... FOR ALL "--test"
    if ws_cmd_args.lower().count("--test") > 0:
        print("You entered the '--test' cmd arg!")

    print(" ")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
