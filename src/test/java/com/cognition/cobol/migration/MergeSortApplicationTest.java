package com.cognition.cobol.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

public class MergeSortApplicationTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        
        deleteFileIfExists("test-file-1.txt");
        deleteFileIfExists("test-file-2.txt");
        deleteFileIfExists("merge-output.txt");
        deleteFileIfExists("sorted-contract-id.txt");
    }
    
    private void deleteFileIfExists(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    public void testCompleteWorkflow() throws Exception {
        MergeSortApplication.main(new String[]{});
        
        String output = outContent.toString();
        
        assertTrue(output.contains("Creating test data files..."));
        assertTrue(output.contains("Merging and sorting files..."));
        assertTrue(output.contains("Sorting merged file on descending contract id...."));
        assertTrue(output.contains("Done."));
        
        assertTrue(Files.exists(Paths.get("test-file-1.txt")));
        assertTrue(Files.exists(Paths.get("test-file-2.txt")));
        assertTrue(Files.exists(Paths.get("merge-output.txt")));
        assertTrue(Files.exists(Paths.get("sorted-contract-id.txt")));
        
        String mergedContent = Files.readString(Paths.get("merge-output.txt"));
        assertFalse(mergedContent.trim().isEmpty());
        
        String sortedContent = Files.readString(Paths.get("sorted-contract-id.txt"));
        assertFalse(sortedContent.trim().isEmpty());
    }
    
    @Test
    public void testOutputFilesContainExpectedRecords() throws Exception {
        MergeSortApplication.main(new String[]{});
        
        String mergedContent = Files.readString(Paths.get("merge-output.txt"));
        String[] mergedLines = mergedContent.trim().split("\n");
        assertEquals(11, mergedLines.length);
        
        String sortedContent = Files.readString(Paths.get("sorted-contract-id.txt"));
        String[] sortedLines = sortedContent.trim().split("\n");
        assertEquals(11, sortedLines.length);
        
        assertTrue(mergedLines[0].startsWith("00001"));
        assertTrue(mergedLines[10].startsWith("00999"));
    }
}
