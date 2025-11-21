package com.cobol.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DisplayTiming {
    private static final Logger logger = LoggerFactory.getLogger(DisplayTiming.class);
    
    private static final int MAX_ROWS = 20;
    private static final int MAX_COLS = 80;
    private static final int MAX_TIMES_TO_RUN = 10;
    private static final int TIMES_TO_REFRESH = 100;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Press enter to start...");
        scanner.nextLine();
        
        clearScreen();

        List<Long> timeDifferences1 = new ArrayList<>();
        
        logger.info("Starting first timing test (display at position)");
        for (int run = 0; run < MAX_TIMES_TO_RUN; run++) {
            long startTime = System.nanoTime();
            
            for (int refresh = 0; refresh < TIMES_TO_REFRESH; refresh++) {
                for (int row = 1; row <= MAX_ROWS; row++) {
                    for (int col = 1; col <= MAX_COLS; col++) {
                        displayAtPosition("@", row, col);
                    }
                }
            }
            
            long endTime = System.nanoTime();
            long timeDiff = endTime - startTime;
            timeDifferences1.add(timeDiff);
            
            displayTimingInfo(startTime, endTime, timeDiff, run + 1);
        }
        
        clearScreen();
        double average1 = computeAndDisplayAverage(timeDifferences1);
        
        clearScreen();

        List<Long> timeDifferences2 = new ArrayList<>();
        
        logger.info("Starting second timing test (simple display)");
        for (int run = 0; run < MAX_TIMES_TO_RUN; run++) {
            long startTime = System.nanoTime();
            
            for (int refresh = 0; refresh < TIMES_TO_REFRESH; refresh++) {
                for (int row = 1; row <= MAX_ROWS; row++) {
                    for (int col = 1; col <= MAX_COLS; col++) {
                        System.out.print("@");
                    }
                }
            }
            
            long endTime = System.nanoTime();
            long timeDiff = endTime - startTime;
            timeDifferences2.add(timeDiff);
            
            displayTimingInfo(startTime, endTime, timeDiff, run + 1);
        }
        
        clearScreen();
        double average2 = computeAndDisplayAverage(timeDifferences2);
        
        logger.info("Test 1 average: {} ms, Test 2 average: {} ms", average1, average2);
        
        scanner.close();
    }

    private static void displayAtPosition(String text, int row, int col) {
        System.out.print(text);
    }

    private static void displayTimingInfo(long startTime, long endTime, long timeDiff, int runNumber) {
        double milliseconds = timeDiff / 1_000_000.0;
        System.out.println("Run " + runNumber + ": " + String.format("%.2f", milliseconds) + " ms");
    }

    private static double computeAndDisplayAverage(List<Long> timeDifferences) {
        System.out.println("\nTiming Results:");
        System.out.println("===============");
        
        long totalNanos = 0;
        for (int i = 0; i < timeDifferences.size(); i++) {
            long nanos = timeDifferences.get(i);
            double millis = nanos / 1_000_000.0;
            System.out.println("Run " + (i + 1) + ": " + String.format("%.2f", millis) + " ms");
            totalNanos += nanos;
        }
        
        double averageNanos = (double) totalNanos / timeDifferences.size();
        double averageMillis = averageNanos / 1_000_000.0;
        
        System.out.println("\nAverage time: " + String.format("%.2f", averageMillis) + " ms");
        System.out.println("\nPress enter to continue...");
        
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        
        return averageMillis;
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
