#!/usr/bin/env python3
"""Port of sql/generated_sql_ex.cbl: the esqlOC *precompiled* SQL program.

Where ``sql_example.py`` uses sqlite3 directly, this port keeps the shape of
the generated code: an ``SQLCA`` status block, prepared statement constants
(``SQL_STMT_0..2``) and explicit runtime calls that mirror the OCSQL
library (``ocsql`` connect, ``ocsqlocu`` open cursor, ``ocsqlftc`` fetch,
``ocsqlccu`` close cursor, ``ocsqldis`` disconnect). Each call updates SQLCA
and the program checks ``SQLSTATE``/``SQLCODE`` exactly like the COBOL.
"""
import argparse
import sqlite3
import sys
from dataclasses import dataclass

import sql_db

SQL_SUCCESS = "00000"
SQL_NODATA = "02000"

SQL_STMT_0 = "SELECT ID,FIRST_NAME,LAST_NAME,PHONE,ADDRESS,IS_ENABLED,CREATE_DT,MOD_DT FROM ACCOUNTS ORDER BY ID;"
SQL_STMT_1 = (
    "SELECT ID,FIRST_NAME,LAST_NAME,PHONE,ADDRESS,IS_ENABLED,CREATE_DT,MOD_DT FROM ACCOUNTS "
    "WHERE IS_ENABLED = 'N' ORDER BY ID;"
)
SQL_STMT_2 = (
    "SELECT ID,FIRST_NAME,LAST_NAME,PHONE,ADDRESS,IS_ENABLED,CREATE_DT,MOD_DT FROM ACCOUNTS "
    "WHERE FIRST_NAME LIKE ? OR LAST_NAME LIKE ? OR PHONE LIKE ? OR ADDRESS LIKE ? ORDER BY ID;"
)

# SQLV host-variable lengths for the 8 fetched columns (PIC X sizes).
FETCH_LENGTHS = (None, 8, 8, 10, 22, 1, 20, 20)


@dataclass
class SQLCA:
    sqlstate: str = SQL_SUCCESS
    sqlcode: int = 0
    sqlerrmc: str = ""

    def ok(self, nodata: bool = False) -> None:
        self.sqlstate, self.sqlcode, self.sqlerrmc = (SQL_NODATA, 100, "") if nodata else (SQL_SUCCESS, 0, "")

    def error(self, exc: sqlite3.Error) -> None:
        self.sqlstate = "HY000"
        self.sqlcode = -getattr(exc, "sqlite_errorcode", 1)
        self.sqlerrmc = str(exc)

    @property
    def is_success(self) -> bool:
        return self.sqlstate == SQL_SUCCESS

    @property
    def is_nodata(self) -> bool:
        return self.sqlstate == SQL_NODATA


@dataclass
class SQLStatement:
    sql: str
    cursor: sqlite3.Cursor = None


class OCSQL:
    """Minimal stand-in for the esqlOC runtime library."""

    def __init__(self, sqlca: SQLCA):
        self.sqlca = sqlca
        self.conn = None

    def ocsql(self, db_path: str) -> None:
        try:
            self.conn = sql_db.connect(db_path)
            self.sqlca.ok()
        except sqlite3.Error as exc:
            self.sqlca.error(exc)

    def ocsqlocu(self, stmt: SQLStatement, params=()) -> None:
        try:
            stmt.cursor = self.conn.execute(stmt.sql, params)
            self.sqlca.ok()
        except sqlite3.Error as exc:
            self.sqlca.error(exc)

    def ocsqlftc(self, stmt: SQLStatement):
        try:
            row = stmt.cursor.fetchone()
        except sqlite3.Error as exc:
            self.sqlca.error(exc)
            return None
        self.sqlca.ok(nodata=row is None)
        if row is None:
            return None
        return tuple(
            value if size is None else str(value or "")[:size].ljust(size)
            for value, size in zip(row, FETCH_LENGTHS)
        )

    def ocsqlccu(self, stmt: SQLStatement) -> None:
        if stmt.cursor is not None:
            stmt.cursor.close()
            stmt.cursor = None
        self.sqlca.ok()

    def ocsqldis(self) -> None:
        if self.conn is not None:
            self.conn.close()
            self.conn = None
        self.sqlca.ok()


class SqlExample:
    def __init__(self, db_path: str):
        self.db_path = db_path
        self.sqlca = SQLCA()
        self.runtime = OCSQL(self.sqlca)
        self.is_connected = False
        self.stmts = [SQLStatement(SQL_STMT_0), SQLStatement(SQL_STMT_1), SQLStatement(SQL_STMT_2)]
        self.accounts = []

    def check_sql_state(self) -> None:
        if self.sqlca.is_success or self.sqlca.is_nodata:
            return
        print(" ")
        print("SQL Error:")
        print(f"SQLCODE: {self.sqlca.sqlcode}")
        print(f"SQLSTATE: {self.sqlca.sqlstate}")
        if self.sqlca.sqlerrmc:
            print("ERROR MESSAGE: " + self.sqlca.sqlerrmc)
        print(" ")
        if self.is_connected:
            self.runtime.ocsqldis()
        sys.exit(1)

    def fetch_all(self, stmt: SQLStatement, params=()) -> None:
        self.runtime.ocsqlocu(stmt, params)
        self.check_sql_state()
        self.accounts = []
        while True:  # PERFORM WITH TEST AFTER UNTIL SQLCODE = 100
            row = self.runtime.ocsqlftc(stmt)
            self.check_sql_state()
            if not self.sqlca.is_nodata:
                self.accounts.append(row)
            if self.sqlca.sqlcode == 100:
                break
        self.runtime.ocsqlccu(stmt)
        self.check_sql_state()
        self.display_account_results()

    def display_account_results(self) -> None:
        print(" ")
        print("ACCOUNTS:")
        print(" ")
        print(" ID   | First    | Last     | Phone      | Address                | Enabled ")
        print("------|----------|----------|------------|------------------------|---------")
        for acc_id, first, last, phone, address, enabled, _create, _mod in self.accounts:
            print(f"{acc_id % 100000:05d} | {first} | {last} | {phone} | {address} | {enabled}")

    def query_accounts(self) -> None:
        search_again = True
        while search_again:
            print(" ")
            search_string = read_line("Enter search value: ")[:48]
            search_value_text = f"%{search_string.strip(' ')}%"[:50]
            search_value_len = len(search_value_text)  # FUNCTION STORED-CHAR-LENGTH
            print("Search value: " + search_value_text.ljust(50))
            print(f"Length: +{search_value_len:05d}")
            self.fetch_all(self.stmts[2], (search_value_text,) * 4)
            print(" ")
            search_again = read_line("Search again? (Y/[N]) ")[:1].upper() == "Y"

    def run(self) -> int:
        print(" ")
        print("COBOL SQL DB Example Program")
        print("----------------------------")
        print(" ")
        self.runtime.ocsql(self.db_path)
        self.check_sql_state()
        self.is_connected = True

        while True:
            print(" ")
            print("1) Display all accounts")
            print("2) Display disabled accounts")
            print("3) Query accounts")
            print("4) Exit")
            choice = read_line("Selection: ")[:1]
            if choice == "1":
                self.fetch_all(self.stmts[0])
            elif choice == "2":
                self.fetch_all(self.stmts[1])
            elif choice == "3":
                self.query_accounts()
            elif choice == "4":
                break
            else:
                print("Please make a selection between 1-4")

        self.runtime.ocsqldis()
        print("Disconnected.")
        print(" ")
        return 0


def read_line(prompt: str) -> str:
    try:
        return input(prompt)
    except EOFError:
        raise SystemExit(0)


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--db", default=":memory:", help="SQLite file to (re)create (default: in memory)")
    return SqlExample(parser.parse_args(argv).db).run()


if __name__ == "__main__":
    raise SystemExit(main())
