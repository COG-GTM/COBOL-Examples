# Phase 0 — Contracts for `merge-sort-example`

Source of truth: `merge_sort/merge_sort_test.cbl` (program-id `merge-sort-example`), unmodified.
Oracle: GnuCOBOL 3.1.2.0 (`cobc -x merge_sort_test.cbl -o merge_sort_test`), fixed format.

Everything below was derived by reading the whole source and then **running the compiled binary**;
where the two could disagree, the observed behaviour of the binary wins and the ruling says so.

**Authority order:** FD/SD record description in the program > procedure division source >
any prose/documentation. There are no copybooks, JCL, BMS maps, DB2 tables or CICS calls in this
program: it is a self-contained batch job over line-sequential files.

---

## 1. Inventory

| COBOL name | Kind | Organization | Read by | Written by |
| --- | --- | --- | --- | --- |
| `fd-test-file-1` (`test-file-1.txt`) | FD | line sequential | `merge-and-display-files` (MERGE USING) | `create-test-data` |
| `fd-test-file-2` (`test-file-2.txt`) | FD | line sequential | `merge-and-display-files` (MERGE USING) | `create-test-data` |
| `fd-merged-file` (`merge-output.txt`) | FD | line sequential | `merge-and-display-files` (display loop), `sort-and-display-file` (SORT USING) | MERGE GIVING |
| `fd-sorted-contract-id` (`sorted-contract-id.txt`) | FD | line sequential | `sort-and-display-file` (display loop) | SORT GIVING |
| `fd-sorting-file` | SD | work file | MERGE / SORT | MERGE / SORT |

Paragraphs: `main-procedure` → `create-test-data`, `merge-and-display-files`,
`sort-and-display-file`. The Python port keeps those names
(`app/merge_sort.py::create_test_data`, `merge_and_display_files`, `sort_and_display_file`,
`main_procedure`).

## 2. Record layout — 135 bytes, identical in every FD and the SD

| Field | PIC | Offset | Length | Python representation |
| --- | --- | --- | --- | --- |
| `f-customer-id` | `9(5)` | 0 | 5 | 5 raw bytes, `str` of length 5 (`CustomerRecord.customer_id`); sort key via `display_key_value()` → `int` |
| `f-customer-last-name` | `X(50)` | 5 | 50 | 50 raw bytes, space padded |
| `f-customer-first-name` | `X(50)` | 55 | 50 | 50 raw bytes, space padded |
| `f-customer-contract-id` | `9(5)` | 105 | 5 | as `f-customer-id` |
| `f-customer-comment` | `X(25)` | 110 | 25 | 25 raw bytes, space padded |

There is **no monetary, `COMP-3`, `V99` or `ROUNDED` field anywhere in this program**, and no
arithmetic at all — so there is no rounding rule to derive. The only numeric semantics that
matter are the *comparison* semantics of `PIC 9(5)` DISPLAY under a SORT key (§4). No value is
ever held as a float: `int` for keys, raw byte strings for data.

A record is carried in Python as `CustomerRecord.raw`: a `str` of exactly 135 characters where
every character is one **byte** (latin-1 round-trip, `app/cobol_types.to_byte_string`). COBOL
applies the layout to bytes; slicing Python characters would mis-align any non-ASCII input
(ruling D-007).

## 3. Keys and ordering

```cobol
merge fd-sorting-file on ascending key f-customer-id
    of f-customer-record-merged
    using fd-test-file-1 fd-test-file-2 giving fd-merged-file

sort fd-sorting-file on descending key f-customer-contract-id
    of f-customer-record-sorted-contract-id
    using fd-merged-file giving fd-sorted-contract-id
```

Single-field keys, not compound. `merge-output.txt` is the two inputs ordered ascending by
customer id; `sorted-contract-id.txt` is `merge-output.txt` re-ordered descending by contract id.
The SORT reads the *file* the merge wrote, so the records go through line-sequential read
semantics a second time (D-005/D-006 apply to that pass too).

**No de-duplication.** Neither statement has `DUPLICATES` suppression and neither drops rows: the
record count out equals the record count in, for both passes. Verified on fixtures
`duplicate_ids_across_files` and `duplicate_contract_ids`.

## 4. Discrepancy register / rulings

Each ruling names the fixture in `parity/fixtures.py` that pins it.

### D-001 — MERGE does not assume its inputs are ordered
`MERGE` is defined on pre-sorted inputs; the program's own `create-test-data` writes both files
**unsorted** (file 1 is `1, 5, 10, 50, 25, 75`). Standard-wise this is undefined input, so a
different compiler could legitimately produce interleaved garbage. GnuCOBOL 3.1.2 fully sorts the
concatenation, and the program's committed output depends on it.
**Ruling: preserve GnuCOBOL's behaviour** — the port sorts the concatenation of both files. Recorded
as a portability risk, not a bug to fix. Fixture: `unsorted_inputs`.

### D-002 — `PIC 9(5)` compares as a *converted number*, and non-digit bytes do not raise
The fields are DISPLAY numerics but nothing validates them. GnuCOBOL converts the 5 bytes to a
32-bit unsigned accumulator and compares that, so non-digit bytes produce a defined but surprising
key. Empirically (all confirmed against the binary):

* Leading bytes whose **low nibble is zero** are skipped as leading zeros, so
  `"00000"`, `" 0000"`, `"@0000"`, `"P0000"`, `"`0000"` and `"p0000"` all key as **0** and tie.
* Otherwise each byte contributes `byte - 0x30`, accumulating mod 2³², so `"ABCDE"` → 190121,
  `"0000:"` → 10, `"0001*"` → 4, and `"   12"` ties exactly with `"00012"` at 12.
* A byte below `'0'` in the last position wraps: `"0000/"` → 4294967295, `"     "` → 4294967280,
  `"-0001"` → 4294937297. **A blank key therefore sorts at the very top of an ascending merge**, not
  at the bottom.
* A leading `0xFF` clamps to `10⁵` (above every digit-only value) and a leading `0x00` clamps to
  `-10⁵` (below every digit-only value).

**Ruling: preserve exactly.** `app/cobol_types.display_key_value()` implements this and nothing
sanitises input. Rejecting non-digits would change the ordering of live data.
Fixtures: `leading_zero_nibble_keys`, `non_numeric_keys`, `sentinel_bytes`.

### D-003 — Both passes are stable, and DESCENDING is a reversed key, not a reversed result
Neither statement names a tie-breaker. Empirically GnuCOBOL's sort is **stable**:

* MERGE: equal customer ids keep every file-1 record before every file-2 record, and keep input
  order within each file.
* SORT: equal contract ids keep their `merge-output.txt` order — the group is *not* reversed.

**Ruling: match it.** The port uses Python's stable `sorted()` with `key=+value` for ascending and
`key=-value` for descending — a negated key rather than `reverse=True`, so the code says what the
COBOL says: the *key* is descending, the tie order is not touched. Verified at scale on
1500 equal-keyed records. Fixtures: `duplicate_ids_across_files`, `duplicate_contract_ids`,
`large_stable_run`.

### D-004 — `X(n)` padding is data
`move "last-1" to f-customer-last-name` space-fills to 50. Those spaces are part of the record and
survive into the console display and into every downstream read.
**Ruling: never `.strip()` before re-emitting.** `CustomerRecord.raw` keeps the padding; only
`as_json()` trims, and only for the JSON view. Fixture: `padding_is_data`.

### D-005 — Short, empty and over-long input lines
A line-sequential READ pads a short line to 135 bytes with spaces and truncates a longer one at
135; an empty line becomes 135 spaces (which then keys as 4294967280 per D-002, so blank rows sort
near the end of the *descending* pass and at the top of the ascending one). No error, no status
other than `"00"`.
**Ruling: preserve.** `cobol_types.read_record()` pads/truncates identically.
Fixture: `short_and_ragged_lines`.

### D-006 — WRITE strips trailing spaces; a trailing tab or CR is different
A line-sequential WRITE emits the record area with **trailing spaces removed**, then a newline —
so an all-blank record becomes an empty line. A trailing tab is *not* a space and is retained. On
read, a trailing CR is stripped before the record is padded.
**Ruling: preserve all three.** `cobol_types.write_record()` / `read_record()`.
Fixture: `trailing_tab_and_cr`.

### D-007 — The layout is applied to bytes, not characters
Multibyte UTF-8 input shifts the columns: `"בנק לאומי"` occupies 17 bytes, not 9 characters, and
GnuCOBOL slices at byte 5/55/105/110 regardless.
**Ruling: model bytes.** Input text is round-tripped through
`text.encode("utf-8").decode("latin-1")` before any slicing, and parity is compared as `bytes`.
Fixture: `multibyte_utf8`.

That representation must be undone at every API boundary or it leaks out double-encoded: JSON
fields (`records[].*`, `PageView.raw`, `RunSummary.console`) are decoded back with
`from_byte_string()`, and the byte-exact endpoints (`/console`, `/files/{name}`) return a
`Response` built from `to_bytes()` rather than letting the framework UTF-8 encode a string that is
already a byte sequence.

### D-008 — `file status` handling, and its HTTP mapping
The program declares `file status` on all four files and checks it after each `open`, at three
sites with three distinct literals:

| Site | Check | Message | Then |
| --- | --- | --- | --- |
| `open output fd-test-file-1` / `fd-test-file-2` | `not = "00"` | `Failed to open file for output: ` + status | `stop run` |
| `open input fd-merged-file` | `not = "00"` | `Error opening merged output file: ` + status | `stop run` |
| `open input fd-sorted-contract-id` | `not = "00"` | `Error opening sorted output file: ` + status | `stop run` |

Note what is **not** checked: the status after `close`, after each `write`, and the MERGE/SORT
statements themselves have no `on exception`. A write failure is invisible to this program.

The service holds all four files in memory, so an open can never fail there. The guards are still
ported (the status fields exist on `MergeSortRun` and are settable) and are mapped as:

| COBOL status | HTTP | Body |
| --- | --- | --- |
| `"00"` | `200` / `201` | the run, with `file_statuses[].message` empty — the program displays nothing on success |
| any other value at an `open` guard | `502` | `detail` = the program's own message literal + the status code, e.g. `Error opening merged output file: 35` |

A run that produced **zero** records is `200` with an empty page, never `404` — an empty result is
an empty state (Phase 5). `404` is reserved for an unknown/evicted `run_id` and for paging past the
last page.
**Ruling: expose the program's literals verbatim**, rather than translating statuses into
service-native error text.

### D-009 — Messages with no COBOL counterpart
The COBOL has no interactive input, so it has no wording for "you must supply both files" or for a
paging boundary. Those strings are ours (`app/static/app.js`, `app/service.py` HTTP details) and are
marked as such here so a reviewer does not go looking for them in the source. Every string that
*does* have a COBOL counterpart — the four console literals, the three failure literals, the 135-byte
display lines — is reproduced character for character.

### D-010 — Limits the COBOL does not have
The batch job is bounded by its input dataset; a public endpoint is not. The service caps input at
4 MiB per request and 20 000 records per run (`RecordLimitExceeded` → `413`) and bounds the run
store to 32 runs (LRU). These are **additions**, not ported behaviour; a run that would exceed them
is rejected, never silently truncated.

4 MiB is enforced twice, because the two readings of "4 MiB per request" differ: middleware rejects
a declared `Content-Length` above the cap before anything parses or buffers the body, and
`create_run` re-checks the decoded file content, which is the figure the 20 000-record cap is
expressed in. The LRU store is also lock-guarded — the batch job was single threaded, the service is
not, and a lookup racing an eviction would otherwise fail a valid run id.

## 5. Console output

`main-procedure` displays, in order: `Creating test data files...`, `Merging and sorting files...`,
then every merged record's full 135-byte record area, `Sorting merged file on descending contract
id....` (four dots — the literal is reproduced as-is), every sorted record's 135-byte record area,
then `Done.`. `MergeSortRun.console_text()` reproduces this byte for byte and it is diffed by the
parity harness alongside the three output files.

## 6. Parity strategy

Two oracles, both compiled from real COBOL by `cobc`:

1. **The unmodified legacy program**, copied into a scratch directory and run there (never in
   `merge_sort/`), covering `create-test-data` and the program's own rows end to end.
2. **A stand-in** (`parity/cobol/merge_sort_oracle.cbl`) for fixture-driven runs, because the legacy
   program hard-codes its inputs and cannot be fed. It is the same source with `create-test-data`
   deleted and nothing else changed — the FD/SD entries, the MERGE and the SORT statements and the
   display loops are line-for-line the originals. A test (`test_parity.py`) runs the stand-in over
   the program's own rows and asserts it reproduces the real program's three outputs, so the
   stand-in cannot drift.
