"""Read-path API tests: the endpoints must speak the COBOL's own wording."""

from __future__ import annotations

from fastapi.testclient import TestClient

from app.service import MAX_INPUT_BYTES, app

client = TestClient(app)


def test_keyed_search_hit() -> None:
    body = client.get("/api/search/keyed", params={"id1": "3"}).json()
    assert body["found"] is True
    assert body["message"] == " Record found:"
    assert body["item"]["item_id_1"] == "0003"
    assert body["item"]["name"] == "test item 3     "
    assert body["item"]["name_trimmed"] == "test item 3"
    assert body["item"]["date"] == "2021/03/03"
    assert body["lines"][0] == " Record found:"


def test_keyed_search_miss_is_an_empty_result_not_an_error() -> None:
    response = client.get("/api/search/keyed", params={"id1": "9"})
    assert response.status_code == 200
    body = response.json()
    assert body["found"] is False
    assert body["message"] == "Item not found."
    assert body["item"] is None


def test_keyed_all_requires_every_key() -> None:
    hit = client.get("/api/search/keyed-all", params={"id1": "2", "id2": "102", "id3": "499"}).json()
    assert hit["found"] is True
    miss = client.get("/api/search/keyed-all", params={"id1": "2", "id2": "102", "id3": "999"}).json()
    assert miss["found"] is False
    assert miss["message"] == "Item not found."


def test_sequential_search() -> None:
    body = client.get("/api/search/sequential", params={"id": "1"}).json()
    assert body["found"] is True
    assert body["no_key_item"]["no_key_value"] == "Value of id 1.           "
    assert body["lines"][1] == "---------------"


def test_accept_semantics_are_applied_to_query_values() -> None:
    """D-006: the query value is the raw ACCEPT line, parsed by COBOL's rules."""
    assert client.get("/api/search/keyed", params={"id1": "-2"}).json()["accepted"]["id1"] == "0002"
    assert client.get("/api/search/keyed", params={"id1": "3abc"}).json()["found"] is True
    assert client.get("/api/search/keyed", params={"id1": "a3"}).json()["found"] is False
    assert client.get("/api/search/keyed", params={"id1": ""}).json()["accepted"]["id1"] == "0000"


def test_oversized_input_is_rejected() -> None:
    response = client.get("/api/search/keyed", params={"id1": "9" * (MAX_INPUT_BYTES + 1)})
    assert response.status_code == 422


def test_tables_endpoint_exposes_both_tables() -> None:
    body = client.get("/api/tables").json()
    assert [row["item_id_1"] for row in body["ws_item_table"]] == ["0001", "0002", "0003"]
    assert [row["no_key_id"] for row in body["ws_no_key_item_table"]] == ["0002", "0003", "0001"]


def test_index_and_static_assets_are_served() -> None:
    assert "Bank Leumi" in client.get("/").text
    assert client.get("/static/styles.css").status_code == 200
    assert client.get("/static/app.js").status_code == 200
