package com.cobolmigration.service;

import com.cobolmigration.model.ReportRecord;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service replacing the COBOL Report Writer in report_writer/report_test.cbl (lines 41-63).
 * Generates formatted text reports using a simple template approach.
 *
 * COBOL RD configuration mapped:
 *   page limit 66  -> maxLinesPerPage = 66
 *   heading is 1   -> header starts at line 1
 *   first detail 6 -> detail records start at line 6
 *   last detail 42 -> last detail line on a page
 *   footing 52     -> footer area starts at line 52
 */
@Service
public class ReportService {

    private static final int PAGE_LIMIT = 66;
    private static final int FIRST_DETAIL_LINE = 6;
    private static final int LAST_DETAIL_LINE = 42;
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Generates an HTML report from a list of ReportRecord entries.
     * Replaces the COBOL INITIATE/GENERATE/TERMINATE report writer pattern
     * in report_test.cbl (lines 73-88).
     *
     * @param records list of report records (replaces reading from fd-test-input-file)
     * @return HTML string of the formatted report
     */
    public String generateHtmlReport(List<ReportRecord> records) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>").append(LINE_SEPARATOR);
        html.append("<html><head><title>Customer Order Report</title>").append(LINE_SEPARATOR);
        html.append("<style>").append(LINE_SEPARATOR);
        html.append("  body { font-family: 'Courier New', monospace; }").append(LINE_SEPARATOR);
        html.append("  table { border-collapse: collapse; width: 100%; }").append(LINE_SEPARATOR);
        html.append("  th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }").append(LINE_SEPARATOR);
        html.append("  th { background-color: #4CAF50; color: white; }").append(LINE_SEPARATOR);
        html.append("  .page-header { text-align: center; margin-bottom: 20px; }").append(LINE_SEPARATOR);
        html.append("  .page-info { text-align: right; font-size: 0.9em; color: #666; }").append(LINE_SEPARATOR);
        html.append("</style></head><body>").append(LINE_SEPARATOR);

        int totalPages = calculateTotalPages(records.size());
        int recordIndex = 0;

        for (int page = 1; page <= totalPages; page++) {
            html.append("<div class='page'>").append(LINE_SEPARATOR);
            html.append("  <div class='page-header'><h2>Customer Order Report</h2></div>")
                    .append(LINE_SEPARATOR);
            html.append("  <div class='page-info'>PAGE ").append(page).append("</div>")
                    .append(LINE_SEPARATOR);
            html.append("  <table>").append(LINE_SEPARATOR);
            html.append("    <tr><th>Student ID</th><th>Student Name</th><th>Major</th><th>Courses</th></tr>")
                    .append(LINE_SEPARATOR);

            int detailsOnPage = LAST_DETAIL_LINE - FIRST_DETAIL_LINE + 1;
            for (int i = 0; i < detailsOnPage && recordIndex < records.size(); i++) {
                ReportRecord record = records.get(recordIndex++);
                html.append("    <tr>");
                html.append("<td>").append(String.format("%06d", record.getStudentId())).append("</td>");
                html.append("<td>").append(escapeHtml(record.getStudentName())).append("</td>");
                html.append("<td>").append(escapeHtml(record.getMajor())).append("</td>");
                html.append("<td>").append(String.format("%02d", record.getNumCourses())).append("</td>");
                html.append("</tr>").append(LINE_SEPARATOR);
            }

            html.append("  </table>").append(LINE_SEPARATOR);
            html.append("</div>").append(LINE_SEPARATOR);

            if (page < totalPages) {
                html.append("<hr style='page-break-after: always;'>").append(LINE_SEPARATOR);
            }
        }

        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Generates a plain text report in a format similar to the COBOL report writer output.
     *
     * @param records list of report records
     * @return plain text report string
     */
    public String generateTextReport(List<ReportRecord> records) {
        StringBuilder report = new StringBuilder();
        int totalPages = calculateTotalPages(records.size());
        int recordIndex = 0;

        for (int page = 1; page <= totalPages; page++) {
            // Header (line 1) - matches report-header in report_test.cbl lines 48-57
            report.append(padRight("", 43)).append("Customer Order Report").append(LINE_SEPARATOR);
            report.append(padRight("", 99)).append("PAGE").append(String.format("%4d", page))
                    .append(LINE_SEPARATOR);

            // Blank lines before first detail (lines 3-5)
            for (int line = 3; line < FIRST_DETAIL_LINE; line++) {
                report.append(LINE_SEPARATOR);
            }

            // Detail lines (starting at line 6) - matches report-line in report_test.cbl lines 59-63
            int detailsOnPage = LAST_DETAIL_LINE - FIRST_DETAIL_LINE + 1;
            for (int i = 0; i < detailsOnPage && recordIndex < records.size(); i++) {
                ReportRecord record = records.get(recordIndex++);
                report.append("   ")
                        .append(String.format("%06d", record.getStudentId()))
                        .append("      ")
                        .append(padRight(record.getStudentName(), 20))
                        .append("     ")
                        .append(padRight(record.getMajor(), 3))
                        .append("  ")
                        .append(String.format("%02d", record.getNumCourses()))
                        .append(LINE_SEPARATOR);
            }

            // Fill remaining lines to reach page limit
            if (page < totalPages) {
                report.append(LINE_SEPARATOR);
            }
        }

        return report.toString();
    }

    private int calculateTotalPages(int totalRecords) {
        int detailsPerPage = LAST_DETAIL_LINE - FIRST_DETAIL_LINE + 1;
        return Math.max(1, (int) Math.ceil((double) totalRecords / detailsPerPage));
    }

    private static String padRight(String value, int length) {
        if (value == null) value = "";
        return String.format("%-" + length + "s", value);
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
