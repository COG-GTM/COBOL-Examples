import pytest
import pytest_asyncio
from httpx import ASGITransport, AsyncClient
from sqlalchemy.ext.asyncio import async_sessionmaker, create_async_engine

from app.db import Base, get_db
from app.main import app

TEST_DATABASE_URL = "sqlite+aiosqlite:///:memory:"


@pytest_asyncio.fixture
async def client():
    engine = create_async_engine(TEST_DATABASE_URL, future=True)
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

    session_factory = async_sessionmaker(engine, expire_on_commit=False)

    async def override_get_db():
        async with session_factory() as session:
            yield session

    app.dependency_overrides[get_db] = override_get_db

    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as ac:
        yield ac

    app.dependency_overrides.clear()
    await engine.dispose()


def _sample_payload(**overrides):
    payload = {
        "first_name": "John",
        "last_name": "Tester",
        "phone": "15555550100",
        "address": "123 Fake St, Nowhere",
        "is_enabled": "Y",
    }
    payload.update(overrides)
    return payload


@pytest.mark.asyncio
async def test_list_accounts_returns_list(client):
    resp = await client.get("/accounts")
    assert resp.status_code == 200
    body = resp.json()
    assert "accounts" in body
    assert "total" in body
    assert isinstance(body["accounts"], list)


@pytest.mark.asyncio
async def test_create_account(client):
    resp = await client.post("/accounts", json=_sample_payload())
    assert resp.status_code == 201
    body = resp.json()
    assert body["id"] is not None
    assert body["first_name"] == "John"
    assert body["create_dt"] is not None
    assert body["mod_dt"] is not None


@pytest.mark.asyncio
async def test_get_account(client):
    created = (await client.post("/accounts", json=_sample_payload())).json()
    resp = await client.get(f"/accounts/{created['id']}")
    assert resp.status_code == 200
    assert resp.json()["id"] == created["id"]
    assert resp.json()["last_name"] == "Tester"


@pytest.mark.asyncio
async def test_update_account_bumps_mod_dt(client):
    created = (await client.post("/accounts", json=_sample_payload())).json()
    resp = await client.put(
        f"/accounts/{created['id']}", json={"address": "999 New Ave"}
    )
    assert resp.status_code == 200
    body = resp.json()
    assert body["address"] == "999 New Ave"
    assert body["mod_dt"] >= created["mod_dt"]


@pytest.mark.asyncio
async def test_delete_account(client):
    created = (await client.post("/accounts", json=_sample_payload())).json()
    resp = await client.delete(f"/accounts/{created['id']}")
    assert resp.status_code == 204
    assert (await client.get(f"/accounts/{created['id']}")).status_code == 404


@pytest.mark.asyncio
async def test_search_accounts(client):
    await client.post("/accounts", json=_sample_payload(last_name="Tester"))
    await client.post(
        "/accounts", json=_sample_payload(first_name="Mike", last_name="Tester1")
    )
    resp = await client.get("/accounts/search", params={"q": "Tester"})
    assert resp.status_code == 200
    body = resp.json()
    assert body["total"] >= 2
    assert len(body["accounts"]) >= 2


@pytest.mark.asyncio
async def test_get_missing_account_returns_404(client):
    assert (await client.get("/accounts/999999")).status_code == 404


@pytest.mark.asyncio
async def test_update_missing_account_returns_404(client):
    resp = await client.put("/accounts/999999", json={"address": "x"})
    assert resp.status_code == 404


@pytest.mark.asyncio
async def test_delete_missing_account_returns_404(client):
    assert (await client.delete("/accounts/999999")).status_code == 404
