# Python ports of the COBOL examples

Every GnuCOBOL example in this repository has a Python 3 port here. The
original `.cbl` files are unchanged so you can compare the two. The ports use
only the standard library (Python 3.9+), and every script runs on its own:

```bash
python3 python/<name>.py
```

Each port keeps the COBOL program's structure (paragraphs become functions)
and its output. For fixed-width `PIC X` fields the port pads or truncates the
text the same way COBOL does, so most programs print byte-for-byte the same
output as the GnuCOBOL build.

## Program map

| Original COBOL program | Python port | Python features used | Run (example) |
|---|---|---|---|
| `merge_sort/merge_sort_test.cbl` | `merge_sort.py` | dataclass fixed-width records, `sorted()` for MERGE/SORT | `python3 python/merge_sort.py [--work-dir out/]` |
| `unstring/unstring.cbl` | `unstring.py` | `str.split`, plus an `unstring()` helper for DELIMITER IN / COUNT IN / POINTER / TALLYING / OVERFLOW | `python3 python/unstring.py` |
| `trim/trim.cbl` | `trim.py` | `str.strip/lstrip/rstrip(" ")` | `python3 python/trim.py` |
| `numval_test/numval_test.cbl` | `numval_test.py` | regex + `decimal.Decimal` NUMVAL, `float` for COMP-2 | `printf '12.5\n7\n' \| python3 python/numval_test.py` |
| `is_numeric/is_numeric.cbl` | `is_numeric.py` | regex `[0-9]+` (stricter than `str.isdigit`) | `python3 python/is_numeric.py` |
| `search/search.cbl` | `search.py` | `bisect` for SEARCH ALL, generator for SEARCH | `python3 python/search.py` |
| `redifines/redefines.cbl` | `redefines.py` | `bytearray` record + field views, `struct` for COMP-2 | `python3 python/redefines.py` |
| `comp_test/comp_test.cbl` | `comp_test.py` | `struct` (COMP binary) vs ASCII digits (DISPLAY), PIC Z editing | `python3 python/comp_test.py [--explain]` |
| `json_generate/json_generate.cbl` | `json_generate.py` | `json.dumps` | `python3 python/json_generate.py` |
| `xml_generate/xml_generate.cbl` | `xml_generate.py` | `xml.etree.ElementTree` | `python3 python/xml_generate.py` |
| `sql/sql_example.cbl` | `sql_example.py` | `sqlite3` with named parameters | `python3 python/sql_example.py [--db file.sqlite3]` |
| `sql/generated_sql_ex.cbl` | `generated_sql_ex.py` | `sqlite3` behind a small OCSQL-style runtime (`SQLCA`, `ocsqlocu`, `ocsqlftc`, ...) | `python3 python/generated_sql_ex.py` |
| `sql/create_test_db.sql` | `sql_db.py` (helper) | runs the SQL script against SQLite | `python3 python/sql_db.py [file.sqlite3]` |
| `accept/accept.cbl` | `accept.py` | `input()`, then curses for TIMEOUT / AUTO-SKIP / NO-ECHO / UPPER | `python3 python/accept.py` |
| `accept/accept-secure.cbl` | `accept_secure.py` | curses field that shows `*` (falls back to `getpass`) | `python3 python/accept_secure.py` |
| `accept/accept_from.cbl` | `accept_from.py` | `sys.argv`, `os.environ`, `datetime`, `getpass.getuser`, curses/`shutil.get_terminal_size` | `python3 python/accept_from.py a b` |
| `display_test/display-test.cbl` | `display_test.py` | curses positioning, colour, `clrtoeol`, `beep` | `python3 python/display_test.py` |
| `display_timing/display_timing.cbl` | `display_timing.py` | curses + `time.perf_counter` | `python3 python/display_timing.py [--runs N --refreshes N]` |
| `read_command_args/read_cmd_line_args.cbl` | `read_cmd_line_args.py` | `sys.argv` | `python3 python/read_cmd_line_args.py --test` |
| `read_command_args/read_specific_cmd_line_args.cbl` | `read_specific_cmd_line_args.py` | `sys.argv` | `python3 python/read_specific_cmd_line_args.py a b c` |
| `screen_size/get_screen_size.cbl` | `screen_size.py` | curses `getmaxyx`/`LINES`/`COLS`, `shutil.get_terminal_size` | `python3 python/screen_size.py` |
| `mouse/mouse_example.cbl` | `mouse_example.py` | curses mouse events | `python3 python/mouse_example.py` |
| `sub_program/main_app.cbl` | `main_app.py` | calls a function in a separate module | `python3 python/main_app.py` |
| `sub_program/sub.cbl` | `sub_app.py` (helper) | module globals = WORKING-STORAGE, `importlib.reload` = CANCEL | called from `main_app.py` |
| `report_writer/report_test.cbl` | `report_writer.py` | f-strings for a 66-line page | `python3 python/report_writer.py [--output report.txt]` |

`cobol_screen.py` holds shared curses helpers (1-based `AT line/column`,
COBOL colour numbers, an `ACCEPT` field with TIMEOUT/AUTO-SKIP/NO-ECHO/SECURE/UPPER).

Some directories have more than one program, and a few names would clash with
Python modules (`sub` → `sub_app`). That's why there are more files here than
COBOL directories.

## Behavioural differences

**Numbers**
- COBOL `PIC 9`/`PIC S9V99` fields are fixed-point decimals. Where precision
  matters the ports use `decimal.Decimal` (for example NUMVAL in
  `numval_test.py`) and only convert to `float` where the COBOL program uses
  `COMP-2`. A `float` is never used for a value that COBOL keeps as a decimal.
- Moving a number into a `PIC 9(n)` field drops the high-order digits. The
  ports do the same with `% 10**n` (see `pic_9()` helpers).
- `COMP-2` values are printed with Python's shortest round-trip `repr`, which
  matches GnuCOBOL for the values in these examples.
- GnuCOBOL's `NUMVAL` returns 0 for invalid input; `numval()` does too, rather
  than raising `ValueError` the way `float()` would.

**Text and records**
- `FUNCTION TRIM` removes spaces only. The ports call `strip(" ")`, not
  `strip()`, which would also remove tabs and newlines.
- `redefines.py` writes the shared bytes directly, so the COMP-2-over-`PIC X`
  example prints the same raw (mostly unprintable) bytes that COBOL prints.
- `merge_sort.py` writes its work files to a temporary directory unless you
  pass `--work-dir`. The COBOL program writes them to the current directory.

**Data formats**
- `json_generate.py` builds the JSON GnuCOBOL 3.2 produces (trailing spaces
  trimmed, an all-space field becomes `" "`). The GnuCOBOL 3.1 build used to
  check these ports was compiled without JSON support, so the COBOL program
  printed nothing there.
- `xml_generate.py` output matches GnuCOBOL's character for character,
  including `SUPPRESS WHEN SPACES` and the `enabled` attribute.

**SQL**
- The COBOL programs use PostgreSQL through esqlOC/ODBC. The ports use
  SQLite: `sql_db.py` runs `sql/create_test_db.sql` and converts the
  PostgreSQL-only parts (`CREATE DATABASE`, `\c`, `serial`, `now()`).
  The database is in memory by default; use `--db` to keep it in a file.
- `PRAGMA case_sensitive_like = ON` makes `LIKE` case-sensitive, as it is in
  PostgreSQL.
- `generated_sql_ex.py` copies the structure of the esqlOC-generated code
  (the `SQLCA` status, statement constants and the
  connect/open/fetch/close/disconnect calls) on top of `sqlite3`. Error codes
  are SQLite's, not ODBC's.

**Terminal programs (curses)**
- These ports need a real terminal for screen mode: `accept.py`,
  `accept_secure.py`, `accept_from.py` (last part), `display_test.py`,
  `display_timing.py`, `screen_size.py` and `mouse_example.py`. They use
  `curses`, which ships with Python on Linux and macOS. On Windows, install the
  `windows-curses` package.
- With no TTY (a pipe or CI), or when `COBOL_DEMO_NO_CURSES=1` is set, they
  switch to a plain line-mode version of the same steps so they still finish.
  `mouse_example.py` prints a message and exits in that case.
- `mouse_example.py` needs a terminal that reports mouse motion (xterm-style,
  `?1003h`). Q or Esc quits.
- `display_timing.py` times with `time.perf_counter()` (sub-microsecond).
  COBOL's `ACCEPT FROM TIME` only has 1/100 s resolution. The terminal must be
  at least 80x22.
- `accept_from.py` returns `exception status` as `0000`, because Python has no
  equivalent of the COBOL exception register.

**Sub-programs**
- `CALL ... BY CONTENT` is done by passing a copy of the mutable field holder
  (`PicX`). `BY REFERENCE` passes the caller's object itself. `CANCEL` reloads
  the module, which resets its WORKING-STORAGE globals.

## Smoke tests

```bash
python/run_all.sh                 # stdlib only
python3 -m pytest python/tests    # needs pytest; also checks key output lines
```

Both run every port with canned stdin and `COBOL_DEMO_NO_CURSES=1`, and check
that each one exits with status 0. The pytest suite also fails if a new script
is added without a smoke case.
