"""FastAPI service exposing ``unstring-example`` as a read-only parsing API.

The COBOL program is a console batch program with no input: every endpoint here is a
pure function of its query/body parameters, nothing is written to disk, and nothing
under ``unstring/`` is touched. Runs are kept in a bounded in-memory store behind a
small adapter so a database can be dropped in later without touching the routes.
"""

from __future__ import annotations

from collections import OrderedDict
from decimal import Decimal, InvalidOperation
from pathlib import Path
from typing import Any
from uuid import uuid4

from fastapi import FastAPI, HTTPException, Query
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel, Field

from app.cobol_types import DEST_LEN, MULTI_OCCURS, SOURCE_LEN, from_byte_string
from app.program import (
    DEFAULT_DELIMITER,
    DEFAULT_MULTI_DELIM_SOURCE,
    DEFAULT_MULTI_DEST_SOURCE,
    DEFAULT_SIMPLE_SOURCE,
    DEFAULT_SOURCE_NUM,
    ExampleView,
    Program,
    run_program,
)

STATIC_DIR = Path(__file__).resolve().parent / "static"

#: Inputs are capped well above the PIC X(30) the program can hold, so an oversized
#: request is rejected rather than silently truncated into a large useless run.
MAX_INPUT_LEN = 256
#: Bound on the run store, evicted least-recently-used.
MAX_RUNS = 200
EXAMPLE_COUNT = 6


class RunRequest(BaseModel):
    """Working-storage values to run the program with."""

    simple_source: str = Field(default=DEFAULT_SIMPLE_SOURCE, max_length=MAX_INPUT_LEN)
    multi_delim_source: str = Field(default=DEFAULT_MULTI_DELIM_SOURCE, max_length=MAX_INPUT_LEN)
    multi_dest_source: str = Field(default=DEFAULT_MULTI_DEST_SOURCE, max_length=MAX_INPUT_LEN)
    delimiter: str = Field(default=DEFAULT_DELIMITER, min_length=1, max_length=1)
    #: ``PIC $999,999.99`` is money: carried as a JSON **string**, never a float.
    amount: str = Field(default=str(DEFAULT_SOURCE_NUM), max_length=32)


class RunStore:
    """LRU adapter over the runs; swap for a document store without touching routes."""

    def __init__(self, capacity: int = MAX_RUNS) -> None:
        self._capacity = capacity
        self._runs: OrderedDict[str, dict[str, Any]] = OrderedDict()

    def put(self, payload: dict[str, Any]) -> str:
        run_id = uuid4().hex
        self._runs[run_id] = payload
        self._runs.move_to_end(run_id)
        while len(self._runs) > self._capacity:
            self._runs.popitem(last=False)
        return run_id

    def get(self, run_id: str) -> dict[str, Any] | None:
        payload = self._runs.get(run_id)
        if payload is not None:
            self._runs.move_to_end(run_id)
        return payload

    def __len__(self) -> int:
        return len(self._runs)


store = RunStore()
app = FastAPI(
    title="unstring-example (migrated)",
    description="Cloud-native migration of the GnuCOBOL unstring-example program",
    version="1.0.0",
)


def _amount(value: str) -> Decimal:
    try:
        parsed = Decimal(value)
    except InvalidOperation as exc:
        raise HTTPException(status_code=422, detail=f"amount is not numeric: {value!r}") from exc
    if not parsed.is_finite():
        raise HTTPException(status_code=422, detail="amount must be finite")
    return parsed


def _view_payload(view: ExampleView) -> dict[str, Any]:
    return {
        "number": view.number,
        "title": view.title,
        "source": view.source,
        "lines": [from_byte_string(line) for line in view.lines],
        "stats": view.stats,
        "overflow": view.overflow,
    }


def _program_payload(program: Program, request: RunRequest) -> dict[str, Any]:
    return {
        "request": request.model_dump(),
        "console": [from_byte_string(line) for line in program.console],
        "stdout": from_byte_string(program.stdout_text()),
        "examples": [_view_payload(view) for view in program.examples],
        "working_storage": {
            "source_str": from_byte_string(program.ws.source_str),
            "part_1": from_byte_string(program.ws.part_1),
            "part_2": from_byte_string(program.ws.part_2),
            "delimiter": program.ws.delimiter,
            "pointer": program.ws.pointer,
            "single_fields_filled": program.ws.single_fields_filled,
            "multi_fields_filled": program.ws.multi_fields_filled,
            # Money crosses the wire as a string.
            "source_num": str(program.ws.source_num),
        },
        "limits": {
            "source_width": SOURCE_LEN,
            "dest_width": DEST_LEN,
            "table_occurs": MULTI_OCCURS,
        },
    }


def _run(request: RunRequest) -> Program:
    return run_program(
        simple_source=request.simple_source,
        multi_delim_source=request.multi_delim_source,
        multi_dest_source=request.multi_dest_source,
        delimiter=request.delimiter,
        source_num=_amount(request.amount),
    )


@app.get("/api/health")
def health() -> dict[str, object]:
    return {"status": "ok", "program": "unstring-example", "runs": len(store)}


@app.get("/api/defaults")
def defaults() -> dict[str, object]:
    """The literals the legacy program hard-codes, for pre-filling the UI."""
    return {
        "simple_source": DEFAULT_SIMPLE_SOURCE,
        "multi_delim_source": DEFAULT_MULTI_DELIM_SOURCE,
        "multi_dest_source": DEFAULT_MULTI_DEST_SOURCE,
        "delimiter": DEFAULT_DELIMITER,
        "amount": str(DEFAULT_SOURCE_NUM),
        "source_width": SOURCE_LEN,
        "dest_width": DEST_LEN,
        "table_occurs": MULTI_OCCURS,
    }


def _query_request(
    simple_source: str,
    multi_delim_source: str,
    multi_dest_source: str,
    delimiter: str,
    amount: str,
) -> RunRequest:
    return RunRequest(
        simple_source=simple_source,
        multi_delim_source=multi_delim_source,
        multi_dest_source=multi_dest_source,
        delimiter=delimiter,
        amount=amount,
    )


@app.get("/api/program")
def get_program(
    simple_source: str = Query(DEFAULT_SIMPLE_SOURCE, max_length=MAX_INPUT_LEN),
    multi_delim_source: str = Query(DEFAULT_MULTI_DELIM_SOURCE, max_length=MAX_INPUT_LEN),
    multi_dest_source: str = Query(DEFAULT_MULTI_DEST_SOURCE, max_length=MAX_INPUT_LEN),
    delimiter: str = Query(DEFAULT_DELIMITER, min_length=1, max_length=1),
    amount: str = Query(str(DEFAULT_SOURCE_NUM), max_length=32),
) -> dict[str, Any]:
    """Run all six examples and return the full console transcript plus statistics."""
    request = _query_request(simple_source, multi_delim_source, multi_dest_source, delimiter, amount)
    payload = _program_payload(_run(request), request)
    payload["run_id"] = store.put(payload)
    return payload


@app.get("/api/examples/{number}")
def get_example(
    number: int,
    simple_source: str = Query(DEFAULT_SIMPLE_SOURCE, max_length=MAX_INPUT_LEN),
    multi_delim_source: str = Query(DEFAULT_MULTI_DELIM_SOURCE, max_length=MAX_INPUT_LEN),
    multi_dest_source: str = Query(DEFAULT_MULTI_DEST_SOURCE, max_length=MAX_INPUT_LEN),
    delimiter: str = Query(DEFAULT_DELIMITER, min_length=1, max_length=1),
    amount: str = Query(str(DEFAULT_SOURCE_NUM), max_length=32),
) -> dict[str, Any]:
    """One endpoint per ``EXAMPLE n`` block of ``main-procedure``.

    The examples share one working storage in the COBOL, so the whole program is run
    and the requested example's slice of the transcript is returned.
    """
    if not 1 <= number <= EXAMPLE_COUNT:
        raise HTTPException(status_code=404, detail=f"no such example: {number}")
    request = _query_request(simple_source, multi_delim_source, multi_dest_source, delimiter, amount)
    program = _run(request)
    return _view_payload(program.examples[number - 1])


@app.post("/api/runs")
def create_run(request: RunRequest) -> dict[str, Any]:
    """Run the program and keep the result addressable for the UI."""
    payload = _program_payload(_run(request), request)
    payload["run_id"] = store.put(payload)
    return payload


@app.get("/api/runs/{run_id}")
def get_run(run_id: str) -> dict[str, Any]:
    payload = store.get(run_id)
    if payload is None:
        raise HTTPException(status_code=404, detail="run not found")
    return payload


@app.get("/")
def index() -> FileResponse:
    return FileResponse(STATIC_DIR / "index.html")


app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")
