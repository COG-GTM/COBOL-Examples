"""COBOL oracle: compiles and runs the unmodified ``search/search.cbl``.

The legacy source is copied into a scratch directory before compiling; nothing
under ``search/`` is ever written to.  The program is driven exactly as a user
would drive it — by piping the ``ACCEPT`` answers to stdin — and its full stdout
is returned as bytes for a byte-for-byte diff.
"""

from __future__ import annotations

import shutil
import subprocess
import tempfile
from pathlib import Path

LEGACY_SOURCE = Path(__file__).resolve().parents[2] / "search" / "search.cbl"
COBC = "cobc"
RUN_TIMEOUT_SECONDS = 30


class CobolToolchainMissing(RuntimeError):
    """Raised when GnuCOBOL is unavailable; parity must SKIP, never pass."""


def cobc_available() -> bool:
    return shutil.which(COBC) is not None


def run_cobol(stdin_bytes: bytes) -> bytes:
    """Run ``search-example`` over ``stdin_bytes`` and return its stdout."""
    if not cobc_available():
        raise CobolToolchainMissing("GnuCOBOL 'cobc' is not installed")

    with tempfile.TemporaryDirectory(prefix="search-parity-") as tmp:
        work = Path(tmp)
        shutil.copy2(LEGACY_SOURCE, work / LEGACY_SOURCE.name)
        # The .cbl sources are fixed-format: compile without -free.
        compile_result = subprocess.run(
            [COBC, "-x", LEGACY_SOURCE.name, "-o", "search"],
            cwd=work,
            capture_output=True,
            text=True,
            check=False,
        )
        if compile_result.returncode != 0:
            raise RuntimeError(f"cobc failed: {compile_result.stderr}")

        run_result = subprocess.run(
            ["./search"],
            cwd=work,
            input=stdin_bytes,
            capture_output=True,
            check=False,
            timeout=RUN_TIMEOUT_SECONDS,
        )
        if run_result.returncode != 0:
            raise RuntimeError(f"search-example failed: {run_result.stderr!r}")
        return run_result.stdout
