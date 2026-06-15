from datetime import datetime

from sqlalchemy import DateTime, Integer, String, func
from sqlalchemy.orm import Mapped, mapped_column

from .db import Base


class Account(Base):
    """ORM model mapped to the existing ``accounts`` table.

    Replaces the WORKING-STORAGE SECTION record definitions
    (ws-account-record) from sql_example.cbl.
    """

    __tablename__ = "accounts"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    first_name: Mapped[str] = mapped_column(String, nullable=False)
    last_name: Mapped[str] = mapped_column(String, nullable=False)
    phone: Mapped[str] = mapped_column(String, nullable=False)
    address: Mapped[str] = mapped_column(String, nullable=False)
    is_enabled: Mapped[str] = mapped_column(
        String(1), nullable=False, server_default="N", default="N"
    )
    create_dt: Mapped[datetime] = mapped_column(
        DateTime, server_default=func.now()
    )
    mod_dt: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())
