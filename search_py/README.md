# search_py — cloud-native migration of `search-example`

A running Python service that reproduces `search/search.cbl` (GnuCOBOL program
`search-example`): its two working-storage tables, its binary (`SEARCH ALL`) and
sequential (`SEARCH`) lookups, its `PIC 9(4)` `ACCEPT` semantics and its exact
console wording — proven byte for byte against the compiled COBOL.

The legacy tree is untouched and remains the system of record. Nothing here
writes anywhere; the program is an inquiry transaction, so the migration is
read-only.

```
search_py/
  app/            port (cobol_types, tables, search_program) + FastAPI service
  app/static/     Bank Leumi-skinned UI replacing the ACCEPT prompts
  parity/         COBOL oracle, fixtures, reconciliation harness
  docs/           phase0-contracts.md — layouts, output geometry, rulings D-001…D-011
  tests/          unit, API and parity tests
```

## Setup

```bash
cd search_py
python3 -m venv .venv && . .venv/bin/activate
pip install -e ".[dev]"
sudo apt-get update && sudo apt-get install -y gnucobol   # for the parity gate
```

## Run

```bash
uvicorn app.service:app --port 8000
# http://localhost:8000        UI
# http://localhost:8000/docs   OpenAPI
```

| Endpoint | COBOL construct |
| --- | --- |
| `GET /api/search/keyed?id1=` | `SEARCH ALL` on `ws-item-id-1` only |
| `GET /api/search/keyed-all?id1=&id2=&id3=` | `SEARCH ALL` on all three declared keys |
| `GET /api/search/sequential?id=` | `SEARCH` over the unkeyed table |
| `GET /api/tables` | both tables as loaded by `setup-test-data` |

Query values are the **raw** `ACCEPT` lines: `id1=-2` accepts as `0002` and
`id1=12345` as `1234`, exactly as at the console (ruling D-006). A miss returns
HTTP 200 with the COBOL's own `Item not found.` — an empty result, not an error.

## Parity

```bash
python parity/harness.py     # 0 = parity, 1 = diffs, 2 = skipped (no cobc)
```

The harness compiles the **unmodified** `search/search.cbl` into a scratch
directory, pipes each fixture's answers to its stdin and diffs the whole stdout
byte for byte. Fixtures cross every boundary in the contracts doc: exact match,
partial key match, no match, first/last table entry, blank, whitespace-only,
non-numeric, signed, decimal, separator, over-long and multibyte input, early
EOF and a missing trailing newline. Two seeded-regression tests prove the gate
has teeth, and it skips (never passes) when `cobc` is absent.

## Checks

```bash
ruff check . && ruff format --check . && mypy && pytest
```
