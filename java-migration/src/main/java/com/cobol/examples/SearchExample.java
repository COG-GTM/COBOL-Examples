package com.cobol.examples;

import com.cobol.examples.model.NoKeyItem;
import com.cobol.examples.model.TableItem;
import com.cobol.examples.search.BinarySearchTable;
import com.cobol.examples.search.SequentialSearchTable;

import java.time.LocalDate;
import java.util.Scanner;

public class SearchExample {
    private BinarySearchTable binaryTable;
    private SequentialSearchTable sequentialTable;
    private Scanner scanner;
    
    public SearchExample() {
        this.binaryTable = new BinarySearchTable();
        this.sequentialTable = new SequentialSearchTable();
        this.scanner = new Scanner(System.in);
        setupTestData();
    }
    
    public static void main(String[] args) {
        SearchExample example = new SearchExample();
        example.run();
    }
    
    public void run() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Searching keyed table using binary search.");
        System.out.print("Enter id-1 to search for: ");
        int searchId1 = scanner.nextInt();
        
        TableItem found = binaryTable.searchByItemId1(searchId1);
        if (found != null) {
            displayFoundItem(found);
        } else {
            System.out.println("Item not found.");
        }
        
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Searching again with all required ids matching.");
        
        System.out.print("Enter id-1 to search for: ");
        int searchId1All = scanner.nextInt();
        
        System.out.print("Enter id-2 to search for: ");
        int searchId2All = scanner.nextInt();
        
        System.out.print("Enter id-3 to search for: ");
        int searchId3All = scanner.nextInt();
        
        TableItem foundAll = binaryTable.searchByAllKeys(searchId1All, searchId2All, searchId3All);
        if (foundAll != null) {
            displayFoundItem(foundAll);
        } else {
            System.out.println("Item not found.");
        }
        
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Searching not keyed table using sequential search.");
        System.out.print("Enter id: ");
        int searchSeqId = scanner.nextInt();
        
        NoKeyItem foundSeq = sequentialTable.searchById(searchSeqId);
        if (foundSeq != null) {
            System.out.println(" Record found:");
            System.out.println("---------------");
            System.out.println("   ws-no-key-id: " + foundSeq.getId());
            System.out.println("ws-no-key-value: " + foundSeq.getValue());
            System.out.println();
        } else {
            System.out.println("Item not found.");
        }
        
        System.out.println();
        scanner.close();
    }
    
    private void displayFoundItem(TableItem item) {
        System.out.println(" Record found:");
        System.out.println("----------------");
        System.out.println("Item id-1: " + item.getItemId1());
        System.out.println("Item id-2: " + item.getItemId2());
        System.out.println("Item id-3: " + item.getItemId3());
        System.out.println("Item Name: " + item.getItemName());
        System.out.println("Item Date: " + item.getFormattedDate());
        System.out.println();
    }
    
    private void setupTestData() {
        binaryTable.addItem(new TableItem(1, 101, 500, "test item 1", LocalDate.of(2021, 1, 1)));
        binaryTable.addItem(new TableItem(2, 102, 499, "test item 2", LocalDate.of(2021, 2, 2)));
        binaryTable.addItem(new TableItem(3, 103, 498, "test item 3", LocalDate.of(2021, 3, 3)));
        
        sequentialTable.addItem(new NoKeyItem(2, "Value of id 2."));
        sequentialTable.addItem(new NoKeyItem(3, "Value of id 3."));
        sequentialTable.addItem(new NoKeyItem(1, "Value of id 1."));
    }
}
