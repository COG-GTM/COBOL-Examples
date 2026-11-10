#!/usr/bin/env python3
"""Port of redifines/redefines.cbl: REDEFINES as views over a byte buffer.

Each record is a ``bytearray`` laid out exactly like the COBOL record.
Fields are ``memoryview`` slices, so ``corp_name`` and
``first_name``/``last_name`` share the same 30 bytes, and a COMP-2 (double)
shares storage with a PIC X(10) via ``struct``.
"""
import struct
import sys


class Field:
    """A PIC X / PIC 9 view onto a slice of the record buffer."""

    def __init__(self, offset: int, size: int, numeric: bool = False):
        self.offset, self.size, self.numeric = offset, size, numeric

    def __set_name__(self, owner, name):
        self.name = name

    def __get__(self, record, owner=None):
        raw = bytes(record.buffer[self.offset:self.offset + self.size])
        return raw.decode("latin-1")

    def __set__(self, record, value):
        if self.numeric:
            text = str(int(value) % 10**self.size).zfill(self.size)
        else:
            text = str(value)[:self.size].ljust(self.size)
        record.buffer[self.offset:self.offset + self.size] = text.encode("latin-1")


class CustomerRecord:
    """01 ws-customer (58 bytes).

    offset 0  pic 9       customer-type (88 person=1, corp=2)
    offset 1  pic x(10)   first-name  \\  customer-name
    offset 11 pic x(20)   last-name   /
    offset 1  pic x(30)   corp-name   REDEFINES customer-name
    offset 31 pic x(20)   street-address
    offset 51 pic xx      state
    offset 53 pic 9(5)    zip-code
    """

    SIZE = 58
    customer_type = Field(0, 1, numeric=True)
    first_name = Field(1, 10)
    last_name = Field(11, 20)
    corp_name = Field(1, 30)
    street_address = Field(31, 20)
    state = Field(51, 2)
    zip_code = Field(53, 5, numeric=True)

    def __init__(self):
        self.buffer = bytearray(b" " * self.SIZE)

    @property
    def is_person(self) -> bool:
        return self.customer_type == "1"


class DiffDataTypes:
    """01 ws-diff-data-types: PIC A type flag + PIC X(10) REDEFINED as COMP-2."""

    SIZE = 11
    data_type = Field(0, 1)
    data_disp_value = Field(1, 10)

    def __init__(self):
        self.buffer = bytearray(b" " * self.SIZE)

    @property
    def data_comp_value(self) -> float:
        return struct.unpack_from("=d", self.buffer, 1)[0]

    @data_comp_value.setter
    def data_comp_value(self, value: float) -> None:
        struct.pack_into("=d", self.buffer, 1, value)


def format_comp2(value: float) -> str:
    text = repr(value)
    text = text.removesuffix(".0")
    return text.replace("e", "E")


def out(text: str = "") -> None:
    """Write raw bytes so redefined binary data shows exactly as COBOL shows it."""
    sys.stdout.flush()
    sys.stdout.buffer.write(text.encode("latin-1") + b"\n")
    sys.stdout.buffer.flush()


def setup_test_data():
    customers = [CustomerRecord() for _ in range(3)]
    out(" ")
    out("1. Person record with first/last name entered.")
    c = customers[0]
    c.customer_type = 1
    c.first_name, c.last_name = "test-first", "test-last"
    c.street_address, c.state, c.zip_code = "123 fake st", "NV", 12345

    out("2. Corp record with corp name entered.")
    c = customers[1]
    c.customer_type = 2
    c.corp_name = "no-name corp"
    c.street_address, c.state, c.zip_code = "567 real st", "NY", 11795

    out("3. Person record with corp name entered.")
    c = customers[2]
    c.customer_type = 1
    c.corp_name = "SET CORP VALUE"  # spills from first-name into last-name
    c.street_address, c.state, c.zip_code = "890 what st", "MA", 9345
    return customers


def display_customer_data(customers) -> None:
    out(" ")
    out("Displaying fake customer data:")
    out("------------------------------")
    out(" ")
    for c in customers:
        if c.is_person:
            out("Customer Type: PERSON")
            out("First Name: " + c.first_name)
            out("Last Name: " + c.last_name)
        else:
            out("Customer Type: CORP")
            out("Company name: " + c.corp_name)
        out("Address: ")
        out(c.street_address)
        out(c.state + ", " + c.zip_code)
        out("------------------------------")
        out(" ")


def setup_second_test_data():
    data = [DiffDataTypes(), DiffDataTypes()]
    data[0].data_type = "D"
    data[0].data_disp_value = "ABC123"
    data[1].data_type = "C"
    data[1].data_comp_value = 12345.63
    return data


def display_second_test_data(data) -> None:
    out(" ")
    out("Redefines with different variable types:")
    out("----------------------------------------")
    out("Value entered in ws-data-disp-value: ABC123")
    out("ws-data-disp-value x(10): " + data[0].data_disp_value)
    out("ws-data-comp-value comp-2: " + format_comp2(data[0].data_comp_value))
    out(" ")
    out("----------------------------------------")
    out("Value entered in ws-data-comp-value: 12345.63")
    out("ws-data-disp-value x(10): " + data[1].data_disp_value)
    out("ws-data-comp-value comp-2: " + format_comp2(data[1].data_comp_value))
    out(" ")


def main() -> int:
    customers = setup_test_data()
    display_customer_data(customers)
    display_second_test_data(setup_second_test_data())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
