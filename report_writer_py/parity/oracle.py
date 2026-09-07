"""COBOL oracle: compiles and runs the unmodified ``report_test.cbl``.

The legacy source is copied read-only into a scratch directory (the program reads
``input.txt`` and writes ``report.txt`` from its working directory); nothing under
``report_writer/`` is ever written to.
"""

from __future__ import annotations

import shutil
import subprocess
import tempfile
from pathlib import Path

LEGACY_SOURCE = Path(__file__).resolve().parents[2] / "report_writer" / "report_test.cbl"
COBC = "cobc"


class CobolToolchainMissing(RuntimeError):
    """Raised when GnuCOBOL is unavailable; parity must SKIP, never pass."""


def cobc_available() -> bool:
    return shutil.which(COBC) is not None


def run_cobol(input_text: str) -> bytes:
    """Run the COBOL program over ``input_text`` and return the produced report bytes."""
    if not cobc_available():
        raise CobolToolchainMissing("GnuCOBOL 'cobc' is not installed")

    with tempfile.TemporaryDirectory(prefix="report-parity-") as tmp:
        work = Path(tmp)
        shutil.copy2(LEGACY_SOURCE, work / LEGACY_SOURCE.name)
        binary = work / "report_test"
        compile_result = subprocess.run(
            [COBC, "-x", LEGACY_SOURCE.name, "-o", binary.name],
            cwd=work,
            capture_output=True,
            text=True,
            check=False,
        )
        if compile_result.returncode != 0:
            raise RuntimeError(f"cobc failed: {compile_result.stderr}")

        (work / "input.txt").write_bytes(input_text.encode("utf-8"))
        run_result = subprocess.run(
            [f"./{binary.name}"], cwd=work, capture_output=True, text=True, check=False
        )
        if run_result.returncode != 0:
            raise RuntimeError(f"report-test failed: {run_result.stderr}")
        return (work / "report.txt").read_bytes()
