"""Port of sub_program/sub.cbl: the called sub-program.

COBOL storage sections map to Python like this:

* WORKING-STORAGE -> module globals: they persist between calls until the
  module is re-initialised (``importlib.reload`` plays the role of CANCEL).
* LOCAL-STORAGE   -> local variables: fresh on every call.
* LINKAGE SECTION -> parameters. ``PicX`` is a mutable holder, so passing the
  caller's object is BY REFERENCE and passing a copy is BY CONTENT.
"""
from dataclasses import dataclass


@dataclass
class PicX:
    size: int
    value: str = ""

    def __post_init__(self):
        self.set(self.value)

    def set(self, value: str) -> None:
        self.value = value[:self.size].ljust(self.size)

    def __str__(self) -> str:
        return self.value


# WORKING-STORAGE SECTION
ws_test_item_1 = PicX(10)
ws_test_item_2 = PicX(10)


def sub_app(l_test_item_1: PicX, l_test_item_2: PicX) -> None:
    # LOCAL-STORAGE SECTION
    ls_test_item_1 = PicX(10)
    ls_test_item_2 = PicX(10)

    print(f"In sub program: {l_test_item_1} {l_test_item_2}")
    print(" ")
    print("working-storage values at start:")
    print(f"ws-test-item-1: {ws_test_item_1}")
    print(f"ws-test-item-2: {ws_test_item_2}")
    print(" ")
    print("local-storage values at start:")
    print(f"ls-test-item-1: {ls_test_item_1}")
    print(f"ls-test-item-2: {ls_test_item_2}")
    print(" ")
    print("Moving linkage section values to ws and ls vars..")

    ws_test_item_1.set(l_test_item_1.value)
    ws_test_item_2.set(l_test_item_2.value)
    ls_test_item_1.set(l_test_item_1.value)
    ls_test_item_2.set(l_test_item_2.value)

    print("setting input variables to new value...")
    l_test_item_1.set("replace1")
    l_test_item_2.set("replace2")

    print(" ")
    print("working-storage values at end:")
    print(f"ws-test-item-1: {ws_test_item_1}")
    print(f"ws-test-item-2: {ws_test_item_2}")
    print(" ")
    print("local-storage values at end:")
    print(f"ls-test-item-1: {ls_test_item_1}")
    print(f"ls-test-item-2: {ls_test_item_2}")
    print(" ")
    print(f"Exit sub program: {l_test_item_1} {l_test_item_2}")


if __name__ == "__main__":
    print("sub_app.py is a sub-program; run main_app.py instead.")
