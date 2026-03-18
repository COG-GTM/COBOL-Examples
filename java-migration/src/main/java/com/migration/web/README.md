# Phase 7: UI Layer (Replace Screen Mode)

## COBOL Sources
- `accept/accept.cbl` - ACCEPT verb examples (input modes)
- `screen_size/screen_size.cbl` - Terminal screen size detection
- `display_test/display_test.cbl` - DISPLAY output examples
- `mouse/mouse_example.cbl` - Mouse input handling

## COBOL-to-Java Mapping

### Input/Output
| COBOL | Java/REST |
|-------|-----------|
| `ACCEPT ws-input` | HTTP request body (POST/PUT) |
| `DISPLAY "text"` | HTTP response body (JSON) |
| `ACCEPT ws-menu-choice` + `EVALUATE` | Separate REST endpoints per action |
| Menu loop (`PERFORM FOREVER`) | Stateless REST API (client decides what to call) |

### Intentionally Dropped Features
The following COBOL screen-mode features have no REST API equivalent and are intentionally not migrated:

| COBOL Feature | Reason |
|---------------|--------|
| `ACCEPT ... AT yyxx` (screen coordinates) | Web UI handles layout via CSS |
| `ACCEPT ... TIMEOUT 3` | HTTP has its own timeout mechanisms |
| `ACCEPT ... AUTO-SKIP` | Client-side form validation |
| `ACCEPT ... NO-ECHO` / `SECURE` | HTTPS + client-side input masking |
| `ACCEPT ... UPPER` | Server-side string conversion or client-side |
| `CBL_GET_SCR_SIZE` (screen size) | Responsive web design handles this |
| Mouse event handling | Standard browser mouse events |
| ncurses/pdcurses dependency | Not needed for web UI |

### REST Endpoints

**AccountController** (`/api/accounts`):
- `GET /` - List all accounts (replaces menu choice 1: display-all-accounts)
- `GET /?enabled=N` - List disabled accounts (replaces menu choice 2)
- `GET /?search=term` - Search accounts (replaces menu choice 3: query-accounts)
- `GET /{id}` - Get single account
- `POST /` - Create account (replaces ACCEPT input fields)
- `PUT /{id}` - Update account
- `DELETE /{id}` - Delete account

**CustomerController** (`/api/customers`):
- `GET /` - List all customer records
- `GET /{id}` - Search by ID (binary search)
- `POST /person` - Create person customer
- `POST /corporate` - Create corporate customer

**ReportController** (`/api/reports`):
- `GET /customers` - Generate text report
- `GET /customers?format=csv` - Generate CSV report
