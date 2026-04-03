package com.cobolmigration.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Replaces display_timing/display_timing.cbl.
 * Uses Java's Instant/nanoTime for timing operations instead of COBOL's
 * ACCEPT FROM TIME and manual time difference calculations.
 */
@Service
public class DisplayTimingService {

    /**
     * Represents a single timing measurement.
     */
    public record TimingResult(long durationMillis, long durationNanos) {
        public double durationSeconds() {
            return durationMillis / 1000.0;
        }
    }

    /**
     * Times the execution of a Runnable task.
     *
     * @param task the task to time
     * @return TimingResult with the duration
     */
    public TimingResult timeExecution(Runnable task) {
        Instant start = Instant.now();
        task.run();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        return new TimingResult(duration.toMillis(), duration.toNanos());
    }

    /**
     * Runs a task multiple times and returns all timing results.
     * Replaces the COBOL loop that runs timing tests ws-max-times-to-run times.
     *
     * @param task  the task to time
     * @param times number of times to run
     * @return list of TimingResults
     */
    public List<TimingResult> timeMultipleExecutions(Runnable task, int times) {
        List<TimingResult> results = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            results.add(timeExecution(task));
        }
        return results;
    }

    /**
     * Computes the average duration from a list of timing results.
     * Replaces the compute-and-display-average paragraph from display_timing.cbl.
     *
     * @param results list of TimingResults
     * @return average TimingResult
     */
    public TimingResult computeAverage(List<TimingResult> results) {
        if (results == null || results.isEmpty()) {
            return new TimingResult(0, 0);
        }
        long totalMillis = results.stream().mapToLong(TimingResult::durationMillis).sum();
        long totalNanos = results.stream().mapToLong(TimingResult::durationNanos).sum();
        return new TimingResult(totalMillis / results.size(), totalNanos / results.size());
    }
}
