# `unstring_py` — cloud-native migration of `unstring-example`

A Python/FastAPI port of `unstring/unstring.cbl`, built *alongside* the COBOL. The COBOL
stays the specification and the system of record: nothing under `unstring/` is modified,
and every release is gated by a harness that compiles and runs the real program and diffs
its stdout byte for byte against the port.

```
unstring_py/
  app/          port (cobol_types, unstring engine, program, FastAPI service) + static UI
  parity/       oracle (cobc), fixtures, reconciliation harness
  docs/         phase0-contracts.md — field layouts, output geometry, numbered rulings
  tests/        unit tests, API tests, and the parity gate
```

## Run it

```bash
cd unstring_py
pip install -e '.[dev]'
uvicorn app.service:app --port 8000
```

Then open http://localhost:8000 — a Bank Leumi-skinned single page that replaces the
console output: enter the source strings, the `ws-delimiter` character and the amount, and
each example's parsed fields, tallies, delimiters, character counts and pointer are shown
alongside the raw fixed-width transcript in a monospace pane.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/health` | liveness |
| GET | `/api/defaults` | the literals the COBOL hard-codes |
| GET | `/api/program` | run all six examples, full transcript + statistics |
| GET | `/api/examples/{1..6}` | one endpoint per `EXAMPLE n` block |
| POST | `/api/runs` | run and store (bounded LRU) |
| GET | `/api/runs/{run_id}` | retrieve a stored run |

Monetary values (`ws-source-num`, `PIC $999,999.99`) cross the wire as JSON **strings** and
are held as `decimal.Decimal` — never a float.

## Parity gate

```bash
python -m parity.harness          # 0 = clean, 1 = diffs, 2 = cobc missing (skip)
pytest                            # runs the same gate inside the suite
```

Requires GnuCOBOL: `sudo apt-get install -y gnucobol`. Without `cobc` the gate **skips**;
it never passes silently. `tests/test_parity.py` also seeds deliberate regressions
(half-up rounding, shifted output) and asserts the harness reports diffs.

## Checks

```bash
ruff check . && ruff format --check . && mypy && pytest
```
