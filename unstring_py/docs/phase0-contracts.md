# Phase 0 — Contracts for `unstring-example`

Legacy source of truth: `unstring/unstring.cbl` (program-id `unstring-example`, GnuCOBOL,
fixed format). Nothing under `unstring/` is modified by this migration; the COBOL remains
the specification and the system of record.

Authority order used for every ruling below:

1. **Copybook / record layout** — none exist for this program; the `WORKING-STORAGE
   SECTION` of the program plays that role and is treated as layer 1.
2. **Program source** — `main-procedure`, the six `EXAMPLE n` blocks.
3. **Documentation** — the in-source comments and `unstring/README.md`.
4. **Observed behaviour of the compiled binary** — used only to *disambiguate* a rule the
   source leaves open (e.g. what GnuCOBOL stores in a `PIC 9` `COUNT IN` for a 12-character
   token). Every such case is recorded as a numbered ruling and pinned by the byte-level
   parity harness.

## 1. Inventory

| Artefact | Kind | Notes |
| --- | --- | --- |
| `unstring/unstring.cbl` | batch console program | the entire pipeline: no files, no DB2, no BMS maps, no JCL |
| `unstring/README.md` | documentation | describes the example, no additional contract |

There is no file section, no CICS commarea, and no persisted state: the program reads no
input, runs six `UNSTRING` demonstrations over hard-coded literals, and writes 148 lines to
stdout. **stdout is therefore the record layout** — the parity oracle diffs it byte for
byte (§6).

## 2. Working-storage → Python representation

| COBOL field | PIC | Bytes | Python representation | Notes |
| --- | --- | --- | --- | --- |
| `ws-source-str` | `X(30)` | 30 | `str` of exactly 30 latin-1 chars | space-padded, never trimmed (`move_to_byte_source`) |
| `ws-dest-str` / `ws-part-1`, `ws-part-2` | `X(15)` ×2 | 30 | `str` of exactly 15 | group item is the two parts concatenated |
| `ws-delimiter` | `X value '|'` | 1 | `str` length 1 | runtime delimiter, used in examples 4 and 5 |
| `ws-single-fields-filled` | `99` | 2 | `int` 0–99 | display-numeric; wraps modulo 100 on store |
| `ws-single-dest-str` | `X(5)` | 5 | `str` of exactly 5 | truncating destination |
| `ws-single-delimiter` | `X` | 1 | `str` length 1 | `DELIMITER IN` |
| `ws-single-char-count` | `9` | 1 | `str` of one digit | `COUNT IN`, see D-004 |
| `ws-multi-fields-filled` | `99` | 2 | `int` 0–99 | `TALLYING IN` for example 5 |
| `ws-multi-dest-info` | `OCCURS 6` | 6×7 | `list[MultiEntry]` fixed length 6 | never grown, see D-005 |
| `ws-pointer` | `9(5) COMP` | 2 (binary) | `int` | binary, not display — see D-008 |
| `ws-source-num` | `$999,999.99` | 10 | `Decimal` + rendered `str` | numeric-edited, see D-006/D-007 |
| `ws-dest-num` | `999 OCCURS 3` | 3×3 | `list[str]` of 3 digits | zero-filled, right-justified |

Byte discipline: COBOL applies these widths to **bytes**. All input is round-tripped
through `text.encode("utf-8").decode("latin-1")` (`app.cobol_types.to_byte_string`) before
any slicing, and the transcript is compared as `bytes`, so multibyte input lines up exactly
as GnuCOBOL sees it. `PIC X(n)` values are never `.strip()`ed before being re-emitted;
trimmed copies exist only in the JSON views.

Money: `ws-source-num` is held as `decimal.Decimal` and serialised on the wire as a JSON
**string**. No float appears anywhere in the port.

## 3. Output geometry

Every `DISPLAY` emits one line, and operands are concatenated with **no** separator, so the
label literals carry their own spacing. The exact literals matter and are reproduced
verbatim, including the quirks:

- `"CHAR COUNT:"` has **no** trailing space, unlike every other label.
- `"EX 4 : UNSTRING WITH MULTIPLE DELIMITERS "` has a **trailing** space.
- `display spaces` emits a line of spaces that GnuCOBOL renders as a single blank; the port
  emits one space character, which is what the binary writes.
- `PIC 9(5) COMP` displays as five zero-padded digits (`POINTER: 00012`).
- `PIC 99` displays as two zero-padded digits.
- An `INDEXED BY` index displays as a signed 10-digit value: `STRING NUMBER: +000000001`.

## 4. The six examples (read path)

| # | COBOL block | Endpoint | What it demonstrates |
| --- | --- | --- | --- |
| 1 | EXAMPLE 1 | `GET /api/examples/1` | plain `DELIMITED BY SPACE` into two `X(15)` fields |
| 2 | EXAMPLE 2 | `GET /api/examples/2` | `WITH POINTER` re-entered twice, `ON OVERFLOW` |
| 3 | EXAMPLE 3 | `GET /api/examples/3` | enough receivers ⇒ no overflow; final pointer value |
| 4 | EXAMPLE 4 | `GET /api/examples/4` | four delimiters, `DELIMITER IN` / `COUNT IN` / `TALLYING IN` in a loop |
| 5 | EXAMPLE 5 | `GET /api/examples/5` | one statement into the `OCCURS 6` table |
| 6 | EXAMPLE 6 | `GET /api/examples/6` | reference-modified numeric-edited source `ws-source-num(2:)` |

`GET /api/program` returns all six plus the full transcript; `POST /api/runs` stores a run
in a bounded LRU store and `GET /api/runs/{id}` retrieves it. All endpoints are pure
functions of their parameters; nothing writes to disk or to the legacy tree.

## 5. Discrepancy register / rulings

**D-001 — stdout is the contract, not the field values.** The program has no callers and no
output records, so parity is defined as byte-identical stdout, not as a field-level compare
of working storage. Authority: program source (there is no copybook to compare against).

**D-002 — the examples share one working storage, and that leakage is preserved.** Example 3
displays `ws-part-2` left over from example 1 when its own `UNSTRING` fills only one
receiver; example 4's loop starts from `ws-single-fields-filled` as it stood after the
previous iteration because `TALLYING IN` **accumulates** rather than resets. The port runs
all six examples over a single `WorkingStorage` instance rather than re-initialising per
example, so per-example endpoints run the whole program and slice the transcript.
Authority: program source.

**D-003 — `TALLYING IN` accumulates.** COBOL adds the number of receivers filled to the
existing contents of the tallying field; it does not assign. Example 4 relies on this to
count across loop iterations. Authority: program source + observed binary.

**D-004 — `COUNT IN` is stored into `PIC 9`, so counts above 9 wrap.** A token of 12
examined characters displays as `2`, not `12`, because the count is `MOVE`d into a
one-digit display field. This looks like a bug and is preserved: it is the observable
contract, and callers reading `CHAR COUNT:` see the wrapped digit. Fixture
`count_wraps_past_nine` pins it. Authority: layout (`ws-single-char-count pic 9`) over
intuition.

**D-005 — table overflow drops tokens silently; destination overflow truncates.** With more
tokens than the `OCCURS 6` table holds, the seventh onwards are never stored and the
statement raises the overflow condition (which examples 4/5 do not trap, so nothing is
printed). A token longer than `PIC X(5)` is truncated to its first five bytes while
`COUNT IN` reports the *examined* length (subject to D-004). Both are preserved; the UI
flags them with the program's own wording (`ERROR: OVERFLOW`) and an explicit
"truncated to PIC X(5)" annotation, and the JSON exposes `truncated` / `table_overflow`
rather than dropping the information. Fixtures `token_longer_than_dest` and
`more_tokens_than_table` pin them. Authority: layout + observed binary.

**D-006 — the amount truncates toward zero, it does not round.** `MOVE 123456.12 TO
ws-source-num` stores into `$999,999.99`; a `MOVE`/`COMPUTE` without `ROUNDED` truncates
excess low-order digits toward zero. `987654.999` therefore displays as `$987,654.99`, not
`$987,655.00`. The port uses `Decimal` with `ROUND_DOWN` (`move_to_source_num`) and the
seeded-regression test swaps it for rounding to prove the gate catches it. Authority:
COBOL standard `MOVE` semantics + program source (no `ROUNDED`), confirmed by fixture
`amount_truncates_cents`.

**D-007 — high-order digits beyond `$999,999.99` are dropped, not reported.** Storing
`91234567.89` yields `$234,567.89`. This is standard high-order truncation on a `MOVE`; it
is preserved and pinned by fixture `amount_overflows_picture`.

**D-008 — `PIC 9(5) COMP` is binary storage, display is the conversion.** The pointer is a
two-byte binary integer internally; `DISPLAY ws-pointer` converts it to five zero-padded
digits. The port carries it as `int` and formats on display (`display_unsigned`). The
representational difference is invisible in stdout but is recorded because a later
persistence layer must not assume five bytes.

**D-009 — a pointer starting beyond the source raises overflow and transfers nothing.**
`WITH POINTER` greater than the field length makes the statement an immediate overflow: no
receiver is modified (so the previous contents remain displayed) and the pointer is left
unchanged. Example 2's second pass relies on the pointer having advanced past `Hello World`
inside the 30-byte field. Authority: program source + observed binary.

**D-010 — empty tokens are real tokens.** Adjacent delimiters, a leading delimiter, and a
trailing delimiter each yield an empty receiver (all spaces) with `COUNT IN` = 0 and
`DELIMITER IN` set to the delimiter that ended it. A source with no delimiter at all fills
one receiver with the whole (truncated) source and leaves `DELIMITER IN` as spaces.
Fixtures `leading_delimiter`, `trailing_delimiter`, `adjacent_delimiters`, `no_delimiter`.
Authority: observed binary.

**D-011 — `ALL <delim>` collapses runs, a bare delimiter does not.** Example 4 declares
`ALL "<"` but plain `">"`, `"!"`, and `ws-delimiter`, so `>>` produces an empty token while
`<<` does not. The distinction is reproduced per-delimiter rather than globally.
Authority: program source.

**D-012 — the generated probe is a stand-in for parameterised fixtures.** The legacy program
takes no input, so every non-default fixture is run against a *generated* `.cbl` that is the
legacy `main-procedure` with the five literals substituted, compiled with the same
`cobc -x` (fixed format, never `-free`). The **default** fixture runs the unmodified
`unstring/unstring.cbl` itself. The probe is a stand-in and is documented as such: it is
generated from the legacy source's statements line for line, and the default fixture is what
proves the probe mirrors the real program.

## 6. Parity definition

- Oracle: `cobc -x <source>.cbl -o <bin>` then run and capture stdout as `bytes`.
- Compare: full stdout, byte for byte, no normalisation of whitespace or line endings.
- Exit codes: `0` clean, `1` any diff, `2` `cobc` unavailable (skip — never a pass).
- Fixtures cross: no delimiter, leading/trailing/adjacent delimiters, token longer than the
  destination, count above one digit, more tokens than the table holds, empty source,
  full-width source, source longer than `PIC X(30)`, a runtime delimiter other than `|`,
  zero amount, truncated cents, an amount exceeding the picture, the maximum amount, and the
  unmodified legacy default.

## 7. Not decided here

Security posture (authentication, IdP, authorisation model, secret management) is **not**
decided by this migration; see *Blockers before cutover* in the PR description.
