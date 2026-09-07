"""FastAPI read path: one GET endpoint per search ``search-example`` performs.

Every endpoint is read-only; the program has no write path, so neither does the
service.  Query values are the *raw* ``ACCEPT`` lines and are parsed with the
COBOL rules, so ``id1=-2`` and ``id1=0002`` behave exactly as they do at the
console.
"""

from __future__ import annotations

from pathlib import Path

from fastapi import FastAPI, HTTPException, Query
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel

from app.cobol_types import Pic9, accept_pic9
from app.search_program import (
    NOT_FOUND,
    RECORD_FOUND,
    display_found_item,
    display_found_no_key_item,
    keyed_conditions,
    search_all_items,
    search_no_key_items,
)
from app.tables import STORE, ItemRecord, NoKeyRecord

#: Accepted query values are bounded so the service cannot be trivially amplified.
MAX_INPUT_BYTES = 256

STATIC_DIR = Path(__file__).resolve().parent / "static"

app = FastAPI(
    title="search-example (migrated)",
    description="Cloud-native port of the GnuCOBOL search/search.cbl inquiry program.",
    version="1.0.0",
)


class ItemView(BaseModel):
    item_id_1: str
    item_id_2: str
    item_id_3: str
    name: str
    name_trimmed: str
    date: str


class NoKeyItemView(BaseModel):
    no_key_id: str
    no_key_value: str
    no_key_value_trimmed: str


class SearchResponse(BaseModel):
    mode: str
    accepted: dict[str, str]
    found: bool
    message: str
    lines: list[str]
    item: ItemView | None = None
    no_key_item: NoKeyItemView | None = None


def _accept(raw: str, field: str) -> Pic9:
    encoded = raw.encode("utf-8")
    if len(encoded) > MAX_INPUT_BYTES:
        raise HTTPException(status_code=422, detail=f"{field} exceeds {MAX_INPUT_BYTES} bytes")
    return accept_pic9(encoded)


def _decode(lines: list[bytes]) -> list[str]:
    return [line.decode("latin-1") for line in lines]


def _item_view(record: ItemRecord) -> ItemView:
    name = record.name.decode("latin-1")
    return ItemView(
        item_id_1=str(record.item_id_1),
        item_id_2=str(record.item_id_2),
        item_id_3=str(record.item_id_3),
        name=name,
        name_trimmed=name.rstrip(" "),
        date=record.date.decode("latin-1"),
    )


def _no_key_item_view(record: NoKeyRecord) -> NoKeyItemView:
    value = record.no_key_value.decode("latin-1")
    return NoKeyItemView(
        no_key_id=str(record.no_key_id),
        no_key_value=value,
        no_key_value_trimmed=value.rstrip(" "),
    )


def _not_found(mode: str, accepted: dict[str, str]) -> SearchResponse:
    """The COBOL's own ``AT END`` wording — an empty result, not an error."""
    return SearchResponse(
        mode=mode,
        accepted=accepted,
        found=False,
        message=NOT_FOUND.decode("latin-1"),
        lines=[NOT_FOUND.decode("latin-1")],
    )


@app.get("/api/search/keyed", response_model=SearchResponse)
def search_keyed(id1: str = Query("", description="raw ACCEPT line for ws-accept-id-1")) -> SearchResponse:
    """``SEARCH ALL ws-item-table WHEN ws-item-id-1(idx) = ws-accept-id-1``."""
    accepted_id_1 = _accept(id1, "id1")
    accepted = {"id1": str(accepted_id_1)}
    hit = search_all_items(keyed_conditions(accepted_id_1))
    if hit is None:
        return _not_found("keyed", accepted)
    record = STORE.items[hit]
    return SearchResponse(
        mode="keyed",
        accepted=accepted,
        found=True,
        message=RECORD_FOUND.decode("latin-1"),
        lines=_decode(display_found_item(record)),
        item=_item_view(record),
    )


@app.get("/api/search/keyed-all", response_model=SearchResponse)
def search_keyed_all(
    id1: str = Query("", description="raw ACCEPT line for ws-accept-id-1"),
    id2: str = Query("", description="raw ACCEPT line for ws-accept-id-2"),
    id3: str = Query("", description="raw ACCEPT line for ws-accept-id-3"),
) -> SearchResponse:
    """``SEARCH ALL`` with all three declared keys; a partial match is not found."""
    accepted_id_1 = _accept(id1, "id1")
    accepted_id_2 = _accept(id2, "id2")
    accepted_id_3 = _accept(id3, "id3")
    accepted = {"id1": str(accepted_id_1), "id2": str(accepted_id_2), "id3": str(accepted_id_3)}
    hit = search_all_items(keyed_conditions(accepted_id_1, accepted_id_2, accepted_id_3))
    if hit is None:
        return _not_found("keyed-all", accepted)
    record = STORE.items[hit]
    return SearchResponse(
        mode="keyed-all",
        accepted=accepted,
        found=True,
        message=RECORD_FOUND.decode("latin-1"),
        lines=_decode(display_found_item(record)),
        item=_item_view(record),
    )


@app.get("/api/search/sequential", response_model=SearchResponse)
def search_sequential(
    id: str = Query("", description="raw ACCEPT line for the unkeyed table"),
) -> SearchResponse:
    """``SEARCH ws-no-key-item-table``: sequential scan of the unsorted table."""
    accepted_id = _accept(id, "id")
    accepted = {"id": str(accepted_id)}
    hit = search_no_key_items(accepted_id)
    if hit is None:
        return _not_found("sequential", accepted)
    record = STORE.no_key_items[hit]
    return SearchResponse(
        mode="sequential",
        accepted=accepted,
        found=True,
        message=RECORD_FOUND.decode("latin-1"),
        lines=_decode(display_found_no_key_item(record)),
        no_key_item=_no_key_item_view(record),
    )


@app.get("/api/tables")
def tables() -> dict[str, list[dict[str, str]]]:
    """The working-storage tables as loaded by ``setup-test-data``."""
    return {
        "ws_item_table": [_item_view(record).model_dump() for record in STORE.items],
        "ws_no_key_item_table": [_no_key_item_view(record).model_dump() for record in STORE.no_key_items],
    }


@app.get("/", include_in_schema=False)
def index() -> FileResponse:
    return FileResponse(STATIC_DIR / "index.html")


app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")
