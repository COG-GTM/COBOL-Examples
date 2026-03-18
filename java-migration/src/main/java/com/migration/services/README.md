# Phase 6: Subprogram Architecture -> Service Layer

## COBOL Sources
- `sub_program/main_app.cbl` - Main application with CALL statements
- `sub_program/sub.cbl` - Subprogram with WORKING-STORAGE and LOCAL-STORAGE

## COBOL-to-Java Mapping

### Call Semantics
| COBOL | Java |
|-------|------|
| `CALL "sub-app" USING BY CONTENT ws-item-1` | `subAppService.processCall(item1, item2, true)` (copy semantics) |
| `CALL "sub-app" USING ws-item-1` (BY REFERENCE) | `subAppService.processCall(item1, item2, false)` (return modified values) |
| `CANCEL "sub-app"` | `subAppService.reset()` |

### Storage Sections
| COBOL | Java | Lifetime |
|-------|------|----------|
| WORKING-STORAGE SECTION | `@Service` singleton instance fields | Persists between calls until reset |
| LOCAL-STORAGE SECTION | Method-local variables | Fresh on each call |
| LINKAGE SECTION | Method parameters | Per-call |

### Program Structure
| COBOL | Java |
|-------|------|
| `PROGRAM-ID. sub-app` | `@Service SubAppService` |
| `PROCEDURE DIVISION USING l-item-1 l-item-2` | `processCall(String item1, String item2, boolean byContent)` |
| Main program CALL pattern | `@Service MainAppService` with dependency injection |

## Behavioral Differences

1. **BY CONTENT immutability**: In COBOL, BY CONTENT copies the value to the subprogram's linkage section. In Java, Strings are inherently immutable, so the original is never modified. The `byContent` flag controls whether the sub-service returns modified values.

2. **CANCEL semantics**: COBOL CANCEL deallocates the subprogram and reinitializes its WORKING-STORAGE on next CALL. Java `reset()` manually clears instance fields since Spring singleton beans are never destroyed during runtime.

3. **Singleton scope**: The Spring `@Service` default scope is singleton, matching COBOL's behavior where a subprogram's WORKING-STORAGE persists between CALLs. For prototype (fresh-each-time) behavior, use `@Scope("prototype")`.
