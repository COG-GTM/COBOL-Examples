package com.cobol.examples.cli;

import com.cobol.examples.core.report.ReportWriter;
import com.cobol.examples.core.report.StudentRecord;
import com.cobol.examples.ui.ConsoleView;
import java.util.List;
import picocli.CommandLine.Command;

@Command(name = "report", description = "Report Writer: paginated, column-positioned report.")
public final class ReportCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        List<StudentRecord> students = List.of(
                new StudentRecord(100234, "Ada Lovelace", "CSC", 4),
                new StudentRecord(100567, "Grace Hopper", "MAT", 5),
                new StudentRecord(100890, "Alan Turing", "PHY", 3));

        view.banner("Generated report");
        view.info(ReportWriter.generate(students));
    }
}
