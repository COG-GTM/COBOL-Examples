import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportWriter {
    private static final int PAGE_SIZE = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    
    private int currentLine = 1;
    private int pageNumber = 1;
    private PrintWriter writer;

    public static void main(String[] args) {
        ReportWriter reportWriter = new ReportWriter();
        reportWriter.generateReport("input.txt", "report.txt");
    }

    public void generateReport(String inputFile, String outputFile) {
        System.out.println("Starting test report program.");
        
        List<StudentRecord> records = readInputFile(inputFile);
        
        try {
            writer = new PrintWriter(outputFile);
            System.out.println("Init test report.");
            
            writeHeader();
            
            for (StudentRecord record : records) {
                System.out.println("Generate report line.");
                writeDetailLine(record);
            }
            
            System.out.println("Terminate report.");
            fillPageToEnd();
            
            writer.close();
            System.out.println("Done.");
            
        } catch (IOException e) {
            System.err.println("Error writing report: " + e.getMessage());
        }
    }

    private List<StudentRecord> readInputFile(String filename) {
        List<StudentRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                StudentRecord record = StudentRecord.parseFromLine(line);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
        
        return records;
    }

    private void writeHeader() {
        writeLine(buildHeaderLine1());
        writeLine(buildHeaderLine2());
        writeLine("");
        writeLine("");
        writeLine("");
    }

    private String buildHeaderLine1() {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < 43; i++) {
            line.append(" ");
        }
        line.append("Customer Order Report");
        return line.toString();
    }

    private String buildHeaderLine2() {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < 99; i++) {
            line.append(" ");
        }
        line.append("PAGE");
        line.append(String.format("%4d", pageNumber));
        return line.toString();
    }

    private void writeDetailLine(StudentRecord record) {
        if (currentLine > LAST_DETAIL_LINE) {
            startNewPage();
        }
        
        StringBuilder line = new StringBuilder();
        
        for (int i = 0; i < 3; i++) {
            line.append(" ");
        }
        
        line.append(String.format("%06d", record.getStudentId()));
        
        for (int i = line.length(); i < 14; i++) {
            line.append(" ");
        }
        
        line.append(record.getStudentName());
        
        for (int i = line.length(); i < 39; i++) {
            line.append(" ");
        }
        
        line.append(record.getMajor());
        
        for (int i = line.length(); i < 45; i++) {
            line.append(" ");
        }
        
        line.append(String.format("%2d", record.getNumCourses()));
        
        writeLine(line.toString());
    }

    private void writeLine(String line) {
        writer.println(line);
        currentLine++;
    }

    private void startNewPage() {
        fillPageToEnd();
        currentLine = 1;
        pageNumber++;
        writeHeader();
    }

    private void fillPageToEnd() {
        while (currentLine <= PAGE_SIZE) {
            writer.println();
            currentLine++;
        }
    }
}
