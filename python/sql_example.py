#!/usr/bin/env python3
"""Port of sql/sql_example.cbl: query an accounts table through a menu.

The embedded-SQL cursors become parameterised ``sqlite3`` queries. The
database is built from ``sql/create_test_db.sql`` (in memory by default).
"""
import argparse
import sqlite3
import sys

import sql_db

COLUMNS = "ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT"
ACCOUNT_ALL_CUR = f"SELECT {COLUMNS} FROM ACCOUNTS ORDER BY ID"
ACCOUNT_DISABLED_CUR = f"SELECT {COLUMNS} FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID"
ACCOUNT_QUERY_CUR = (
    f"SELECT {COLUMNS} FROM ACCOUNTS WHERE FIRST_NAME LIKE :search OR LAST_NAME LIKE :search"
    " OR PHONE LIKE :search OR ADDRESS LIKE :search ORDER BY ID"
)


def pic_x(value, size: int) -> str:
    return str(value if value is not None else "")[:size].ljust(size)


def read_line(prompt: str) -> str:
    try:
        return input(prompt)
    except EOFError:
        raise SystemExit(0)


def display_account_results(rows) -> None:
    print(" ")
    print("ACCOUNTS:")
    print(" ")
    print(" ID   | First    | Last     | Phone      | Address                | Enabled ")
    print("------|----------|----------|------------|------------------------|---------")
    # Values are moved into the fixed-width PIC X fields of ws-account-record.
    for acc_id, first, last, phone, address, enabled, *_ in rows:
        print(
            f"{acc_id % 100000:05d} | {pic_x(first, 8)} | {pic_x(last, 8)} | "
            f"{pic_x(phone, 10)} | {pic_x(address, 22)} | {pic_x(enabled, 1)}"
        )


def check_sql_state(error: sqlite3.Error, conn) -> None:
    print(" ")
    print("SQL Error:")
    print(f"SQLCODE: {getattr(error, 'sqlite_errorcode', -1)}")
    print(f"SQLSTATE: {getattr(error, 'sqlite_errorname', type(error).__name__)}")
    print(f"ERROR MESSAGE: {error}")
    print(" ")
    if conn is not None:
        conn.close()
    sys.exit(1)


def fetch(conn, sql: str, params=()) -> list:
    try:
        return conn.execute(sql, params).fetchall()
    except sqlite3.Error as error:
        check_sql_state(error, conn)


def query_accounts(conn) -> None:
    search_again = True
    while search_again:
        print(" ")
        search_string = read_line("Enter search value: ")[:48]
        search_value = f"%{search_string.strip(' ')}%"
        print("Search value: " + pic_x(search_value, 50))
        print(f"Length: +{len(search_value):05d}")
        display_account_results(fetch(conn, ACCOUNT_QUERY_CUR, {"search": search_value}))
        print(" ")
        search_again = read_line("Search again? (Y/[N]) ")[:1].upper() == "Y"


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--db", default=":memory:", help="SQLite file to (re)create (default: in memory)")
    args = parser.parse_args(argv)

    print(" ")
    print("COBOL SQL DB Example Program")
    print("----------------------------")
    print(" ")

    try:
        conn = sql_db.connect(args.db)
    except sqlite3.Error as error:
        check_sql_state(error, None)

    while True:
        print(" ")
        print("1) Display all accounts")
        print("2) Display disabled accounts")
        print("3) Query accounts")
        print("4) Exit")
        choice = read_line("Selection: ")[:1]
        if choice == "1":
            display_account_results(fetch(conn, ACCOUNT_ALL_CUR))
        elif choice == "2":
            display_account_results(fetch(conn, ACCOUNT_DISABLED_CUR))
        elif choice == "3":
            query_accounts(conn)
        elif choice == "4":
            break
        else:
            print("Please make a selection between 1-4")

    conn.close()
    print("Disconnected.")
    print(" ")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
