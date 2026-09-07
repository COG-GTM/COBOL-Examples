"""COBOL oracle for ``unstring-example``.

Two oracles, both GnuCOBOL:

* :func:`run_legacy` compiles and runs the **unmodified** ``unstring/unstring.cbl``. Its
  stdout is the byte-level contract for the default fixture.
* :func:`run_probe` compiles a *generated* program that mirrors ``main-procedure``
  statement for statement with the fixture's source strings, delimiter and amount
  substituted. It is the stand-in that lets fixtures cross boundaries the hard-coded
  legacy literals never reach (empty source, leading/trailing delimiters, tokens wider
  than ``PIC X(5)``, more tokens than the ``OCCURS 6`` table). Every line of
  :data:`PROBE_TEMPLATE` is a copy of the corresponding legacy line; nothing under
  ``unstring/`` is ever written to.
"""

from __future__ import annotations

import shutil
import subprocess
import tempfile
from decimal import Decimal
from pathlib import Path

from app.cobol_types import SOURCE_LEN, to_byte_string

REPO_ROOT = Path(__file__).resolve().parents[2]
LEGACY_SOURCE = REPO_ROOT / "unstring" / "unstring.cbl"
COBC = "cobc"


class CobolToolchainMissing(RuntimeError):
    """Raised when GnuCOBOL is unavailable; parity must SKIP, never pass."""


def cobc_available() -> bool:
    return shutil.which(COBC) is not None


def _compile_and_run(source_text: str, name: str) -> bytes:
    if not cobc_available():
        raise CobolToolchainMissing("GnuCOBOL 'cobc' is not installed")
    with tempfile.TemporaryDirectory(prefix="unstring-parity-") as tmp:
        work = Path(tmp)
        cbl = work / f"{name}.cbl"
        # One Python character is one byte, as in the rest of the port.
        cbl.write_bytes(source_text.encode("latin-1"))
        # The sources are fixed-format: compile without -free.
        compiled = subprocess.run(
            [COBC, "-x", cbl.name, "-o", name],
            cwd=work,
            capture_output=True,
            text=True,
            check=False,
        )
        if compiled.returncode != 0:
            raise RuntimeError(f"cobc failed: {compiled.stderr}")
        run = subprocess.run([f"./{name}"], cwd=work, capture_output=True, check=False)
        if run.returncode != 0:
            raise RuntimeError(f"{name} failed: {run.stderr!r}")
        return run.stdout


def run_legacy() -> bytes:
    """Run the unmodified legacy program and return its stdout bytes."""
    return _compile_and_run(LEGACY_SOURCE.read_text(encoding="utf-8"), "unstring")


def cobol_literal(value: str) -> str:
    """Render ``value`` as a COBOL alphanumeric literal (``"`` doubled)."""
    return '"' + value.replace('"', '""') + '"'


def source_operand(value: str) -> str:
    """Operand for ``move ... to ws-source-str`` in the probe.

    The literal is pre-truncated to the 30 bytes the ``PIC X(30)`` receiving field
    holds — exactly what the ``MOVE`` itself would do — so the generated statement
    always fits inside fixed-format column 72 and needs no continuation line. An empty
    source becomes ``spaces``, since COBOL has no zero-length literal.
    """
    truncated = to_byte_string(value)[:SOURCE_LEN]
    if not truncated:
        return "spaces"
    return cobol_literal(truncated)


def probe_source(
    simple_source: str,
    multi_delim_source: str,
    multi_dest_source: str,
    delimiter: str,
    amount: Decimal,
) -> str:
    """Generated COBOL mirroring ``main-procedure`` with the fixture's inputs."""
    return PROBE_TEMPLATE.format(
        delimiter=delimiter,
        simple=source_operand(simple_source),
        multi_delim=source_operand(multi_delim_source),
        multi_dest=source_operand(multi_dest_source),
        # Full precision: the truncation to PIC $999,999.99 must be COBOL's, not ours.
        amount=f"{amount:f}",
    )


def run_probe(
    simple_source: str,
    multi_delim_source: str,
    multi_dest_source: str,
    delimiter: str,
    amount: Decimal,
) -> bytes:
    return _compile_and_run(
        probe_source(simple_source, multi_delim_source, multi_dest_source, delimiter, amount),
        "unstring_probe",
    )


#: Fixed-format COBOL. Mirrors ``unstring/unstring.cbl`` line for line; only the four
#: literals moved into working storage are parameterised.
PROBE_TEMPLATE = """\
      ******************************************************************
      * generated parity probe - mirrors unstring/unstring.cbl
      ******************************************************************
       identification division.
       program-id. unstring-probe.
       data division.
       file section.
       working-storage section.

       01  ws-source-str                  pic x(30).

       01  ws-dest-str.
           05  ws-part-1                  pic x(15).
           05  ws-part-2                  pic x(15).


       01  ws-delimiter                   pic x value '{delimiter}'.

       01  ws-single-stats.
           05  ws-single-fields-filled    pic 99.
           05  ws-single-dest-info.
               10  ws-single-dest-str     pic x(5).
               10  ws-single-delimiter    pic x.
               10  ws-single-char-count   pic 9.

       01  ws-multi-stats.
           05  ws-multi-fields-filled     pic 99.
           05  ws-multi-dest-info         occurs 6 times
                                          indexed by ws-multi-idx.
               10  ws-multi-dest-str      pic x(5).
               10  ws-multi-delimiter     pic x.
               10  ws-multi-char-count    pic 9.

       01  ws-pointer                     pic 9(5) comp.

       01  ws-source-num                  pic $999,999.99.
       01  ws-dest-num                    pic 999 occurs 3 times.

       procedure division.

       main-procedure.

           move {simple} to ws-source-str

           display spaces
           display "================================================="
           display "EX 1 : SIMPLE UNSTRING"
           display space
           display "SOURCE STRING: " ws-source-str

           unstring ws-source-str
               delimited by space
               into ws-part-1 ws-part-2
           end-unstring

           display "PART1: " ws-part-1
           display "PART2: " ws-part-2

           move 1 to ws-pointer

           display spaces
           display "================================================="
           display "EX 2 : UNSTRING MULTIPLE TIMES INTO SAME DEST."

           display space
           display "SOURCE STRING: " ws-source-str

           perform 2 times
               unstring ws-source-str delimited by all spaces
                   into ws-part-1
                   with pointer ws-pointer
                   on overflow
                       display "ERROR: OVERFLOW"
                   not on overflow
                       display "Successfully unstrung."
               end-unstring

               display "PART VALUE: " ws-part-1
               display "POINTER: " ws-pointer
           end-perform

           display spaces
           display "================================================="
           display "EX 3 : UNSTRING INTO EXPLICIT FIELDS"

           move 1 to ws-pointer

           display space
           display "SOURCE STRING: " ws-source-str

           unstring ws-source-str delimited by all spaces
               into ws-part-1 ws-part-2
               with pointer ws-pointer
               on overflow
                   display "ERROR: OVERFLOW"
               not on overflow
                   display "Successfully unstrung."
           end-unstring

           display "PART1: " ws-part-1
           display "PART2: " ws-part-2
           display "POINTER: " ws-pointer

           display spaces
           display "================================================="
           display "EX 4 : UNSTRING WITH MULTIPLE DELIMITERS "

           move 1 to ws-pointer

           move {multi_delim} to ws-source-str

           display space
           display "SOURCE STRING: " ws-source-str

           perform until ws-pointer > function length(ws-source-str)

               unstring ws-source-str
                   delimited by all "<" or ">" or "!" or ws-delimiter
                   into
                       ws-single-dest-str
                           delimiter in ws-single-delimiter
                           count in ws-single-char-count
                   with pointer ws-pointer
                   tallying in ws-single-fields-filled
               end-unstring

               display space
               display "VALUE: " ws-single-dest-str
               display "DELIMITER: " ws-single-delimiter
               display "CHAR COUNT:" ws-single-char-count
               display "CURRENT POINTER: " ws-pointer
               display "TOTAL FIELDS FILLED: " ws-single-fields-filled
               display "-------------------------------------------"
           end-perform

           display spaces
           display "================================================="
           display "EX 5 : UNSTRING WITH MULTIPLE DELIMITERS " &
               "INTO MULTIPLE DESTINATIONS"

           move {multi_dest} to ws-source-str

           display space
           display "SOURCE STRING: " ws-source-str

           unstring ws-source-str
               delimited by
                   all "<"
                   or all ">"
                   or "!"
                   or ws-delimiter
               into
                   ws-multi-dest-str(1)
                       delimiter in ws-multi-delimiter(1)
                       count in ws-multi-char-count(1)
                   ws-multi-dest-str(2)
                       delimiter in ws-multi-delimiter(2)
                       count in ws-multi-char-count(2)
                   ws-multi-dest-str(3)
                       delimiter in ws-multi-delimiter(3)
                       count in ws-multi-char-count(3)
                   ws-multi-dest-str(4)
                       delimiter in ws-multi-delimiter(4)
                       count in ws-multi-char-count(4)
                   ws-multi-dest-str(5)
                       delimiter in ws-multi-delimiter(5)
                       count in ws-multi-char-count(5)
                   ws-multi-dest-str(6)
                       delimiter in ws-multi-delimiter(6)
                       count in ws-multi-char-count(6)
               tallying in ws-multi-fields-filled
           end-unstring

           perform varying ws-multi-idx
           from 1 by 1 until ws-multi-idx > 6
               display space
               display "STRING NUMBER: " ws-multi-idx
               display "VALUE: " ws-multi-dest-str(ws-multi-idx)
               display "DELIMITER: " ws-multi-delimiter(ws-multi-idx)
               display "CHAR COUNT:" ws-multi-char-count(ws-multi-idx)
               display "-------------------------------------------"
           end-perform

           display "TOTALS: "
           display "FIELDS FILLED: " ws-multi-fields-filled

           display spaces
           display "================================================="
           display "EX 6 : UNSTRING FORMATTED NUMBER"
           display space

           move {amount} to ws-source-num
           display "SOURCE VALUE: " ws-source-num

           unstring ws-source-num(2:)
               delimited by ',' or '.'
               into ws-dest-num(1)
                   ws-dest-num(2)
                   ws-dest-num(3)
           end-unstring

           display "PART 1: " ws-dest-num(1)
           display "PART 2: " ws-dest-num(2)
           display "PART 3: " ws-dest-num(3)
           display space

           goback.

       end program unstring-probe.
"""
