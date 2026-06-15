from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .db import engine
from .routes import accounts


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Manage the DB connection pool lifecycle.

    Replaces the COBOL main-procedure CONNECT (startup) and DISCONNECT
    (shutdown) paragraphs.
    """
    yield
    await engine.dispose()


app = FastAPI(
    title="Accounts API",
    description=(
        "Modernized REST API for the COBOL accounts management application "
        "(originally sql/sql_example.cbl)."
    ),
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(accounts.router)


@app.get("/health", tags=["health"])
async def health() -> dict[str, str]:
    return {"status": "ok"}
