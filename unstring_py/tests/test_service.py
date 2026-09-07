"""API tests: one endpoint per UNSTRING example, plus the store and input caps."""

from __future__ import annotations

import pytest
from fastapi.testclient import TestClient

from app.program import DEFAULT_SOURCE_NUM, run_program
from app.service import MAX_INPUT_LEN, RunStore, app

client = TestClient(app)


def test_health():
    body = client.get("/api/health").json()
    assert body["status"] == "ok"
    assert body["program"] == "unstring-example"


def test_defaults_match_the_cobol_literals():
    body = client.get("/api/defaults").json()
    assert body["simple_source"] == "Hello World"
    assert body["delimiter"] == "|"
    assert body["amount"] == str(DEFAULT_SOURCE_NUM)
    assert (body["source_width"], body["dest_width"], body["table_occurs"]) == (30, 5, 6)


def test_program_endpoint_reproduces_stdout():
    body = client.get("/api/program").json()
    assert body["stdout"] == run_program().stdout_text()
    assert len(body["examples"]) == 6
    # Money is a string on the wire, never a JSON number.
    assert isinstance(body["working_storage"]["source_num"], str)


@pytest.mark.parametrize("number", [1, 2, 3, 4, 5, 6])
def test_one_endpoint_per_example(number):
    body = client.get(f"/api/examples/{number}").json()
    assert body["number"] == number
    assert body["lines"]
    assert body["stats"]


def test_unknown_example_is_not_found():
    response = client.get("/api/examples/7")
    assert response.status_code == 404
    assert response.json()["detail"] == "no such example: 7"


def test_run_round_trip():
    created = client.post("/api/runs", json={"simple_source": "alpha beta"}).json()
    fetched = client.get(f"/api/runs/{created['run_id']}").json()
    assert fetched["stdout"] == created["stdout"]
    assert "alpha" in created["examples"][0]["stats"]["part_1"]


def test_unknown_run_is_not_found():
    response = client.get("/api/runs/does-not-exist")
    assert response.status_code == 404
    assert response.json()["detail"] == "run not found"


def test_delimiter_must_be_one_character():
    assert client.post("/api/runs", json={"delimiter": "||"}).status_code == 422


def test_oversized_input_is_rejected():
    response = client.post("/api/runs", json={"simple_source": "x" * (MAX_INPUT_LEN + 1)})
    assert response.status_code == 422


def test_non_numeric_amount_is_rejected():
    response = client.post("/api/runs", json={"amount": "twelve"})
    assert response.status_code == 422


@pytest.mark.parametrize("amount", ["1e9999999", "9" * 30, "NaN", "Infinity"])
def test_amounts_wider_than_a_cobol_literal_are_rejected_not_crashed(amount):
    assert client.post("/api/runs", json={"amount": amount}).status_code == 422


def test_working_storage_reports_the_picture_constrained_amount():
    body = client.post("/api/runs", json={"amount": "91234567.899"}).json()
    # High-order and low-order truncation both happen in the MOVE (D-006/D-007).
    assert body["working_storage"]["source_num"] == "234567.89"
    assert body["examples"][5]["stats"]["edited_value"] == "$234,567.89"


def test_run_store_is_bounded_lru():
    store = RunStore(capacity=2)
    first = store.put({"n": 1})
    second = store.put({"n": 2})
    store.get(first)
    third = store.put({"n": 3})
    assert len(store) == 2
    assert store.get(second) is None
    assert store.get(first) == {"n": 1}
    assert store.get(third) == {"n": 3}


def test_index_serves_the_leumi_ui():
    body = client.get("/").text
    assert "בנק לאומי" in body
    assert "app.js" in body
