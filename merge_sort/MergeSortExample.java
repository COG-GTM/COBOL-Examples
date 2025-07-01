import java.io.*;
import java.util.*;

public class MergeSortExample {
    
    public static void main(String[] args) {
        MergeSortExample example = new MergeSortExample();
        
        example.createTestData();
        example.mergeAndDisplayFiles();
        example.sortAndDisplayFile();
        
        System.out.println("Done.");
    }
    
    public void createTestData() {
        System.out.println("Creating test data files...");
        
        createTestFile1();
        createTestFile2();
    }
    
    private void createTestFile1() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test-file-1.txt"))) {
            List<CustomerRecord> records = Arrays.asList(
                new CustomerRecord(1, "last-1", "first-1", 5423, "comment-1"),
                new CustomerRecord(5, "last-5", "first-5", 12323, "comment-5"),
                new CustomerRecord(10, "last-10", "first-10", 653, "comment-10"),
                new CustomerRecord(50, "last-50", "first-50", 5050, "comment-50"),
                new CustomerRecord(25, "last-25", "first-25", 7725, "comment-25"),
                new CustomerRecord(75, "last-75", "first-75", 1175, "comment-75")
            );
            
            records.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
            
            for (CustomerRecord record : records) {
                writer.println(record.toFileLine());
            }
        } catch (IOException e) {
            System.err.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void createTestFile2() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test-file-2.txt"))) {
            List<CustomerRecord> records = Arrays.asList(
                new CustomerRecord(999, "last-999", "first-999", 1610, "comment-99"),
                new CustomerRecord(3, "last-03", "first-03", 3331, "comment-03"),
                new CustomerRecord(30, "last-30", "first-30", 8765, "comment-30"),
                new CustomerRecord(85, "last-85", "first-85", 4567, "comment-85"),
                new CustomerRecord(24, "last-24", "first-24", 247, "comment-24")
            );
            
            records.sort(Comparator.comparingInt(CustomerRecord::getCustomerId));
            
            for (CustomerRecord record : records) {
                writer.println(record.toFileLine());
            }
        } catch (IOException e) {
            System.err.println("Failed to open file for output: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void mergeAndDisplayFiles() {
        System.out.println("Merging and sorting files...");
        
        mergeFiles("test-file-1.txt", "test-file-2.txt", "merge-output.txt");
        displayFile("merge-output.txt");
    }
    
    public void sortAndDisplayFile() {
        System.out.println("Sorting merged file on descending contract id....");
        
        sortFileByContractId("merge-output.txt", "sorted-contract-id.txt");
        displayFile("sorted-contract-id.txt");
    }
    
    private void mergeFiles(String file1, String file2, String outputFile) {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(file1));
             BufferedReader reader2 = new BufferedReader(new FileReader(file2));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            CustomerRecord record1 = null;
            CustomerRecord record2 = null;
            
            if (line1 != null && !line1.trim().isEmpty()) {
                record1 = CustomerRecord.fromFileLine(line1);
            }
            if (line2 != null && !line2.trim().isEmpty()) {
                record2 = CustomerRecord.fromFileLine(line2);
            }
            
            while (record1 != null && record2 != null) {
                if (record1.getCustomerId() <= record2.getCustomerId()) {
                    writer.println(record1.toFileLine());
                    line1 = reader1.readLine();
                    if (line1 != null && !line1.trim().isEmpty()) {
                        record1 = CustomerRecord.fromFileLine(line1);
                    } else {
                        record1 = null;
                    }
                } else {
                    writer.println(record2.toFileLine());
                    line2 = reader2.readLine();
                    if (line2 != null && !line2.trim().isEmpty()) {
                        record2 = CustomerRecord.fromFileLine(line2);
                    } else {
                        record2 = null;
                    }
                }
            }
            
            while (record1 != null) {
                writer.println(record1.toFileLine());
                line1 = reader1.readLine();
                if (line1 != null && !line1.trim().isEmpty()) {
                    record1 = CustomerRecord.fromFileLine(line1);
                } else {
                    record1 = null;
                }
            }
            
            while (record2 != null) {
                writer.println(record2.toFileLine());
                line2 = reader2.readLine();
                if (line2 != null && !line2.trim().isEmpty()) {
                    record2 = CustomerRecord.fromFileLine(line2);
                } else {
                    record2 = null;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error opening merged output file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void sortFileByContractId(String inputFile, String outputFile) {
        try {
            List<String> tempFiles = new ArrayList<>();
            int chunkSize = 1000;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                List<CustomerRecord> chunk = new ArrayList<>();
                String line;
                int fileIndex = 0;
                
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        chunk.add(CustomerRecord.fromFileLine(line));
                        
                        if (chunk.size() >= chunkSize) {
                            String tempFileName = "temp_sort_" + fileIndex + ".tmp";
                            sortAndWriteChunk(chunk, tempFileName);
                            tempFiles.add(tempFileName);
                            chunk.clear();
                            fileIndex++;
                        }
                    }
                }
                
                if (!chunk.isEmpty()) {
                    String tempFileName = "temp_sort_" + fileIndex + ".tmp";
                    sortAndWriteChunk(chunk, tempFileName);
                    tempFiles.add(tempFileName);
                }
            }
            
            if (tempFiles.size() == 1) {
                new File(tempFiles.get(0)).renameTo(new File(outputFile));
            } else {
                mergeTemporaryFiles(tempFiles, outputFile);
            }
            
            for (String tempFile : tempFiles) {
                new File(tempFile).delete();
            }
            
        } catch (IOException e) {
            System.err.println("Error opening sorted output file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void sortAndWriteChunk(List<CustomerRecord> chunk, String fileName) throws IOException {
        chunk.sort((r1, r2) -> Integer.compare(r2.getContractId(), r1.getContractId()));
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (CustomerRecord record : chunk) {
                writer.println(record.toFileLine());
            }
        }
    }
    
    private void mergeTemporaryFiles(List<String> tempFiles, String outputFile) throws IOException {
        List<BufferedReader> readers = new ArrayList<>();
        List<CustomerRecord> currentRecords = new ArrayList<>();
        
        try {
            for (String tempFile : tempFiles) {
                BufferedReader reader = new BufferedReader(new FileReader(tempFile));
                readers.add(reader);
                
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    currentRecords.add(CustomerRecord.fromFileLine(line));
                } else {
                    currentRecords.add(null);
                }
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                while (true) {
                    int maxIndex = -1;
                    int maxContractId = -1;
                    
                    for (int i = 0; i < currentRecords.size(); i++) {
                        CustomerRecord record = currentRecords.get(i);
                        if (record != null && record.getContractId() > maxContractId) {
                            maxContractId = record.getContractId();
                            maxIndex = i;
                        }
                    }
                    
                    if (maxIndex == -1) break;
                    
                    writer.println(currentRecords.get(maxIndex).toFileLine());
                    
                    String line = readers.get(maxIndex).readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        currentRecords.set(maxIndex, CustomerRecord.fromFileLine(line));
                    } else {
                        currentRecords.set(maxIndex, null);
                    }
                }
            }
        } finally {
            for (BufferedReader reader : readers) {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }
    
    private List<CustomerRecord> readRecordsFromFile(String filename) {
        List<CustomerRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(CustomerRecord.fromFileLine(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
            System.exit(1);
        }
        
        return records;
    }
    
    private void displayFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error opening file for display: " + e.getMessage());
            System.exit(1);
        }
    }
}
