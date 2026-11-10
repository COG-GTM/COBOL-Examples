#!/usr/bin/env python3
"""Port of xml_generate/xml_generate.cbl: XML GENERATE via ElementTree.

Mirrors the COBOL options: WITH XML-DECLARATION, NAME OF overrides,
TYPE OF ws-record-flag IS ATTRIBUTE and SUPPRESS WHEN SPACES.
"""
import xml.etree.ElementTree as ET
from dataclasses import dataclass

XML_DECLARATION = '<?xml version="1.0"?>\n'


@dataclass
class Record:
    name: str = ""  # pic x(10)
    value: str = ""  # pic x(10)
    blank: str = ""  # pic x(10)
    flag: str = "false"  # pic x(5)

    def display(self) -> str:
        return f"{self.name:<10.10}{self.value:<10.10}{self.blank:<10.10}{self.flag:<5.5}"


def xml_generate(record: Record) -> str:
    root = ET.Element("ws-record", {"enabled": record.flag.rstrip(" ")})
    for tag, text in (("name", record.name), ("value", record.value), ("ws-record-blank", record.blank)):
        if text.strip(" "):  # SUPPRESS WHEN SPACES
            ET.SubElement(root, tag).text = text.rstrip(" ")
    return XML_DECLARATION + ET.tostring(root, encoding="unicode")


def main() -> int:
    record = Record(name="Test Name", value="Test Value")
    record.flag = "true"

    try:
        output = xml_generate(record)
    except (TypeError, ValueError) as exc:
        print(f"Error generating xml error {exc}")
        return 1
    print("XML document successfully generated.")

    print("Generated xml for record: " + record.display())
    print("----------------------------")
    print(output.strip())
    print("----------------------------")
    print(f"XML output character count: {len(output):04d}")
    print("Done.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
