package com.example.mergesort;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance test suite measuring execution time similar to COBOL timing approach.
 * Tests various data sizes to measure scalability and performance characteristics.
 */
class PerformanceTest {
    
    private final MergeSortProcessor processor = new MergeSortProcessor();
    
    @Test
    void testPerformance_SmallDataSet() {
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        
        MergeSortProcessor.ProcessingResult result = processor.processRecords(east, west);
        
        assertTrue(result.getProcessingTimeNanos() > 0);
        assertTrue(result.getProcessingTimeMillis() < 100);
        
        System.out.printf("Small dataset (11 records): %.3f ms%n", result.getProcessingTimeMillis());
    }
    
    @Test
    void testPerformance_MediumDataSet() {
        List<CustomerRecord> east = TestDataFactory.createLargeDataSet(500);
        List<CustomerRecord> west = TestDataFactory.createLargeDataSet(500);
        
        MergeSortProcessor.ProcessingResult result = processor.processRecords(east, west);
        
        assertTrue(result.getProcessingTimeNanos() > 0);
        assertEquals(1000, result.getMergedRecords().size());
        assertEquals(1000, result.getSortedRecords().size());
        
        System.out.printf("Medium dataset (1000 records): %.3f ms%n", result.getProcessingTimeMillis());
    }
    
    @Test
    void testPerformance_LargeDataSet() {
        List<CustomerRecord> east = TestDataFactory.createLargeDataSet(5000);
        List<CustomerRecord> west = TestDataFactory.createLargeDataSet(5000);
        
        MergeSortProcessor.ProcessingResult result = processor.processRecords(east, west);
        
        assertTrue(result.getProcessingTimeNanos() > 0);
        assertEquals(10000, result.getMergedRecords().size());
        assertEquals(10000, result.getSortedRecords().size());
        
        System.out.printf("Large dataset (10000 records): %.3f ms%n", result.getProcessingTimeMillis());
        
        assertTrue(result.getProcessingTimeMillis() < 1000);
    }
    
    @Test
    void testPerformance_MultipleRuns() {
        List<CustomerRecord> east = TestDataFactory.createEastRegionData();
        List<CustomerRecord> west = TestDataFactory.createWestRegionData();
        
        int numRuns = 10;
        double totalTime = 0;
        double minTime = Double.MAX_VALUE;
        double maxTime = 0;
        
        for (int i = 0; i < numRuns; i++) {
            MergeSortProcessor.ProcessingResult result = processor.processRecords(east, west);
            double timeMs = result.getProcessingTimeMillis();
            
            totalTime += timeMs;
            minTime = Math.min(minTime, timeMs);
            maxTime = Math.max(maxTime, timeMs);
        }
        
        double avgTime = totalTime / numRuns;
        
        System.out.printf("Performance over %d runs:%n", numRuns);
        System.out.printf("  Average: %.3f ms%n", avgTime);
        System.out.printf("  Minimum: %.3f ms%n", minTime);
        System.out.printf("  Maximum: %.3f ms%n", maxTime);
        
        assertTrue(avgTime > 0);
        assertTrue(minTime <= avgTime);
        assertTrue(maxTime >= avgTime);
    }
    
    @Test
    void testPerformance_ScalabilityAnalysis() {
        int[] dataSizes = {10, 100, 1000, 5000};
        
        System.out.println("Scalability Analysis:");
        System.out.println("Size\tTime (ms)\tTime per record (μs)");
        
        for (int size : dataSizes) {
            List<CustomerRecord> east = TestDataFactory.createLargeDataSet(size / 2);
            List<CustomerRecord> west = TestDataFactory.createLargeDataSet(size / 2);
            
            MergeSortProcessor.ProcessingResult result = processor.processRecords(east, west);
            
            double timeMs = result.getProcessingTimeMillis();
            double timePerRecord = (result.getProcessingTimeNanos() / 1000.0) / size;
            
            System.out.printf("%d\t%.3f\t\t%.3f%n", size, timeMs, timePerRecord);
            
            assertTrue(timeMs > 0);
            assertTrue(timePerRecord > 0);
        }
    }
}
