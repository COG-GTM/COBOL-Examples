#!/usr/bin/env python3
"""Port of json_generate/json_generate.cbl: JSON GENERATE via json.dumps.

GnuCOBOL's JSON GENERATE trims trailing spaces from each PIC X field
(an all-space field becomes a single space) and uses the NAME OF overrides.
"""
import json
from dataclasses import dataclass


@dataclass
class Record:
    name: str = ""  # pic x(10)
    value: str = ""  # pic x(10)
    blank: str = ""  # pic x(10)
    flag: str = "false"  # pic x(5), 88 enabled "true" / disabled "false"

    def display(self) -> str:
        return f"{self.name:<10.10}{self.value:<10.10}{self.blank:<10.10}{self.flag:<5.5}"


def json_value(text: str) -> str:
    return text.rstrip(" ") or " "


def json_generate(record: Record) -> str:
    document = {
        "ws-record": {
            "name": json_value(record.name),
            "value": json_value(record.value),
            "ws-record-blank": json_value(record.blank),
            "enabled": json_value(record.flag),
        }
    }
    return json.dumps(document, separators=(",", ":"))


def main() -> int:
    record = Record(name="Test Name", value="Test Value")
    record.flag = "true"

    try:
        output = json_generate(record)
    except (TypeError, ValueError) as exc:
        print(f"Error generating JSON error {exc}")
        return 1
    print("JSON document successfully generated.")

    print("Generated JSON for record: " + record.display())
    print("----------------------------")
    print(output.strip())
    print("----------------------------")
    print(f"JSON output character count: {len(output):04d}")
    print("Done.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
