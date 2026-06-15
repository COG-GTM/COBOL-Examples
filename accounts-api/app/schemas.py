from datetime import datetime

from pydantic import BaseModel, ConfigDict


class AccountBase(BaseModel):
    first_name: str
    last_name: str
    phone: str
    address: str
    is_enabled: str = "N"


class AccountCreate(AccountBase):
    """Payload for POST /accounts."""


class AccountUpdate(BaseModel):
    """Payload for PUT /accounts/{id}; all fields optional."""

    first_name: str | None = None
    last_name: str | None = None
    phone: str | None = None
    address: str | None = None
    is_enabled: str | None = None


class AccountResponse(AccountBase):
    id: int
    create_dt: datetime | None = None
    mod_dt: datetime | None = None

    model_config = ConfigDict(from_attributes=True)


class AccountListResponse(BaseModel):
    total: int
    accounts: list[AccountResponse]
