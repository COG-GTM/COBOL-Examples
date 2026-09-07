"""FD ``fd-test-input-file`` / ``01 f-test-record`` (docs/phase0-contracts.md §2)."""

from __future__ import annotations

from dataclasses import dataclass

from .cobol_types import PictureField, from_byte_string, pad_record_area, to_byte_string

F_TEST_STUDENT_ID = PictureField("f-test-student-id", start=1, width=6, numeric=True)
F_TEST_STUDENT_NAME = PictureField("f-test-student-name", start=7, width=20, numeric=False)
F_TEST_MAJOR = PictureField("f-test-major", start=27, width=3, numeric=False)
F_TEST_NUM_COURSES = PictureField("f-test-num-courses", start=30, width=2, numeric=True)

RECORD_FIELDS = (F_TEST_STUDENT_ID, F_TEST_STUDENT_NAME, F_TEST_MAJOR, F_TEST_NUM_COURSES)
RECORD_LENGTH = 31

#: The record area before any READ: COBOL leaves it space filled (ruling D-001).
INITIAL_RECORD_AREA = " " * RECORD_LENGTH


@dataclass(frozen=True)
class TestRecord:
    """One occurrence of ``f-test-record``.

    ``record_area`` holds the record's *bytes*, one character per byte, so field offsets
    match what GnuCOBOL reads even for multibyte input. The raw fixed-width values feed the
    report; the trimmed properties exist only for the JSON views.
    """

    __test__ = False  # not a pytest test class; the name mirrors 01 f-test-record

    record_area: str

    @classmethod
    def from_line(cls, line: str) -> TestRecord:
        return cls(pad_record_area(to_byte_string(line.rstrip("\r\n")), RECORD_LENGTH))

    @property
    def student_id_raw(self) -> str:
        return F_TEST_STUDENT_ID.extract(self.record_area)

    @property
    def student_name_raw(self) -> str:
        """The full X(20) field as stored; this is what the report line prints."""
        return F_TEST_STUDENT_NAME.extract(self.record_area)

    @property
    def major_raw(self) -> str:
        """The full XXX field as stored; this is what the report line prints."""
        return F_TEST_MAJOR.extract(self.record_area)

    @property
    def student_name(self) -> str:
        return from_byte_string(self.student_name_raw).strip()

    @property
    def major(self) -> str:
        return from_byte_string(self.major_raw).strip()

    @property
    def num_courses_raw(self) -> str:
        return F_TEST_NUM_COURSES.extract(self.record_area)

    @property
    def student_id(self) -> int | None:
        raw = self.student_id_raw.replace(" ", "0")
        return int(raw) if raw.isdigit() else None

    @property
    def num_courses(self) -> int | None:
        raw = self.num_courses_raw.replace(" ", "0")
        return int(raw) if raw.isdigit() else None


class RecordReader:
    """``READ fd-test-input-file AT END SET ws-eof TO TRUE``.

    The record area keeps its previous contents at end of file, which is what makes
    ruling D-001 (the duplicated final detail line) observable.
    """

    def __init__(self, text: str) -> None:
        body = text.split("\n")
        if body and body[-1] == "":
            body.pop()
        self._lines = body
        self._index = 0
        self.record = TestRecord(INITIAL_RECORD_AREA)
        self.eof = False

    def read(self) -> None:
        if self._index >= len(self._lines):
            self.eof = True
            return
        self.record = TestRecord.from_line(self._lines[self._index])
        self._index += 1
