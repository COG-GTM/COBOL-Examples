"""Smoke tests: every Python port runs standalone and exits 0.

Run with ``python3 -m pytest python/tests``.
"""
import os
import subprocess
import sys
from pathlib import Path

import pytest

PYTHON_DIR = Path(__file__).resolve().parent.parent
HELPER_MODULES = {"cobol_screen.py", "sql_db.py", "sub_app.py"}

# script -> (arguments, stdin, text expected in stdout)
CASES = {
    "accept.py": ([], "hello\n\nt1\nabcdefghijklmnop\nsecret\nupper\n", "You entered: UPPER"),
    "accept_from.py": (["one", "two"], "console\n\n", "accept from argument-value: two"),
    "accept_secure.py": ([], "pw123\n", "You entered: pw123"),
    "comp_test.py": (["--explain"], "123\n", "COMP    storage of 123: 00 7b"),
    "display_test.py": ([], "", "    hello world"),
    "display_timing.py": (["--runs", "2", "--refreshes", "5"], "\n", "LINE/COLUMN:"),
    "generated_sql_ex.py": ([], "1\n2\n3\nTester1\nn\n4\n", "00011 | Richard  | Tester10"),
    "is_numeric.py": ([], "123\n42\n 42 \n", "0000000042 is numeric!"),
    "json_generate.py": ([], "", '{"ws-record":{"name":"Test Name"'),
    "main_app.py": ([], "hello\nworld\n", "Main app: replace1  replace2"),
    "merge_sort.py": ([], "", "Sorting merged file on descending contract id"),
    "mouse_example.py": ([], "", "needs an interactive terminal"),
    "numval_test.py": ([], "12.5-\n7\n", "Total: -5.5"),
    "read_cmd_line_args.py": (["--test", "foo"], "", "You entered the '--test' cmd arg!"),
    "read_specific_cmd_line_args.py": (["alpha", "beta"], "", "beta"),
    "redefines.py": ([], "", "Company name: no-name corp"),
    "report_writer.py": (["--output", "{tmp}/report.txt"], "", "Terminate report."),
    "screen_size.py": ([], "\n" * 6, "Done."),
    "search.py": ([], "2\n2\n102\n499\n3\n", "ws-no-key-value: Value of id 3."),
    "sql_example.py": ([], "1\n2\n3\nTester1\nn\n4\n", "00009 | Bill     | Tester8"),
    "trim.py": ([], "", "--hello world--"),
    "unstring.py": ([], "", "TOTAL FIELDS FILLED: 08"),
    "xml_generate.py": ([], "", '<ws-record enabled="true"><name>Test Name</name>'),
}


def test_every_script_has_a_case():
    scripts = {p.name for p in PYTHON_DIR.glob("*.py")} - HELPER_MODULES
    assert scripts == set(CASES)


@pytest.mark.parametrize("script", sorted(CASES))
def test_script_runs(script, tmp_path):
    args, stdin, expected = CASES[script]
    args = [a.replace("{tmp}", str(tmp_path)) for a in args]
    env = dict(os.environ, COBOL_DEMO_NO_CURSES="1")
    result = subprocess.run(
        [sys.executable, str(PYTHON_DIR / script), *args],
        check=False, input=stdin, capture_output=True, text=True, errors="replace", timeout=120, cwd=tmp_path, env=env,
    )
    assert result.returncode == 0, result.stderr
    assert expected in result.stdout


def test_report_matches_cobol_layout(tmp_path):
    subprocess.run(
        [sys.executable, str(PYTHON_DIR / "report_writer.py"), "--output", str(tmp_path / "r.txt")],
        check=True, capture_output=True,
    )
    lines = (tmp_path / "r.txt").read_text().splitlines()
    assert len(lines) == 66  # PAGE LIMIT 66
    assert lines[0].strip() == "Customer Order Report"
    assert lines[1].rstrip().endswith("PAGE   1")
    assert lines[5] == "   003345     Test Name2               PHY   12"
