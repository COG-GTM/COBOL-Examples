"""Build the SQLite test database from sql/create_test_db.sql.

The original script targets PostgreSQL, so a few dialect differences are
translated on the fly: ``CREATE DATABASE``/``\\c`` are skipped,
``serial`` becomes ``INTEGER`` (SQLite auto-increments an INTEGER PRIMARY KEY)
and ``now()`` becomes ``CURRENT_TIMESTAMP``.
"""
import re
import sqlite3
from pathlib import Path

CREATE_DB_SCRIPT = Path(__file__).resolve().parent.parent / "sql" / "create_test_db.sql"


def postgres_to_sqlite(script: str) -> str:
    statements = []
    for statement in script.split(";"):
        stripped = statement.strip()
        if not stripped or stripped.upper().startswith("CREATE DATABASE") or stripped.startswith("\\"):
            continue
        stripped = re.sub(r"\bid\s+serial\b", "id INTEGER", stripped, flags=re.IGNORECASE)
        stripped = re.sub(r"\bnow\(\)", "CURRENT_TIMESTAMP", stripped, flags=re.IGNORECASE)
        statements.append(stripped + ";")
    return "\n".join(statements)


def connect(db_path: str = ":memory:", script_path: Path = CREATE_DB_SCRIPT) -> sqlite3.Connection:
    """Open ``db_path`` and (re)create the accounts table from the script."""
    conn = sqlite3.connect(db_path)
    # PostgreSQL's LIKE is case-sensitive; SQLite's is not by default.
    conn.execute("PRAGMA case_sensitive_like = ON")
    conn.executescript(postgres_to_sqlite(script_path.read_text()))
    return conn


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="Create the SQLite test database.")
    parser.add_argument("db_path", nargs="?", default="cobol_db_example.sqlite3")
    path = parser.parse_args().db_path
    with connect(path) as connection:
        count = connection.execute("SELECT COUNT(*) FROM accounts").fetchone()[0]
    print(f"Created {path} with {count} accounts.")
