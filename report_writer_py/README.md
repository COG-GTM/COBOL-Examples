# report_writer_py — Python migration of the COBOL `report-test` program

A cloud-native port of `report_writer/report_test.cbl` (GnuCOBOL REPORT WRITER) built
*alongside* the COBOL. The COBOL stays the system of record and is byte-identical: this
directory only ever reads it.

- `app/` — the port: FD record layout, DISPLAY-numeric move semantics, `RD r-test-report`
  page geometry, and a FastAPI service.
- `app/static/` — Bank Leumi themed UI replacing the console/print output.
- `parity/` — reconciliation harness that compiles and runs the unmodified COBOL and diffs
  its `report.txt` against the port, line by line.
- `docs/phase0-contracts.md` — extracted contracts and the discrepancy register (D-001…D-007).

## Setup

```bash
python3 -m venv .venv && .venv/bin/pip install -e ".[dev]"
```

GnuCOBOL (`cobc`) is required for the parity gate. Without it the harness **skips**; it never
reports parity it did not verify.

## Run

```bash
.venv/bin/uvicorn app.service:app --reload --port 8000   # then open http://localhost:8000
```

## Verify

```bash
.venv/bin/ruff check . && .venv/bin/ruff format --check . && .venv/bin/mypy
.venv/bin/python -m pytest -q          # unit + API + parity tests
.venv/bin/python parity/harness.py     # parity gate; non-zero exit on any diff
```

## API

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/reports` | Run the report over an input file body; returns the print file, pages, parsed records and the COBOL console messages |
| `GET` | `/api/reports/{id}` | Fetch a previous run |
| `GET` | `/api/reports/{id}/pages/{n}` | One physical page |
| `GET` | `/api/sample-input` | The repository's own `report_writer/input.txt` |

All endpoints are read-only; nothing writes to the legacy tree and there is no write/batch
path to port — the COBOL program has no output other than its print file.
