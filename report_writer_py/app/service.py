"""REST service replacing the ``report-test`` batch job.

Read-only with respect to the legacy tree: the COBOL source and ``input.txt`` are only
ever read, and runs are held in an in-memory store behind a small adapter so a document
store can be swapped in later.
"""

from __future__ import annotations

import uuid
from collections import OrderedDict
from pathlib import Path
from threading import Lock

from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel, Field

from .record import RecordReader
from .report_writer import (
    DETAIL_LINES_PER_PAGE,
    FIRST_DETAIL,
    LAST_DETAIL,
    PAGE_LIMIT,
    ReportRun,
    run_report,
)

LEGACY_INPUT = Path(__file__).resolve().parents[2] / "report_writer" / "input.txt"
STATIC_DIR = Path(__file__).resolve().parent / "static"
#: Copy of the legacy sample shipped as package data, for installs without the source tree.
BUNDLED_SAMPLE_INPUT = STATIC_DIR / "sample-input.txt"

MAX_INPUT_BYTES = 1_000_000
#: Guard against a small body expanding into a huge report (blank records are still records).
MAX_INPUT_RECORDS = 20_000
#: Runs are kept only so the pager can re-fetch them; oldest are evicted.
MAX_RETAINED_RUNS = 200


class ReportRequest(BaseModel):
    input_text: str = Field(default="", description="Contents of the line sequential input file")


class RecordView(BaseModel):
    index: int
    student_id_raw: str
    student_id: int | None
    student_name: str
    major: str
    num_courses_raw: str
    num_courses: int | None


class PageView(BaseModel):
    number: int
    lines: list[str]


class ReportResponse(BaseModel):
    id: str
    report_text: str
    pages: list[PageView]
    console: list[str]
    detail_count: int
    record_count: int
    page_count: int
    records: list[RecordView]
    geometry: dict[str, int]


class RunStore:
    """Bounded in-memory adapter; the interface is what a document store would implement.

    Retention is capped so a long-lived worker cannot accumulate every report ever run;
    evicted ids answer 404 like any unknown id.
    """

    def __init__(self, max_entries: int = MAX_RETAINED_RUNS) -> None:
        self._runs: OrderedDict[str, ReportResponse] = OrderedDict()
        self._max_entries = max_entries
        self._lock = Lock()

    def put(self, response: ReportResponse) -> None:
        with self._lock:
            self._runs[response.id] = response
            self._runs.move_to_end(response.id)
            while len(self._runs) > self._max_entries:
                self._runs.popitem(last=False)

    def get(self, run_id: str) -> ReportResponse | None:
        with self._lock:
            response = self._runs.get(run_id)
            if response is not None:
                self._runs.move_to_end(run_id)
            return response


store = RunStore()
app = FastAPI(title="Report Writer — migrated from COBOL report-test", version="1.0.0")


def _records_of(input_text: str) -> list[RecordView]:
    reader = RecordReader(input_text)
    views: list[RecordView] = []
    index = 0
    while True:
        reader.read()
        if reader.eof:
            break
        record = reader.record
        views.append(
            RecordView(
                index=index,
                student_id_raw=record.student_id_raw,
                student_id=record.student_id,
                student_name=record.student_name,
                major=record.major,
                num_courses_raw=record.num_courses_raw,
                num_courses=record.num_courses,
            )
        )
        index += 1
    return views


def _to_response(run_id: str, run: ReportRun, input_text: str) -> ReportResponse:
    return ReportResponse(
        id=run_id,
        report_text=run.report_text(),
        pages=[PageView(number=page.number, lines=page.display_lines()) for page in run.pages],
        console=run.console,
        detail_count=run.detail_count,
        record_count=run.record_count,
        page_count=len(run.pages),
        records=_records_of(input_text),
        geometry={
            "page_limit": PAGE_LIMIT,
            "first_detail": FIRST_DETAIL,
            "last_detail": LAST_DETAIL,
            "detail_lines_per_page": DETAIL_LINES_PER_PAGE,
        },
    )


def _record_count(input_text: str) -> int:
    """Records the reader will yield: a trailing newline terminates, it does not add a record."""
    if not input_text:
        return 0
    return input_text.count("\n") + (0 if input_text.endswith("\n") else 1)


@app.post("/api/reports", response_model=ReportResponse)
def create_report(request: ReportRequest) -> ReportResponse:
    if len(request.input_text.encode("utf-8")) > MAX_INPUT_BYTES:
        raise HTTPException(status_code=413, detail="Input file too large")
    if _record_count(request.input_text) > MAX_INPUT_RECORDS:
        raise HTTPException(status_code=413, detail=f"Input file has more than {MAX_INPUT_RECORDS} records")
    run = run_report(request.input_text)
    response = _to_response(uuid.uuid4().hex, run, request.input_text)
    store.put(response)
    return response


@app.get("/api/reports/{run_id}", response_model=ReportResponse)
def get_report(run_id: str) -> ReportResponse:
    response = store.get(run_id)
    if response is None:
        raise HTTPException(status_code=404, detail="Report run not found")
    return response


@app.get("/api/reports/{run_id}/pages/{page_number}", response_model=PageView)
def get_report_page(run_id: str, page_number: int) -> PageView:
    response = store.get(run_id)
    if response is None:
        raise HTTPException(status_code=404, detail="Report run not found")
    for page in response.pages:
        if page.number == page_number:
            return page
    raise HTTPException(status_code=404, detail="Page not found")


@app.get("/api/sample-input")
def sample_input() -> dict[str, str]:
    """The repository's own ``report_writer/input.txt``, read only.

    Served from the legacy tree in a source checkout and from the packaged copy otherwise,
    since an installed wheel has no ``report_writer/`` above it.
    """
    source = LEGACY_INPUT if LEGACY_INPUT.exists() else BUNDLED_SAMPLE_INPUT
    if not source.exists():
        raise HTTPException(status_code=404, detail="Sample input is not available")
    return {"input_text": source.read_text(encoding="utf-8")}


@app.get("/")
def index() -> FileResponse:
    return FileResponse(STATIC_DIR / "index.html")


app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")
