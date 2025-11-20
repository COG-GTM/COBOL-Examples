package com.cobolexamples.timing;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DisplayTiming {
    
    private static final int MAX_ROWS = 20;
    private static final int MAX_COLS = 80;
    private static final int MAX_TIMES_TO_RUN = 10;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Long> timeDifferences = new ArrayList<>();
        
        System.out.println("Press enter to start...");
        scanner.nextLine();
        
        System.out.println("Running first timing test (using simple display)...");
        
        for (int timesToRun = 0; timesToRun < MAX_TIMES_TO_RUN; timesToRun++) {
            Instant startTime = Instant.now();
            
            for (int timesToRefresh = 0; timesToRefresh < 100; timesToRefresh++) {
                for (int row = 1; row <= MAX_ROWS; row++) {
                    for (int col = 1; col <= MAX_COLS; col++) {
                        System.out.print("@");
                    }
                }
            }
            
            Instant endTime = Instant.now();
            long durationMillis = Duration.between(startTime, endTime).toMillis();
            timeDifferences.add(durationMillis);
            
            System.out.println("\nRun " + (timesToRun + 1) + ": " + durationMillis + " ms");
        }
        
        displayAverage(timeDifferences);
        
        timeDifferences.clear();
        
        System.out.println("\nPress enter to start second test...");
        scanner.nextLine();
        
        System.out.println("Running second timing test (using positioned display)...");
        
        for (int timesToRun = 0; timesToRun < MAX_TIMES_TO_RUN; timesToRun++) {
            Instant startTime = Instant.now();
            
            for (int timesToRefresh = 0; timesToRefresh < 100; timesToRefresh++) {
                for (int row = 1; row <= MAX_ROWS; row++) {
                    for (int col = 1; col <= MAX_COLS; col++) {
                        System.out.print("@");
                    }
                }
            }
            
            Instant endTime = Instant.now();
            long durationMillis = Duration.between(startTime, endTime).toMillis();
            timeDifferences.add(durationMillis);
            
            System.out.println("\nRun " + (timesToRun + 1) + ": " + durationMillis + " ms");
        }
        
        displayAverage(timeDifferences);
        
        scanner.close();
    }
    
    private static void displayAverage(List<Long> timeDifferences) {
        long total = 0;
        
        System.out.println("\nAll timing results:");
        for (int i = 0; i < timeDifferences.size(); i++) {
            System.out.println("Run " + (i + 1) + ": " + timeDifferences.get(i) + " ms");
            total += timeDifferences.get(i);
        }
        
        double average = (double) total / timeDifferences.size();
        System.out.println("\nAverage time: " + String.format("%.2f", average) + " ms");
        System.out.println("Total runs: " + timeDifferences.size());
    }
}
