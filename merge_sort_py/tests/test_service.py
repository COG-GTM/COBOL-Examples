"""Read path, write path and paging boundaries (contracts D-008, D-009, D-010)."""

from __future__ import annotations

import pytest
from fastapi.testclient import TestClient

from app import service
from app.service import app
from app.store import InMemoryRunStore

client = TestClient(app)


@pytest.fixture(autouse=True)
def _fresh_store():
    service.store = InMemoryRunStore()
    yield


def record(customer_id: str, tag: str, contract_id: str) -> str:
    return (customer_id + tag.ljust(50) + tag.ljust(50) + contract_id + tag.ljust(25)).rstrip(" ")


def generated_run() -> str:
    response = client.post("/api/runs", json={"generate": True})
    assert response.status_code == 201
    run_id: str = response.json()["run_id"]
    return run_id


def test_health():
    assert client.get("/api/health").json()["program"] == "merge-sort-example"


def test_generated_run_reports_counts_console_and_statuses():
    response = client.post("/api/runs", json={"generate": True})
    body = response.json()
    assert response.status_code == 201
    assert body["merged_count"] == 11
    assert body["sorted_count"] == 11
    assert body["console"][0] == "Creating test data files..."
    assert body["console"][-1] == "Done."
    assert [entry["file"] for entry in body["file_statuses"]] == [
        "test-file-1.txt",
        "test-file-2.txt",
        "merge-output.txt",
        "sorted-contract-id.txt",
    ]
    assert all(entry["file_status"] == "00" for entry in body["file_statuses"])
    # The program displays no message while the status is "00".
    assert all(entry["message"] == "" for entry in body["file_statuses"])


def test_supplied_inputs_are_used_and_never_written_to_disk():
    payload = {
        "test_file_1": record("00002", "two", "00002") + "\n",
        "test_file_2": record("00001", "one", "00001") + "\n",
    }
    body = client.post("/api/runs", json=payload).json()
    merged = client.get(f"/api/runs/{body['run_id']}/merge-output").json()
    assert [r["last_name"] for r in merged["records"]] == ["one", "two"]

    sorted_page = client.get(f"/api/runs/{body['run_id']}/sorted-contract-id").json()
    assert [r["last_name"] for r in sorted_page["records"]] == ["two", "one"]


def test_read_endpoints_are_paged_at_every_boundary():
    run_id = generated_run()

    first = client.get(f"/api/runs/{run_id}/merge-output?page=1&page_size=5").json()
    assert first["page"] == 1
    assert first["page_count"] == 3
    assert first["record_count"] == 11
    assert first["more_data"] is True
    assert len(first["records"]) == 5

    middle = client.get(f"/api/runs/{run_id}/merge-output?page=2&page_size=5").json()
    assert middle["more_data"] is True
    assert len(middle["records"]) == 5

    last = client.get(f"/api/runs/{run_id}/merge-output?page=3&page_size=5").json()
    assert last["more_data"] is False
    assert len(last["records"]) == 1

    past_end = client.get(f"/api/runs/{run_id}/merge-output?page=4&page_size=5")
    assert past_end.status_code == 404


def test_page_raw_is_byte_faithful_fixed_width_text():
    run_id = generated_run()
    page = client.get(f"/api/runs/{run_id}/merge-output?page=1&page_size=1").json()
    line = page["raw"].rstrip("\n")
    assert line.startswith("00001")
    assert line[5:55].rstrip() == "last-1"
    # Trailing spaces of the final field are stripped by the line-sequential WRITE (D-006).
    assert not line.endswith(" ")


def test_empty_result_is_an_empty_page_not_an_error():
    body = client.post("/api/runs", json={"test_file_1": "", "test_file_2": ""}).json()
    page = client.get(f"/api/runs/{body['run_id']}/merge-output")
    assert page.status_code == 200
    assert page.json()["record_count"] == 0
    assert page.json()["records"] == []
    assert page.json()["page_count"] == 1
    assert page.json()["more_data"] is False


def test_unknown_run_is_404():
    assert client.get("/api/runs/deadbeef/merge-output").status_code == 404
    assert client.get("/api/runs/deadbeef").status_code == 404


def test_page_size_bounds_are_enforced():
    run_id = generated_run()
    assert client.get(f"/api/runs/{run_id}/merge-output?page_size=0").status_code == 422
    assert client.get(f"/api/runs/{run_id}/merge-output?page_size=100000").status_code == 422
    assert client.get(f"/api/runs/{run_id}/merge-output?page=0").status_code == 422


def test_raw_file_endpoints_expose_all_four_cobol_files():
    run_id = generated_run()
    for name in ("test-file-1.txt", "test-file-2.txt", "merge-output.txt", "sorted-contract-id.txt"):
        response = client.get(f"/api/runs/{run_id}/files/{name}")
        assert response.status_code == 200
        assert response.text.count("\n") in (6, 5, 11)
    assert client.get(f"/api/runs/{run_id}/files/not-a-file.txt").status_code == 422


def test_console_endpoint_is_the_programs_display_output():
    run_id = generated_run()
    text = client.get(f"/api/runs/{run_id}/console").text
    assert text.startswith("Creating test data files...\n")
    assert text.endswith("Done.\n")


def test_record_limit_returns_413():
    oversized = "".join(record("00001", "x", "00001") + "\n" for _ in range(20_001))
    response = client.post("/api/runs", json={"test_file_1": oversized, "test_file_2": ""})
    assert response.status_code == 413


def test_run_store_is_bounded():
    store = InMemoryRunStore(capacity=2)
    service.store = store
    ids = [generated_run() for _ in range(3)]
    assert len(store) == 2
    assert client.get(f"/api/runs/{ids[0]}").status_code == 404
    assert client.get(f"/api/runs/{ids[2]}").status_code == 200


def test_index_and_static_assets_are_served():
    assert "Bank Leumi" in client.get("/").text
    assert client.get("/static/styles.css").status_code == 200
    assert client.get("/static/app.js").status_code == 200
