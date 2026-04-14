# Mouse Example Migration Note

## COBOL Source
- `mouse/mouse_example.cbl` (lines 47-59)

## COBOL Mouse Handling
The original COBOL program used `COB_MOUSE_FLAGS` and `ACCEPT` with mouse position tracking via GnuCOBOL screen extensions:

```cobol
set environment "COB_MOUSE_FLAGS" to 1
accept ws-key-pressed at 0101
    with auto-return
    with no-echo
    on exception
        accept ws-mouse-row from line number
        accept ws-mouse-col from column number
        accept ws-mouse-flags from escape key
```

## Java/Spring Boot Equivalent

Mouse handling in COBOL terminal applications (`COB_MOUSE_FLAGS`, `ACCEPT` mouse position) is **not applicable** in the Java/Spring Boot migration because:

1. **Browser Mouse Events**: The web frontend (`index.html`) uses standard browser mouse events (click, hover, scroll) which are handled natively by the browser's JavaScript engine.

2. **REST API**: The Java backend communicates via HTTP REST endpoints, not terminal screen positions. User interactions are translated to API calls.

3. **No Terminal UI**: The migration replaces the ncurses-style terminal interface with a modern web-based UI. Mouse interactions are handled by the browser DOM event system.

## Summary

| COBOL Concept | Java/Web Equivalent |
|---|---|
| `COB_MOUSE_FLAGS` | Browser `addEventListener('click', ...)` |
| `ACCEPT ws-mouse-row FROM LINE NUMBER` | DOM `event.clientY` / `event.offsetY` |
| `ACCEPT ws-mouse-col FROM COLUMN NUMBER` | DOM `event.clientX` / `event.offsetX` |
| `ACCEPT ws-mouse-flags FROM ESCAPE KEY` | DOM `event.button`, `event.buttons` |
| Terminal screen coordinates | CSS pixel coordinates |

No backend Java code is needed for mouse handling — it is entirely a frontend/browser concern.
