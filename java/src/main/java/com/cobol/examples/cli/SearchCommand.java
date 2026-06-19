package com.cobol.examples.cli;

import com.cobol.examples.core.search.SearchService;
import com.cobol.examples.core.search.SearchService.Item;
import com.cobol.examples.ui.ConsoleView;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "search", description = "Binary and sequential table search.")
public final class SearchCommand implements Runnable {

    @Option(names = "--id", description = "id-1 value to search for.", defaultValue = "20")
    private int id;

    private static final List<Item> TABLE = List.of(
            new Item(10, 100, 30, "alpha", "2021/01/10"),
            new Item(20, 200, 20, "bravo", "2021/02/20"),
            new Item(30, 300, 10, "charlie", "2021/03/30"));

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();

        view.banner("Binary search (SEARCH ALL) for id-1 = " + id);
        SearchService.binarySearchById1(TABLE, id)
                .ifPresentOrElse(
                        item -> view.success("Found: " + item),
                        () -> view.error("Not found."));

        view.banner("Sequential search (SEARCH) for id-1 = " + id);
        SearchService.linearSearchById1(TABLE, id)
                .ifPresentOrElse(
                        item -> view.success("Found: " + item),
                        () -> view.error("Not found."));
    }
}
