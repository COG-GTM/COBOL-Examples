from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import func, or_, select
from sqlalchemy.ext.asyncio import AsyncSession

from ..db import get_db
from ..models import Account
from ..schemas import (
    AccountCreate,
    AccountListResponse,
    AccountResponse,
    AccountUpdate,
)

router = APIRouter(prefix="/accounts", tags=["accounts"])


def _now() -> datetime:
    # Naive UTC: the accounts table uses TIMESTAMP WITHOUT TIME ZONE.
    return datetime.now(timezone.utc).replace(tzinfo=None)


@router.get("", response_model=AccountListResponse)
async def list_accounts(
    db: AsyncSession = Depends(get_db),
    first_name: str | None = None,
    last_name: str | None = None,
    is_enabled: str | None = None,
    skip: int = Query(0, ge=0),
    limit: int = Query(100, ge=1, le=1000),
) -> AccountListResponse:
    """List accounts with optional filtering and pagination.

    Replaces the COBOL ``display-all-accounts`` and ``query-accounts``
    paragraphs.
    """
    conditions = []
    if first_name is not None:
        conditions.append(Account.first_name == first_name)
    if last_name is not None:
        conditions.append(Account.last_name == last_name)
    if is_enabled is not None:
        conditions.append(Account.is_enabled == is_enabled)

    count_stmt = select(func.count()).select_from(Account)
    list_stmt = select(Account).order_by(Account.id)
    if conditions:
        count_stmt = count_stmt.where(*conditions)
        list_stmt = list_stmt.where(*conditions)

    total = (await db.execute(count_stmt)).scalar_one()
    result = await db.execute(list_stmt.offset(skip).limit(limit))
    accounts = result.scalars().all()
    return AccountListResponse(total=total, accounts=list(accounts))


@router.get("/search", response_model=AccountListResponse)
async def search_accounts(
    q: str = Query(..., min_length=1),
    db: AsyncSession = Depends(get_db),
    skip: int = Query(0, ge=0),
    limit: int = Query(100, ge=1, le=1000),
) -> AccountListResponse:
    """Search accounts across name, phone, and address fields using ILIKE.

    Replaces the COBOL cursor-based search.
    """
    pattern = f"%{q}%"
    search_filter = or_(
        Account.first_name.ilike(pattern),
        Account.last_name.ilike(pattern),
        Account.phone.ilike(pattern),
        Account.address.ilike(pattern),
    )

    total = (
        await db.execute(
            select(func.count()).select_from(Account).where(search_filter)
        )
    ).scalar_one()
    result = await db.execute(
        select(Account)
        .where(search_filter)
        .order_by(Account.id)
        .offset(skip)
        .limit(limit)
    )
    accounts = result.scalars().all()
    return AccountListResponse(total=total, accounts=list(accounts))


@router.get("/{account_id}", response_model=AccountResponse)
async def get_account(
    account_id: int, db: AsyncSession = Depends(get_db)
) -> Account:
    account = await db.get(Account, account_id)
    if account is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Account {account_id} not found",
        )
    return account


@router.post("", response_model=AccountResponse, status_code=status.HTTP_201_CREATED)
async def create_account(
    payload: AccountCreate, db: AsyncSession = Depends(get_db)
) -> Account:
    now = _now()
    account = Account(
        **payload.model_dump(),
        create_dt=now,
        mod_dt=now,
    )
    db.add(account)
    await db.commit()
    await db.refresh(account)
    return account


@router.put("/{account_id}", response_model=AccountResponse)
async def update_account(
    account_id: int,
    payload: AccountUpdate,
    db: AsyncSession = Depends(get_db),
) -> Account:
    account = await db.get(Account, account_id)
    if account is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Account {account_id} not found",
        )

    updates = payload.model_dump(exclude_unset=True)
    for field, value in updates.items():
        setattr(account, field, value)
    account.mod_dt = _now()

    await db.commit()
    await db.refresh(account)
    return account


@router.delete("/{account_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_account(
    account_id: int, db: AsyncSession = Depends(get_db)
) -> None:
    account = await db.get(Account, account_id)
    if account is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Account {account_id} not found",
        )
    await db.delete(account)
    await db.commit()
