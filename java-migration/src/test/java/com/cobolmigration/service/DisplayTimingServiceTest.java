package com.cobolmigration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DisplayTimingService - validates migration of display_timing/display_timing.cbl.
 */
class DisplayTimingServiceTest {

    private DisplayTimingService service;

    @BeforeEach
    void setUp() {
        service = new DisplayTimingService();
    }

    @Test
    void timeExecution_returnsDuration() {
        DisplayTimingService.TimingResult result = service.timeExecution(() -> {
            // Simple task
            int sum = 0;
            for (int i = 0; i < 1000; i++) {
                sum += i;
            }
        });
        assertTrue(result.durationNanos() >= 0);
    }

    @Test
    void timeMultipleExecutions_returnsCorrectCount() {
        List<DisplayTimingService.TimingResult> results = service.timeMultipleExecutions(
                () -> { /* no-op */ }, 10);
        assertEquals(10, results.size());
    }

    @Test
    void computeAverage_calculatesCorrectly() {
        List<DisplayTimingService.TimingResult> results = List.of(
                new DisplayTimingService.TimingResult(100, 100000000),
                new DisplayTimingService.TimingResult(200, 200000000),
                new DisplayTimingService.TimingResult(300, 300000000)
        );
        DisplayTimingService.TimingResult avg = service.computeAverage(results);
        assertEquals(200, avg.durationMillis());
        assertEquals(200000000, avg.durationNanos());
    }

    @Test
    void computeAverage_emptyList() {
        DisplayTimingService.TimingResult avg = service.computeAverage(List.of());
        assertEquals(0, avg.durationMillis());
    }

    @Test
    void computeAverage_nullList() {
        DisplayTimingService.TimingResult avg = service.computeAverage(null);
        assertEquals(0, avg.durationMillis());
    }

    @Test
    void timingResult_durationSeconds() {
        DisplayTimingService.TimingResult result = new DisplayTimingService.TimingResult(1500, 1500000000);
        assertEquals(1.5, result.durationSeconds());
    }
}
