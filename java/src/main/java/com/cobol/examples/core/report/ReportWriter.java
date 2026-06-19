package com.cobol.examples.core.report;

import java.util.List;

/**
 * Pure-logic port of the COBOL Report Writer in
 * {@code report_writer/report_test.cbl}.
 *
 * <p>The COBOL {@code RD} entry declared a page heading and column-positioned
 * detail lines. That declarative layout is reproduced here as an explicit
 * paginator so the formatting rules (page limit, first/last detail line,
 * column positions, page counter) stay in one place and become unit-testable.
 */
public final class ReportWriter {

    private static final String TITLE = "Customer Order Report";
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final int DETAILS_PER_PAGE = LAST_DETAIL_LINE - FIRST_DETAIL_LINE + 1;

    private ReportWriter() {
    }

    /** Renders the full report (all pages) as a single newline-joined string. */
    public static String generate(List<StudentRecord> records) {
        StringBuilder report = new StringBuilder();
        int total = records.size();
        int pages = Math.max(1, (int) Math.ceil(total / (double) DETAILS_PER_PAGE));

        for (int page = 1; page <= pages; page++) {
            report.append(pageHeading(page)).append('\n');
            int from = (page - 1) * DETAILS_PER_PAGE;
            int to = Math.min(from + DETAILS_PER_PAGE, total);
            for (int i = from; i < to; i++) {
                report.append(detailLine(records.get(i))).append('\n');
            }
        }
        return report.toString();
    }

    private static String pageHeading(int page) {
        StringBuilder line = new StringBuilder();
        placeAt(line, 44, TITLE);
        placeAt(line, 100, "PAGE");
        placeAt(line, 105, "%3d".formatted(page));
        return line.toString();
    }

    private static String detailLine(StudentRecord r) {
        StringBuilder line = new StringBuilder();
        placeAt(line, 4, "%06d".formatted(r.studentId()));
        placeAt(line, 15, "%-20s".formatted(r.name()));
        placeAt(line, 40, "%-3s".formatted(r.major()));
        placeAt(line, 46, "%02d".formatted(r.numCourses()));
        return line.toString().stripTrailing();
    }

    /** Writes {@code text} so its first character sits at the 1-based column. */
    private static void placeAt(StringBuilder line, int column, String text) {
        int targetIndex = column - 1;
        while (line.length() < targetIndex + text.length()) {
            line.append(' ');
        }
        for (int i = 0; i < text.length(); i++) {
            line.setCharAt(targetIndex + i, text.charAt(i));
        }
    }
}
