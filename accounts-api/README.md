# Accounts API

A modernized REST API for the legacy COBOL accounts management application.

This service is a Python + FastAPI + PostgreSQL port of the terminal-based,
menu-driven COBOL CRUD program. The original COBOL source lives in
[`sql/sql_example.cbl`](../sql/sql_example.cbl) and the original database setup
script is [`sql/create_test_db.sql`](../sql/create_test_db.sql) in the parent
repository.

## What changed (COBOL → FastAPI)

| COBOL construct | Modern equivalent |
|---|---|
| `main-procedure` / menu loop | FastAPI application + routers (`app/main.py`) |
| `CONNECT` / `DISCONNECT` paragraphs | SQLAlchemy async engine + `get_db` dependency (`app/db.py`) |
| `WORKING-STORAGE SECTION` record | SQLAlchemy ORM model (`app/models.py`) |
| `display-all-accounts` / `query-accounts` | `GET /accounts` with filters/pagination |
| Cursor-based search | `GET /accounts/search?q=...` (SQL `ILIKE`) |
| `check-sql-state` / `SQLCA` error handling | `HTTPException` + HTTP status codes |
| Hard-coded ODBC connection string | `DATABASE_URL` env var (`app/config.py`) |

## Project layout

```
accounts-api/
├── app/
│   ├── main.py            # FastAPI app, CORS, lifespan, router include
│   ├── config.py          # pydantic-settings config (DATABASE_URL)
│   ├── db.py              # async engine, session factory, get_db dependency
│   ├── models.py          # SQLAlchemy ORM Account model
│   ├── schemas.py         # Pydantic request/response schemas
│   └── routes/
│       └── accounts.py    # /accounts CRUD + search endpoints
├── migrations/
│   └── 001_create_accounts.sql   # schema + 11 seed rows
├── tests/
│   └── test_accounts.py
├── requirements.txt
├── Dockerfile
└── docker-compose.yml
```

## Database setup

The schema and seed data are in
[`migrations/001_create_accounts.sql`](migrations/001_create_accounts.sql)
(ported from the original `sql/create_test_db.sql`).

Create the database and apply the migration:

```bash
createdb cobol_db_example
psql -d cobol_db_example -f migrations/001_create_accounts.sql
```

## Install dependencies

```bash
pip install -r requirements.txt
```

## Configuration

Configuration is read from environment variables (see `.env.example`):

- `DATABASE_URL` — defaults to `postgresql+asyncpg://localhost/cobol_db_example`

```bash
export DATABASE_URL="postgresql+asyncpg://postgres:password@localhost:5432/cobol_db_example"
```

## Run the server

```bash
uvicorn app.main:app --reload
```

Interactive API docs are then available at http://localhost:8000/docs.

## Run with Docker

```bash
docker compose up --build
```

This starts PostgreSQL (with the migration auto-applied) and the API on
http://localhost:8000.

## API endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/accounts` | List accounts (filter by `first_name`, `last_name`, `is_enabled`; paginate with `skip`, `limit`) |
| `GET` | `/accounts/{id}` | Get a single account |
| `POST` | `/accounts` | Create an account |
| `PUT` | `/accounts/{id}` | Update an account |
| `DELETE` | `/accounts/{id}` | Delete an account |
| `GET` | `/accounts/search?q=...` | Search across name, phone, address (ILIKE) |
| `GET` | `/health` | Health check |

### Example curl commands

List all accounts:

```bash
curl http://localhost:8000/accounts
```

Filter enabled accounts by last name:

```bash
curl "http://localhost:8000/accounts?last_name=Tester&is_enabled=Y"
```

Get one account:

```bash
curl http://localhost:8000/accounts/1
```

Create an account:

```bash
curl -X POST http://localhost:8000/accounts \
  -H "Content-Type: application/json" \
  -d '{"first_name":"Ada","last_name":"Lovelace","phone":"15555550199","address":"1 Analytical Way","is_enabled":"Y"}'
```

Update an account:

```bash
curl -X PUT http://localhost:8000/accounts/1 \
  -H "Content-Type: application/json" \
  -d '{"address":"456 Updated Rd"}'
```

Delete an account:

```bash
curl -X DELETE http://localhost:8000/accounts/1
```

Search accounts:

```bash
curl "http://localhost:8000/accounts/search?q=Tester"
```

## Run tests

```bash
pytest
```

Tests use an in-memory SQLite database via `httpx.AsyncClient`, so no running
PostgreSQL instance is required.
