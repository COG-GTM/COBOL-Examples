"""The run store's LRU behaviour and the concurrency the batch program never had."""

from __future__ import annotations

import threading

from app.merge_sort import run_program
from app.store import InMemoryRunStore


def test_capacity_must_be_positive():
    try:
        InMemoryRunStore(capacity=0)
    except ValueError as exc:
        assert "at least 1" in str(exc)
    else:  # pragma: no cover - the constructor must reject this
        raise AssertionError("capacity 0 was accepted")


def test_oldest_run_is_evicted_and_a_read_refreshes_recency():
    store = InMemoryRunStore(capacity=2)
    first, second = store.put(run_program()), store.put(run_program())

    assert store.get(first) is not None  # first is now the most recently used
    third = store.put(run_program())

    assert store.get(second) is None
    assert store.get(first) is not None
    assert store.get(third) is not None


def test_lookups_racing_evictions_never_raise():
    """``get`` used to ``move_to_end`` a key a concurrent ``put`` had just evicted."""
    store = InMemoryRunStore(capacity=4)
    run_ids = [store.put(run_program()) for _ in range(4)]
    stop = threading.Event()
    failures: list[BaseException] = []

    def writer() -> None:
        try:
            while not stop.is_set():
                store.put(run_program())
        except BaseException as exc:  # pragma: no cover - only on a regression
            failures.append(exc)

    def reader() -> None:
        try:
            for _ in range(2_000):
                for run_id in run_ids:
                    store.get(run_id)
        except BaseException as exc:  # pragma: no cover - only on a regression
            failures.append(exc)

    threads = [threading.Thread(target=writer), *(threading.Thread(target=reader) for _ in range(3))]
    for thread in threads[1:]:
        thread.start()
    threads[0].start()
    for thread in threads[1:]:
        thread.join()
    stop.set()
    threads[0].join()

    assert failures == []
    assert len(store) == 4
