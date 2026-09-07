# Phase 0 — Contracts for `search/search.cbl` (`search-example`)

The COBOL is the specification and the system of record. Nothing under `search/`
is modified; this document pins down the record layouts, the observable output
geometry and every behavioural ruling **before** any service code was written.
Every claim below was verified empirically against GnuCOBOL 3.1.2 on Ubuntu with
`cobc -x search.cbl -o search` (fixed format — **not** `-free`).

## 1. Inventory

| Artefact | Kind | Notes |
| --- | --- | --- |
| `search/search.cbl` | program `search-example` | console program; no FILE SECTION, no external I/O |
| `ws-item-table` | working-storage table, `OCCURS 3` | `ASCENDING KEY ws-item-id-1, ws-item-id-2`, `DESCENDING KEY ws-item-id-3`, `INDEXED BY idx` |
| `ws-no-key-item-table` | working-storage table, `OCCURS 3` | no key, unsorted, `INDEXED BY idx-2` |
| `setup-test-data` | paragraph | loads both tables with literals |
| `display-found-item` | paragraph | prints a keyed-table hit |
| `main-procedure` | paragraph | three searches, five `ACCEPT`s |

There are no files, no database and no writes: the program is a pure inquiry
transaction, so the migration has a read path only and **no write path**.

## 2. Record layouts

### `ws-item-table` — 34 bytes per occurrence

| COBOL field | PIC | Bytes | Python representation |
| --- | --- | --- | --- |
| `ws-item-id-1` | `9(4)` | 4 | `Pic9` — `int` value + zero-padded 4-byte display image |
| `ws-item-id-2` | `9(4)` | 4 | `Pic9` |
| `ws-item-id-3` | `9(4)` | 4 | `Pic9` |
| `ws-item-name` | `X(16)` | 16 | `bytes`, left-justified, space filled |
| `ws-item-date` | group | 10 | `bytes` — `9(4)` `/` `99` `/` `99` |

Loaded contents (`setup-test-data`):

```
0001 0101 0500 "test item 1     " 2021/01/01
0002 0102 0499 "test item 2     " 2021/02/02
0003 0103 0498 "test item 3     " 2021/03/03
```

### `ws-no-key-item-table` — 29 bytes per occurrence

| COBOL field | PIC | Bytes | Python representation |
| --- | --- | --- | --- |
| `ws-no-key-id` | `9(4)` | 4 | `Pic9` |
| `ws-no-key-value` | `X(25)` | 25 | `bytes`, space filled |

Loaded contents, in table order: `0002 "Value of id 2."`, `0003 "Value of id 3."`,
`0001 "Value of id 1."` — deliberately **not** sorted by id.

### Accept fields

`ws-accept-id-1`, `ws-accept-id-2`, `ws-accept-id-3` are `PIC 9(4)` display
items, filled by `ACCEPT` from stdin.

No value in this program is fractional and none is ever held as floating point:
`PIC 9(n)` maps to `int` plus its display image, `PIC X(n)` maps to `bytes`.

## 3. Output geometry

Every line the program can emit, byte for byte (`·` marks a trailing space that
is part of the output; `DISPLAY SPACE` emits a single space then a newline):

```
·
==================================================
Searching keyed table using binary search.
Enter id-1 to search for: ←no newline (WITH NO ADVANCING)
```

A keyed hit (`display-found-item`) emits, in order:

```
 Record found:
----------------            (16 dashes)
Item id-1: 0003
Item id-2: 0103
Item id-3: 0498
Item Name: test item 3·····  (PIC X(16), padded)
Item Date: 2021/03/03
·
```

A miss emits exactly `Item not found.`. The sequential hit block emits a
**15-dash** rule (one dash shorter than the keyed one — see D-007) and the
`   ws-no-key-id: ` / `ws-no-key-value: ` pair, followed by `DISPLAY SPACE`.
The program ends with one more `DISPLAY SPACE`, so a sequential hit is followed
by two blank-ish lines and a miss by one.

## 4. Discrepancy register / rulings

Authority order: **COBOL source > compiled behaviour of GnuCOBOL 3.1.2 >
`search/README.md`**. Where the compiler resolves something the source leaves
open, the observed behaviour is the contract, and it is recorded here rather
than "fixed".

### D-001 — `SEARCH ALL` with a single key on a multi-key table
The first search declares three keys but tests only `ws-item-id-1`. GnuCOBOL
performs a binary search using only the key(s) named in the `WHEN` condition;
`id-1 = 1/2/3` each return their row, anything else reports `Item not found.`
**Ruling:** the port narrows on exactly the keys supplied, in declared key order.

### D-002 — Multi-key `SEARCH ALL` is an all-keys-must-match search
Verified matrix over the second search: `(1,101,500)`, `(2,102,499)` and
`(3,103,498)` are found; `(2,102,999)`, `(2,999,499)`, `(9,102,499)`,
`(1,101,499)`, `(1,102,500)`, `(3,103,500)` and `(2,101,499)` all report
`Item not found.` A partially matching key set never returns a "closest" row.
**Ruling:** a hit requires every supplied key to match on one occurrence; the
first unequal key decides which half of the table is discarded.

### D-003 — Mixed ASCENDING/DESCENDING keys
`ws-item-id-3` is declared `DESCENDING` while the first two keys are
`ASCENDING`, and the loaded data honours all three orderings simultaneously
(1,2,3 / 101,102,103 / 500,499,498). The narrowing comparison therefore inverts
for `ws-item-id-3`. The port implements the direction per key rather than
assuming ascending; because the data is consistently sorted, the mixed
declaration produces no divergence, which the fixture matrix confirms.

### D-004 — `SEARCH ALL` over data that is not sorted for the declared keys
The standard leaves this undefined. Both tables here are consistent with their
declarations, so the program never enters that state. A probe table
(`0002`, `0003`, `0001` with `ASCENDING KEY`) compiled with the same GnuCOBOL
shows that `SEARCH ALL` silently **misses** the out-of-order row (`0001`
reported as not found) while a sequential `SEARCH` finds it.
**Ruling:** documented as compiler behaviour, not relied upon; the port's binary
search reproduces the same miss because it is the same algorithm.

### D-005 — Sequential `SEARCH` vs binary `SEARCH ALL`
The unkeyed table is unsorted (ids 2, 3, 1) and is scanned sequentially from
`idx-2 = 1`, returning the **first** occurrence whose id matches. On this data
the two searches never disagree (all ids are distinct and every id is present),
but per D-004 they would on unsorted data.
**Ruling:** the port scans the unkeyed table in table order and returns the
first hit; it does not "optimise" it into a keyed lookup.

### D-006 — `ACCEPT` into `PIC 9(4)`
Established by differential probing (3 000 randomised inputs against a compiled
`ACCEPT`/`DISPLAY` probe, zero mismatches):

1. the input line is first truncated to the receiving field's **byte** size
   (`12345` → `1234`, i.e. the *first* four bytes, not the low-order digits);
2. leading whitespace and one optional `+`/`-` sign are consumed; the sign is
   discarded by the unsigned field (`-2` → `0002`);
3. the digits appearing before a decimal point are right-aligned in the field
   (`1 2` → `0012`, `+123` → `0123`, `1.9` → `0001`);
4. embedded spaces and `,` thousands separators are skipped (`12,3` → `0123`);
5. any other character, or a second decimal point, aborts the move and stores
   **zeros** (`a1` → `0000`, `12a45` → `0000`) — but only if it is reached:
   `1abc` → `0001`, because the field is already full;
6. blank input and end of input both leave `0000`.

**Ruling:** reproduced exactly in `app/cobol_types.accept_pic9`; no validation,
no error message, and no rejection is added on top of it. Invalid input is not
an error condition in this program — it simply searches for `0000`, which is
absent from both tables and therefore yields `Item not found.`

### D-007 — Asymmetric rules under `Record found:`
The keyed block prints 16 dashes, the sequential block 15. This is almost
certainly a typo in the original.
**Ruling:** preserved byte for byte; callers (and the parity harness) diff on it.

### D-008 — `ws-item-date` is a fixed-width string, not a date
`ws-item-date` is a group of `9(4)`, a `FILLER PIC X VALUE "/"`, `99`, another
`/` filler and `99`, and it is loaded with an alphanumeric `MOVE`
(`move "2021/01/01" to ws-item-date(1)`) that writes straight through the
fillers.
**Ruling:** modelled as a 10-byte string. It is never parsed, validated or
re-formatted as a date, and it is emitted verbatim.

### D-009 — `PIC X(n)` padding is significant
`ws-item-name` is emitted with its trailing spaces (`test item 3` + 5 spaces),
as is `ws-no-key-value` (14 chars + 11 spaces).
**Ruling:** raw padded values are used for all output and parity; the JSON view
additionally exposes a `*_trimmed` convenience field, never in place of the raw.

### D-010 — Premature end of input
If stdin ends before the fifth `ACCEPT`, the remaining fields keep their initial
value (`0000`) and the program still runs all three searches and exits 0.
**Ruling:** modelled as an empty accept line; no endpoint-level error.

### D-011 — Byte-level, not character-level, fixed width
COBOL applies field sizes to bytes. `é1` (3 bytes) truncated to a 4-byte field
behaves differently from a 2-character view of the same string.
**Ruling:** the port parses `ACCEPT` input as `bytes` throughout and the harness
compares stdout as `bytes`.

## 5. Migration surface

| COBOL construct | Migrated artefact |
| --- | --- |
| `SEARCH ALL ... WHEN ws-item-id-1(idx) = ...` | `GET /api/search/keyed?id1=` |
| `SEARCH ALL ... WHEN` all three ids | `GET /api/search/keyed-all?id1=&id2=&id3=` |
| `SEARCH ws-no-key-item-table` | `GET /api/search/sequential?id=` |
| `setup-test-data` | `app/tables.setup_test_data` behind `TableStore` |
| `display-found-item` | `app/search_program.display_found_item` |
| `ACCEPT` console session | `app/search_program.run_program` (parity oracle side) + the static UI |

Responses carry the program's own wording: `Item not found.` for a miss (an
empty result, not an HTTP error) and ` Record found:` plus the exact display
lines for a hit.
