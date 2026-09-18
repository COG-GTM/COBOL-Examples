package com.cobolexamples.search;

import java.util.Comparator;
import java.util.List;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class SearchExample {

    record Item(int id1, int id2, int id3, String name, String date) {}

    record NoKeyItem(int id, String value) {}

    private final List<Item> items = List.of(
            new Item(1, 101, 500, Pic.x("test item 1", 16), "2021/01/01"),
            new Item(2, 102, 499, Pic.x("test item 2", 16), "2021/02/02"),
            new Item(3, 103, 498, Pic.x("test item 3", 16), "2021/03/03"));

    private final List<NoKeyItem> noKeyItems = List.of(
            new NoKeyItem(2, Pic.x("Value of id 2.", 25)),
            new NoKeyItem(3, Pic.x("Value of id 3.", 25)),
            new NoKeyItem(1, Pic.x("Value of id 1.", 25)));

    private static final Comparator<Item> ID1 = Comparator.comparingInt(Item::id1);
    private static final Comparator<Item> FULL_KEY =
            Comparator.comparingInt(Item::id1).thenComparingInt(Item::id2);

    public static void main(String[] args) {
        new SearchExample().run();
    }

    Item findById1(int id1) {
        int i = java.util.Collections.binarySearch(items,
                new Item(id1, 0, 0, "", ""), ID1);
        return i >= 0 ? items.get(i) : null;
    }

    Item find(int id1, int id2, int id3) {
        for (Item it : items) {
            if (it.id1() == id1 && it.id2() == id2 && it.id3() == id3) return it;
        }
        return null;
    }

    private static int acceptId(String prompt) {
        String s = Console.accept(prompt);
        String t = Pic.trim(s);
        return Pic.isNumeric(t) ? Integer.parseInt(t) : 0;
    }

    void run() {
        Console.display(" ");
        Console.display("==================================================");
        Console.display("Searching keyed table using binary search.");
        int id1 = acceptId("Enter id-1 to search for: ");

        Item found = findById1(id1);
        if (found == null) {
            Console.display("Item not found.");
        } else {
            displayFoundItem(found);
        }

        Console.display(" ");
        Console.display("==================================================");
        Console.display("Searching again with all required ids matching.");

        id1 = acceptId("Enter id-1 to search for: ");
        int id2 = acceptId("Enter id-2 to search for: ");
        int id3 = acceptId("Enter id-3 to search for: ");

        found = find(id1, id2, id3);
        if (found == null) {
            Console.display("Item not found.");
        } else {
            displayFoundItem(found);
        }

        Console.display(" ");
        Console.display("==================================================");
        Console.display("Searching not keyed table using sequential search.");
        int id = acceptId("Enter id: ");

        NoKeyItem nk = null;
        for (NoKeyItem it : noKeyItems) {
            if (it.id() == id) { nk = it; break; }
        }
        if (nk == null) {
            Console.display("Item not found.");
        } else {
            Console.display(" Record found:");
            Console.display("---------------");
            Console.display("   ws-no-key-id: ", Pic.nine(nk.id(), 4));
            Console.display("ws-no-key-value: ", nk.value());
            Console.display(" ");
        }

        Console.display(" ");
    }

    private static void displayFoundItem(Item it) {
        Console.display(" Record found:");
        Console.display("----------------");
        Console.display("Item id-1: ", Pic.nine(it.id1(), 4));
        Console.display("Item id-2: ", Pic.nine(it.id2(), 4));
        Console.display("Item id-3: ", Pic.nine(it.id3(), 4));
        Console.display("Item Name: ", it.name());
        Console.display("Item Date: ", it.date());
        Console.display(" ");
    }
}
