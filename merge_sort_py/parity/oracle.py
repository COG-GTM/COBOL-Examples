"""COBOL oracles for the parity harness.

Two oracles are used, both compiled with GnuCOBOL from *unmodified* sources:

``run_cobol_program()``
    Compiles and runs ``merge_sort/merge_sort_test.cbl`` byte for byte as it sits in the
    legacy tree. It generates its own inputs, so it pins exactly one fixture — but it pins
    it against the real program, with no stand-in anywhere in the loop.

``run_cobol_with_inputs()``
    Compiles and runs ``parity/cobol/merge_sort_oracle.cbl``, a **stand-in**: it is the
    legacy program with ``create-test-data`` removed so the two input files can be
    supplied. Every other line — the FD/SD layouts, the MERGE, the SORT, the status guards,
    the display loops — is copied from the legacy source statement for statement. It exists
    only because the real program hard-codes its own inputs and therefore cannot be driven
    over fixtures. ``test_oracle_matches_legacy_program`` asserts the stand-in reproduces
    the real program's output byte for byte on the program's own data.

Neither oracle ever writes into the legacy tree: the source is copied into a scratch
directory and compiled and run there.
"""

from __future__ import annotations

import shutil
import subprocess
import tempfile
from dataclasses import dataclass
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
LEGACY_SOURCE = REPO_ROOT / "merge_sort" / "merge_sort_test.cbl"
STANDIN_SOURCE = Path(__file__).resolve().parent / "cobol" / "merge_sort_oracle.cbl"
COBC = "cobc"


class CobolToolchainMissing(RuntimeError):
    """Raised when GnuCOBOL is unavailable; parity must SKIP, never pass."""


@dataclass(frozen=True)
class CobolResult:
    """Raw bytes produced by a COBOL run."""

    console: bytes
    test_file_1: bytes
    test_file_2: bytes
    merge_output: bytes
    sorted_contract_id: bytes


def cobc_available() -> bool:
    return shutil.which(COBC) is not None


def _read(path: Path) -> bytes:
    return path.read_bytes() if path.exists() else b""


def _compile_and_run(source: Path, work: Path) -> bytes:
    shutil.copy2(source, work / source.name)
    binary = source.stem
    compiled = subprocess.run(
        [COBC, "-x", source.name, "-o", binary],
        cwd=work,
        capture_output=True,
        text=True,
        check=False,
    )
    if compiled.returncode != 0:
        raise RuntimeError(f"cobc failed for {source.name}: {compiled.stderr}")

    executed = subprocess.run([f"./{binary}"], cwd=work, capture_output=True, check=False)
    if executed.returncode != 0:
        raise RuntimeError(f"{binary} failed: {executed.stderr!r}")
    return executed.stdout


def _run(source: Path, inputs: tuple[bytes, bytes] | None) -> CobolResult:
    if not cobc_available():
        raise CobolToolchainMissing("GnuCOBOL 'cobc' is not installed")

    with tempfile.TemporaryDirectory(prefix="merge-sort-parity-") as tmp:
        work = Path(tmp)
        if inputs is not None:
            (work / "test-file-1.txt").write_bytes(inputs[0])
            (work / "test-file-2.txt").write_bytes(inputs[1])
        console = _compile_and_run(source, work)
        return CobolResult(
            console=console,
            test_file_1=_read(work / "test-file-1.txt"),
            test_file_2=_read(work / "test-file-2.txt"),
            merge_output=_read(work / "merge-output.txt"),
            sorted_contract_id=_read(work / "sorted-contract-id.txt"),
        )


def run_cobol_program() -> CobolResult:
    """Run the unmodified legacy program, which generates its own inputs."""
    return _run(LEGACY_SOURCE, None)


def run_cobol_with_inputs(test_file_1: bytes, test_file_2: bytes) -> CobolResult:
    """Run the stand-in oracle over caller-supplied input files."""
    return _run(STANDIN_SOURCE, (test_file_1, test_file_2))
