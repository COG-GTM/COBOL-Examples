# COBOL Examples 
This is a collection of example and test COBOL programs I've written. I'm currently in the process of updating 
each folder with a README.md file and more comments so that the examples are easier to follow along with.


All program were written using [GnuCOBOL](https://gnucobol.sourceforge.io/) in Linux.

## Python ports

Each example also has a Python 3 port in [`python/`](python/), written with
the standard library only. The original `.cbl` files are unchanged. Run any
port directly:

```bash
python3 python/merge_sort.py
python3 python/sql_example.py      # SQLite, built from sql/create_test_db.sql
python/run_all.sh                  # smoke test: runs every port, checks exit 0
```

[`python/README.md`](python/README.md) maps each COBOL program to its Python
port and lists the behaviour differences: `Decimal` vs fixed-point `PIC` fields,
SQLite in place of PostgreSQL, and the curses programs that need a real terminal.
