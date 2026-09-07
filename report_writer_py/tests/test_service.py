from __future__ import annotations

from fastapi.testclient import TestClient

from app.service import app
from parity.fixtures import make_record

client = TestClient(app)


def test_create_and_fetch_report() -> None:
    payload = {"input_text": make_record("000009", "Dana", "ART", "05") + "\n"}
    created = client.post("/api/reports", json=payload)
    assert created.status_code == 200
    body = created.json()
    assert body["record_count"] == 1
    assert body["detail_count"] == 2  # ruling D-001
    assert body["page_count"] == 1
    assert body["console"][-1] == "Done."
    assert body["records"][0]["student_name"] == "Dana"

    fetched = client.get(f"/api/reports/{body['id']}")
    assert fetched.status_code == 200
    assert fetched.json()["report_text"] == body["report_text"]


def test_page_endpoint_and_not_found_paths() -> None:
    rows = "".join(make_record(f"{i:06d}", f"N{i}", "PHY", "01") + "\n" for i in range(40))
    body = client.post("/api/reports", json={"input_text": rows}).json()
    assert body["page_count"] == 2

    page = client.get(f"/api/reports/{body['id']}/pages/2")
    assert page.status_code == 200
    assert page.json()["number"] == 2

    assert client.get(f"/api/reports/{body['id']}/pages/9").status_code == 404
    assert client.get("/api/reports/does-not-exist").status_code == 404


def test_empty_input_is_not_an_error() -> None:
    response = client.post("/api/reports", json={"input_text": ""})
    assert response.status_code == 200
    assert response.json()["detail_count"] == 1


def test_oversized_input_is_rejected() -> None:
    response = client.post("/api/reports", json={"input_text": "x" * 1_000_001})
    assert response.status_code == 413


def test_sample_input_and_index() -> None:
    assert "input_text" in client.get("/api/sample-input").json()
    assert client.get("/").status_code == 200
