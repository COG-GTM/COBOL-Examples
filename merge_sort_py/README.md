# merge_sort_py — cloud-native port of `merge-sort-example`

A Python/FastAPI service, a static Bank Leumi-skinned UI, and a byte-level parity harness for
`merge_sort/merge_sort_test.cbl`. **The COBOL is untouched and remains the specification and the
system of record**; nothing here writes into the legacy tree, and every output is produced in
memory.

```
merge_sort_py/
├── app/
│   ├── cobol_types.py   PIC semantics: 135-byte geometry, line-sequential read/write, PIC 9(5) keys
│   ├── record.py        the f-customer-record layout shared by every FD/SD
│   ├── merge_sort.py    the port, paragraph for paragraph
│   ├── service.py       FastAPI read + write endpoints
│   ├── store.py         bounded in-memory run store behind a small adapter
│   └── static/          the UI that replaces the console output
├── docs/phase0-contracts.md   record layouts, PIC semantics, and the numbered rulings
├── parity/
│   ├── cobol/merge_sort_oracle.cbl   stand-in oracle (the original, minus create-test-data)
│   ├── oracle.py        compiles and runs real GnuCOBOL in a scratch directory
│   ├── fixtures.py      15 fixtures, each naming the boundary it crosses
│   └── harness.py       diffs both sides field by field; exits non-zero on any diff
└── tests/
```

## Run it

```bash
pip install -e '.[dev]'          # or: pip install fastapi uvicorn pydantic pytest httpx ruff mypy
uvicorn app.service:app --port 8000
```

Open <http://localhost:8000>. Generate the program's own test data or supply/upload two input
files, then read `merge-output.txt` and `sorted-contract-id.txt` side by side in monospace panes,
with the record counts and the file-status messages the COBOL itself reports.

## Parity

```bash
sudo apt-get install -y gnucobol   # if cobc is missing
python parity/harness.py           # 0 = parity, 1 = diffs, 2 = SKIPPED (no toolchain)
pytest                             # the same gate, plus seeded-regression tests
```

The harness runs two oracles, both compiled from real COBOL: the **unmodified legacy program**
(copied to a scratch directory, never run inside `merge_sort/`) and a **stand-in** that is the same
source with `create-test-data` removed so fixtures can be fed in. A test asserts the stand-in
reproduces the real program's output, so it cannot drift. Three seeded-regression tests break the
port deliberately and assert the harness notices.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/runs` | Write path: run the program over supplied files, or `{"generate": true}` for its own test data. In memory only. |
| `GET` | `/api/runs/{run_id}` | Record counts, console output, and the four file statuses. |
| `GET` | `/api/runs/{run_id}/merge-output` | `merge-output.txt` — MERGE, ASCENDING `f-customer-id`. Paged. |
| `GET` | `/api/runs/{run_id}/sorted-contract-id` | `sorted-contract-id.txt` — SORT, DESCENDING `f-customer-contract-id`. Paged. |
| `GET` | `/api/runs/{run_id}/console` | The program's DISPLAY output, byte for byte. |
| `GET` | `/api/runs/{run_id}/files/{name}` | Any of the four files as raw line-sequential text. |

Paging: `?page=` (1-based) and `?page_size=` (default 25, max 500). A page past the last page is
`404`; a run with no records is a `200` empty page, not an error.

## Limits (additions, not ported behaviour — see ruling D-010)

4 MiB per request, 20 000 records per run (`413` if exceeded, never silent truncation), and an
LRU-bounded store of 32 runs.

## Reading the contracts

Start with [`docs/phase0-contracts.md`](docs/phase0-contracts.md). It records the exact field
geometry and ten numbered rulings, the most consequential being D-002 (what GnuCOBOL does with a
`PIC 9(5)` that is not clean digits — a blank key sorts *above* `99999`) and D-003 (both sort
passes are stable, and DESCENDING does not reverse tie groups).
