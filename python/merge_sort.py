#!/usr/bin/env python3
"""Port of merge_sort/merge_sort_test.cbl.

Creates two fixed-width customer files, MERGEs them on ascending customer
id, then SORTs the merged file on descending contract id and displays each
step, just like the COBOL MERGE/SORT verbs.
"""
import argparse
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class CustomerRecord:
    customer_id: int  # pic 9(5)
    last_name: str  # pic x(50)
    first_name: str  # pic x(50)
    contract_id: int  # pic 9(5)
    comment: str  # pic x(25)

    LAYOUT = ((0, 5), (5, 55), (55, 105), (105, 110), (110, 135))

    def to_line(self) -> str:
        return (
            f"{self.customer_id:05d}"
            f"{self.last_name[:50]:<50}"
            f"{self.first_name[:50]:<50}"
            f"{self.contract_id:05d}"
            f"{self.comment[:25]:<25}"
        )

    @classmethod
    def from_line(cls, line: str) -> "CustomerRecord":
        line = line.rstrip("\n").ljust(135)
        cid, last, first, contract, comment = (line[a:b] for a, b in cls.LAYOUT)
        return cls(int(cid), last.rstrip(), first.rstrip(), int(contract), comment.rstrip())


EAST_RECORDS = [
    CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
    CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
    CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
    CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
    CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
    CustomerRecord(75, "last-75", "first-75", 1175, "comment-75"),
]

WEST_RECORDS = [
    CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
    CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
    CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
    CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
    CustomerRecord(24, "last-24", "first-24", 247, "comment-24"),
]


def write_file(path: Path, records) -> None:
    path.write_text("".join(r.to_line() + "\n" for r in records))


def read_file(path: Path) -> list:
    with path.open() as f:
        return [CustomerRecord.from_line(line) for line in f]


def display_file(path: Path) -> None:
    for record in read_file(path):
        print(record.to_line())


def create_test_data(work_dir: Path) -> None:
    print("Creating test data files...")
    write_file(work_dir / "test-file-1.txt", EAST_RECORDS)
    write_file(work_dir / "test-file-2.txt", WEST_RECORDS)


def merge_and_display_files(work_dir: Path) -> None:
    print("Merging and sorting files...")
    # GnuCOBOL's MERGE orders every record by key even though the input
    # files are not pre-sorted, so a stable sort over all inputs is used
    # instead of heapq.merge (which requires pre-sorted inputs).
    records = read_file(work_dir / "test-file-1.txt") + read_file(work_dir / "test-file-2.txt")
    merged = sorted(records, key=lambda r: r.customer_id)
    write_file(work_dir / "merge-output.txt", merged)
    display_file(work_dir / "merge-output.txt")


def sort_and_display_file(work_dir: Path) -> None:
    print("Sorting merged file on descending contract id....")
    records = read_file(work_dir / "merge-output.txt")
    ordered = sorted(records, key=lambda r: r.contract_id, reverse=True)
    write_file(work_dir / "sorted-contract-id.txt", ordered)
    display_file(work_dir / "sorted-contract-id.txt")


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument(
        "--work-dir",
        type=Path,
        help="directory for the generated .txt files (default: a temporary directory)",
    )
    args = parser.parse_args(argv)

    if args.work_dir:
        args.work_dir.mkdir(parents=True, exist_ok=True)
        run(args.work_dir)
    else:
        with tempfile.TemporaryDirectory() as tmp:
            run(Path(tmp))
    return 0


def run(work_dir: Path) -> None:
    create_test_data(work_dir)
    merge_and_display_files(work_dir)
    sort_and_display_file(work_dir)
    print("Done.")


if __name__ == "__main__":
    raise SystemExit(main())
