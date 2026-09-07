"""In-memory document store for completed runs.

The COBOL program keeps its state in files on the batch machine; the service keeps it in a
bounded LRU map behind this small adapter, so swapping in a real document store later is a
matter of implementing :class:`RunStore`.
"""

from __future__ import annotations

import uuid
from collections import OrderedDict
from typing import Protocol

from app.merge_sort import MergeSortRun

DEFAULT_CAPACITY = 32


class RunStore(Protocol):
    """The persistence surface the service depends on."""

    def put(self, run: MergeSortRun) -> str: ...

    def get(self, run_id: str) -> MergeSortRun | None: ...

    def __len__(self) -> int: ...


class InMemoryRunStore:
    """LRU store: bounded so a stream of requests cannot grow memory without limit."""

    def __init__(self, capacity: int = DEFAULT_CAPACITY) -> None:
        if capacity < 1:
            raise ValueError("capacity must be at least 1")
        self._capacity = capacity
        self._runs: OrderedDict[str, MergeSortRun] = OrderedDict()

    def put(self, run: MergeSortRun) -> str:
        run_id = uuid.uuid4().hex
        self._runs[run_id] = run
        self._runs.move_to_end(run_id)
        while len(self._runs) > self._capacity:
            self._runs.popitem(last=False)
        return run_id

    def get(self, run_id: str) -> MergeSortRun | None:
        run = self._runs.get(run_id)
        if run is not None:
            self._runs.move_to_end(run_id)
        return run

    def __len__(self) -> int:
        return len(self._runs)
