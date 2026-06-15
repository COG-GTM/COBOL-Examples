from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Application configuration loaded from environment variables.

    Replaces the hard-coded ODBC connection string from the COBOL source
    (ws-db-connection-string in sql_example.cbl).
    """

    database_url: str = "postgresql+asyncpg://localhost/cobol_db_example"

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")


settings = Settings()
