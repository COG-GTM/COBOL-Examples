"""FastAPI service exposing ``merge-sort-example``.

Read path: one GET endpoint per file the COBOL program writes, each paged.
Write path: one POST that runs the merge/sort over supplied inputs entirely in memory.
"""

from __future__ import annotations

import math
from pathlib import Path
from typing import Annotated, Any, Final, Literal

from fastapi import Body, FastAPI, HTTPException, Query, Request, Response
from fastapi import Path as PathParam
from fastapi.responses import FileResponse, JSONResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel, Field
from starlette.middleware.base import RequestResponseEndpoint

from app.cobol_types import from_byte_string, to_byte_string, to_bytes
from app.merge_sort import (
    MSG_OPEN_MERGED_FAILED,
    MSG_OPEN_OUTPUT_FAILED,
    MSG_OPEN_SORTED_FAILED,
    STATUS_OK,
    MergeSortRun,
    RecordLimitExceeded,
    run_program,
    run_with_inputs,
)
from app.record import CustomerRecord
from app.store import InMemoryRunStore, RunStore

STATIC_DIR = Path(__file__).resolve().parent / "static"

DEFAULT_PAGE_SIZE: Final = 25
MAX_PAGE_SIZE: Final = 500
# One 135 byte record per line; 20 000 records plus newlines fits comfortably under this.
MAX_INPUT_BYTES: Final = 4 * 1024 * 1024

MERGE_OUTPUT: Final = "merge-output.txt"
SORTED_CONTRACT_ID: Final = "sorted-contract-id.txt"
TEST_FILE_1: Final = "test-file-1.txt"
TEST_FILE_2: Final = "test-file-2.txt"

app = FastAPI(
    title="merge-sort-example",
    description="Cloud-native port of the GnuCOBOL batch program merge_sort/merge_sort_test.cbl.",
    version="1.0.0",
)

store: RunStore = InMemoryRunStore()

# Content type for the byte-exact endpoints: the bodies are the program's own bytes.
RAW_MEDIA_TYPE: Final = "text/plain; charset=utf-8"


@app.middleware("http")
async def limit_body_size(request: Request, call_next: RequestResponseEndpoint) -> Response:
    """Reject an oversized body before anything parses or buffers it.

    ``MAX_INPUT_BYTES`` is enforced twice on purpose: here against the whole HTTP body
    (JSON syntax and escaping included), and again in :func:`create_run` against the decoded
    file content, which is what the record cap is expressed in.
    """
    declared = request.headers.get("content-length")
    if declared is not None and declared.isdigit() and int(declared) > MAX_INPUT_BYTES:
        return JSONResponse(
            status_code=413,
            content={"detail": f"Request body exceeds {MAX_INPUT_BYTES} bytes."},
        )
    return await call_next(request)


class RunRequest(BaseModel):
    """Two input files, pasted or uploaded, as text.

    ``None`` for both means "use the program's own ``create-test-data`` rows".
    """

    test_file_1: str | None = Field(default=None, max_length=MAX_INPUT_BYTES)
    test_file_2: str | None = Field(default=None, max_length=MAX_INPUT_BYTES)
    generate: bool = Field(
        default=False,
        description="Run create-test-data instead of using the supplied files.",
    )


class RecordView(BaseModel):
    customer_id: str
    last_name: str
    first_name: str
    contract_id: str
    comment: str
    raw: str


class FileStatusView(BaseModel):
    file: str
    file_status: str
    message: str


class RunSummary(BaseModel):
    run_id: str
    generated: bool
    record_count: int
    merged_count: int
    sorted_count: int
    console: list[str]
    file_statuses: list[FileStatusView]
    stopped_early: bool


class PageView(BaseModel):
    run_id: str
    file: str
    record_count: int
    page: int
    page_size: int
    page_count: int
    more_data: bool
    file_status: str
    message: str
    records: list[RecordView]
    raw: str


def _status_message(status: str, failure_message: str) -> str:
    """The COBOL's own wording: it displays a message only when the status is not "00"."""
    return "" if status == STATUS_OK else failure_message + status


def _file_statuses(run: MergeSortRun) -> list[FileStatusView]:
    return [
        FileStatusView(
            file=TEST_FILE_1,
            file_status=run.file_status_1,
            message=_status_message(run.file_status_1, MSG_OPEN_OUTPUT_FAILED),
        ),
        FileStatusView(
            file=TEST_FILE_2,
            file_status=run.file_status_2,
            message=_status_message(run.file_status_2, MSG_OPEN_OUTPUT_FAILED),
        ),
        FileStatusView(
            file=MERGE_OUTPUT,
            file_status=run.file_status_merge,
            message=_status_message(run.file_status_merge, MSG_OPEN_MERGED_FAILED),
        ),
        FileStatusView(
            file=SORTED_CONTRACT_ID,
            file_status=run.file_status_sorted,
            message=_status_message(run.file_status_sorted, MSG_OPEN_SORTED_FAILED),
        ),
    ]


def _summary(run_id: str, run: MergeSortRun, *, generated: bool | None = None) -> RunSummary:
    generated = run.generated if generated is None else generated
    return RunSummary(
        run_id=run_id,
        generated=generated,
        record_count=len(run.merged_records),
        merged_count=len(run.merged_records),
        sorted_count=len(run.sorted_records),
        console=[from_byte_string(line) for line in run.console],
        file_statuses=_file_statuses(run),
        stopped_early=run.stopped_early,
    )


def _record_view(record: CustomerRecord) -> RecordView:
    payload: dict[str, Any] = record.as_json()
    return RecordView(**payload)


def _load(run_id: str) -> MergeSortRun:
    run = store.get(run_id)
    if run is None:
        raise HTTPException(status_code=404, detail=f"No run {run_id}; it may have been evicted.")
    return run


def _page(
    run_id: str,
    run: MergeSortRun,
    file_name: str,
    records: list[CustomerRecord],
    status: str,
    failure_message: str,
    page: int,
    page_size: int,
) -> PageView:
    record_count = len(records)
    page_count = max(1, math.ceil(record_count / page_size))
    if page > page_count:
        raise HTTPException(
            status_code=404,
            detail=f"Page {page} is past the last page ({page_count}).",
        )
    start = (page - 1) * page_size
    window = records[start : start + page_size]
    return PageView(
        run_id=run_id,
        file=file_name,
        record_count=record_count,
        page=page,
        page_size=page_size,
        page_count=page_count,
        more_data=start + len(window) < record_count,
        file_status=status,
        message=_status_message(status, failure_message),
        records=[_record_view(record) for record in window],
        # file_line() is one Python character per COBOL byte; decode before it is serialised,
        # or the JSON encoder would UTF-8 encode an already-encoded byte string.
        raw=from_byte_string("".join(record.file_line() + "\n" for record in window)),
    )


@app.get("/api/health")
def health() -> dict[str, object]:
    return {"status": "ok", "program": "merge-sort-example", "runs": len(store)}


@app.post("/api/runs", response_model=RunSummary, status_code=201)
def create_run(request: Annotated[RunRequest, Body()]) -> RunSummary:
    """Write path: run the program in memory. Nothing is written to the legacy tree."""
    generated = request.generate or (request.test_file_1 is None and request.test_file_2 is None)

    if generated:
        run = run_program()
    else:
        file_1 = to_byte_string(request.test_file_1 or "")
        file_2 = to_byte_string(request.test_file_2 or "")
        total = len(to_bytes(file_1)) + len(to_bytes(file_2))
        if total > MAX_INPUT_BYTES:
            raise HTTPException(
                status_code=413,
                detail=f"Input exceeds {MAX_INPUT_BYTES} bytes.",
            )
        try:
            run = run_with_inputs(file_1, file_2)
        except RecordLimitExceeded as exc:
            raise HTTPException(status_code=413, detail=str(exc)) from exc

    if run.stopped_early:
        # The program stopped on a non-"00" open; surface its own message, not a generic error.
        raise HTTPException(status_code=502, detail=run.console[-1])

    run_id = store.put(run)
    return _summary(run_id, run, generated=generated)


@app.get("/api/runs/{run_id}", response_model=RunSummary)
def get_run(run_id: Annotated[str, PathParam()]) -> RunSummary:
    run = _load(run_id)
    return _summary(run_id, run, generated=run.generated)


@app.get("/api/runs/{run_id}/merge-output", response_model=PageView)
def get_merge_output(
    run_id: Annotated[str, PathParam()],
    page: Annotated[int, Query(ge=1)] = 1,
    page_size: Annotated[int, Query(ge=1, le=MAX_PAGE_SIZE)] = DEFAULT_PAGE_SIZE,
) -> PageView:
    """``merge-output.txt``: the two inputs merged on ASCENDING f-customer-id."""
    run = _load(run_id)
    return _page(
        run_id,
        run,
        MERGE_OUTPUT,
        run.merged_records,
        run.file_status_merge,
        MSG_OPEN_MERGED_FAILED,
        page,
        page_size,
    )


@app.get("/api/runs/{run_id}/sorted-contract-id", response_model=PageView)
def get_sorted_contract_id(
    run_id: Annotated[str, PathParam()],
    page: Annotated[int, Query(ge=1)] = 1,
    page_size: Annotated[int, Query(ge=1, le=MAX_PAGE_SIZE)] = DEFAULT_PAGE_SIZE,
) -> PageView:
    """``sorted-contract-id.txt``: the merged file on DESCENDING f-customer-contract-id."""
    run = _load(run_id)
    return _page(
        run_id,
        run,
        SORTED_CONTRACT_ID,
        run.sorted_records,
        run.file_status_sorted,
        MSG_OPEN_SORTED_FAILED,
        page,
        page_size,
    )


@app.get("/api/runs/{run_id}/console", response_class=Response)
def get_console(run_id: Annotated[str, PathParam()]) -> Response:
    """The program's DISPLAY output, byte for byte."""
    return Response(
        content=to_bytes(_load(run_id).console_text()),
        media_type=RAW_MEDIA_TYPE,
    )


@app.get("/api/runs/{run_id}/files/{file_name}", response_class=Response)
def get_file(
    run_id: Annotated[str, PathParam()],
    file_name: Annotated[
        Literal["test-file-1.txt", "test-file-2.txt", "merge-output.txt", "sorted-contract-id.txt"],
        PathParam(),
    ],
) -> Response:
    """The raw line-sequential file the program wrote, byte for byte."""
    run = _load(run_id)
    body = {
        TEST_FILE_1: run.test_file_1,
        TEST_FILE_2: run.test_file_2,
        MERGE_OUTPUT: run.merge_output,
        SORTED_CONTRACT_ID: run.sorted_contract_id,
    }[file_name]
    return Response(content=to_bytes(body), media_type=RAW_MEDIA_TYPE)


@app.get("/favicon.ico", include_in_schema=False)
def favicon() -> FileResponse:
    return FileResponse(STATIC_DIR / "favicon.svg", media_type="image/svg+xml")


@app.get("/", include_in_schema=False)
def index() -> FileResponse:
    return FileResponse(STATIC_DIR / "index.html")


app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")
