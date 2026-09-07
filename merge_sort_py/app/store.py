"""In-memory document store for completed runs.

The COBOL program keeps its state in files on the batch machine; the service keeps it in a
bounded LRU map behind this small adapter, so swapping in a real document store later is a
matter of implementing :class:`RunStore`.
"""

from __future__ import annotations

import threading
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
    """LRU store: bounded so a stream of requests cannot grow memory without limit.

    The batch program was single threaded; the service is not. FastAPI dispatches these
    synchronous handlers onto a thread pool, so each compound ``OrderedDict`` operation runs
    under one lock — a lookup that raced an eviction would otherwise ``move_to_end`` a key that
    had just been dropped and raise ``KeyError``.
    """

    def __init__(self, capacity: int = DEFAULT_CAPACITY) -> None:
        if capacity < 1:
            raise ValueError("capacity must be at least 1")
        self._capacity = capacity
        self._runs: OrderedDict[str, MergeSortRun] = OrderedDict()
        self._lock = threading.Lock()

    def put(self, run: MergeSortRun) -> str:
        run_id = uuid.uuid4().hex
        with self._lock:
            self._runs[run_id] = run
            self._runs.move_to_end(run_id)
            while len(self._runs) > self._capacity:
                self._runs.popitem(last=False)
        return run_id

    def get(self, run_id: str) -> MergeSortRun | None:
        with self._lock:
            run = self._runs.get(run_id)
            if run is not None:
                self._runs.move_to_end(run_id)
            return run

    def __len__(self) -> int:
        with self._lock:
            return len(self._runs)
