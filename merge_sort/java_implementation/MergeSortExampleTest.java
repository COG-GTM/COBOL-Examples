import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class MergeSortExampleTest {
    
    private static final String TEST_FILE_1 = "test-file-1.txt";
    private static final String TEST_FILE_2 = "test-file-2.txt";
    private static final String MERGED_FILE = "merge-output.txt";
    private static final String SORTED_FILE = "sorted-contract-id.txt";
    
    @Before
    public void setUp() {
        cleanupTestFiles();
    }
    
    @After
    public void tearDown() {
        cleanupTestFiles();
    }
    
    private void cleanupTestFiles() {
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_1));
            Files.deleteIfExists(Paths.get(TEST_FILE_2));
            Files.deleteIfExists(Paths.get(MERGED_FILE));
            Files.deleteIfExists(Paths.get(SORTED_FILE));
        } catch (IOException e) {
        }
    }
    
    @Test
    public void testCreateTestData() throws IOException {
        MergeSortExample.main(new String[]{});
        
        assertTrue("Test file 1 should exist", Files.exists(Paths.get(TEST_FILE_1)));
        assertTrue("Test file 2 should exist", Files.exists(Paths.get(TEST_FILE_2)));
        
        List<String> file1Lines = Files.readAllLines(Paths.get(TEST_FILE_1));
        List<String> file2Lines = Files.readAllLines(Paths.get(TEST_FILE_2));
        
        assertEquals("Test file 1 should have 6 records", 6, file1Lines.size());
        assertEquals("Test file 2 should have 5 records", 5, file2Lines.size());
    }
    
    @Test
    public void testMergeOperation() throws IOException {
        MergeSortExample.main(new String[]{});
        
        assertTrue("Merged file should exist", Files.exists(Paths.get(MERGED_FILE)));
        
        List<String> mergedLines = Files.readAllLines(Paths.get(MERGED_FILE));
        assertEquals("Merged file should have 11 records", 11, mergedLines.size());
        
        List<CustomerRecord> records = new ArrayList<>();
        for (String line : mergedLines) {
            records.add(CustomerRecord.fromString(line));
        }
        
        int[] expectedCustomerIds = {1, 3, 5, 10, 24, 25, 30, 50, 75, 85, 999};
        for (int i = 0; i < expectedCustomerIds.length; i++) {
            assertEquals("Customer ID at position " + i + " should be " + expectedCustomerIds[i],
                        expectedCustomerIds[i], records.get(i).getCustomerId());
        }
        
        for (int i = 0; i < records.size() - 1; i++) {
            assertTrue("Records should be sorted by customer ID ascending",
                      records.get(i).getCustomerId() <= records.get(i + 1).getCustomerId());
        }
    }
    
    @Test
    public void testSortOperation() throws IOException {
        MergeSortExample.main(new String[]{});
        
        assertTrue("Sorted file should exist", Files.exists(Paths.get(SORTED_FILE)));
        
        List<String> sortedLines = Files.readAllLines(Paths.get(SORTED_FILE));
        assertEquals("Sorted file should have 11 records", 11, sortedLines.size());
        
        List<CustomerRecord> records = new ArrayList<>();
        for (String line : sortedLines) {
            records.add(CustomerRecord.fromString(line));
        }
        
        int[] expectedContractIds = {12323, 8765, 7725, 5423, 5050, 4567, 3331, 1610, 1175, 653, 247};
        for (int i = 0; i < expectedContractIds.length; i++) {
            assertEquals("Contract ID at position " + i + " should be " + expectedContractIds[i],
                        expectedContractIds[i], records.get(i).getCustomerContractId());
        }
        
        for (int i = 0; i < records.size() - 1; i++) {
            assertTrue("Records should be sorted by contract ID descending",
                      records.get(i).getCustomerContractId() >= records.get(i + 1).getCustomerContractId());
        }
    }
    
    @Test
    public void testMergedAndSortedFilesHaveSameRecords() throws IOException {
        MergeSortExample.main(new String[]{});
        
        List<String> mergedLines = Files.readAllLines(Paths.get(MERGED_FILE));
        List<String> sortedLines = Files.readAllLines(Paths.get(SORTED_FILE));
        
        assertEquals("Merged and sorted files should have same number of records",
                    mergedLines.size(), sortedLines.size());
        
        Set<Integer> mergedIds = new HashSet<>();
        Set<Integer> sortedIds = new HashSet<>();
        
        for (String line : mergedLines) {
            mergedIds.add(CustomerRecord.fromString(line).getCustomerId());
        }
        
        for (String line : sortedLines) {
            sortedIds.add(CustomerRecord.fromString(line).getCustomerId());
        }
        
        assertEquals("Both files should contain the same customer IDs", mergedIds, sortedIds);
    }
}
