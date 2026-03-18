# Phase 2: Data Structures

## COBOL Sources
- `redifines/redefines.cbl` - REDEFINES field overlay examples
- `search/search.cbl` - SEARCH and SEARCH ALL table lookup
- `comp_test/comp_test.cbl` - COMP type conversion tests

## COBOL-to-Java Mapping

### REDEFINES -> Polymorphism
COBOL `REDEFINES` allows multiple data names to occupy the same memory. This is replaced by Java sealed interfaces with record implementations.

| COBOL | Java |
|-------|------|
| `ws-customer-type PIC 9` with 88-level conditions | `sealed interface Customer` |
| `ws-customer-type-person VALUE 1` | `PersonCustomer record` |
| `ws-customer-type-corp VALUE 2` | `CorporateCustomer record` |
| `ws-corp-name REDEFINES ws-customer-name` | Separate record fields |
| `IF ws-customer-type-person(idx)` | `switch (customer) { case PersonCustomer p -> ... }` |

### SEARCH -> Collections
| COBOL | Java |
|-------|------|
| `SEARCH ALL` (binary search, sorted indexed table) | `TreeMap.get()` / `Collections.binarySearch()` |
| `SEARCH` (sequential search, no sorting required) | Linear scan / `List.stream().filter()` |
| `AT END` (not found) | `Optional.empty()` |
| `WHEN key = value` | `TreeMap.get(key)` |
| `INDEXED BY idx` | `Map` key or list index |

### COMP Types -> Java Primitives
| COBOL | Java | Notes |
|-------|------|-------|
| `PIC 9(n) COMP` | `int` / `long` | Binary integer, 32/64-bit |
| `COMP-2` | `double` | 64-bit IEEE 754 (~15-17 significant digits) |
| `COMP-3` | `BigDecimal` | Packed decimal, arbitrary precision |
| `PIC 9(n)V9(m)` | `BigDecimal` | Fixed-point decimal |
| `PIC ZZ9` (dynamic display) | `String.format()` | Leading zeros suppressed |

## Behavioral Differences

1. **REDEFINES memory overlay**: COBOL allows reading the same bytes as different types. In the example, setting `ws-corp-name` on a person-type record produces garbage in the first/last name fields. Java's type system prevents this class of bugs entirely.

2. **SEARCH ALL requires sorted data**: COBOL SEARCH ALL only works on tables declared with ASCENDING/DESCENDING KEY. Java's `TreeMap` maintains sort order automatically.

3. **COMP precision**: COBOL COMP-2 maps to IEEE 754 double, which can lose precision. For financial calculations, use `BigDecimal` (COMP-3 equivalent).
