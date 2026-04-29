package com.example.cobolmigration.controller;

import com.example.cobolmigration.dto.JsonRecord;
import com.example.cobolmigration.dto.SearchItem;
import com.example.cobolmigration.service.ReportService;
import com.example.cobolmigration.service.ReportService.StudentRecord;
import com.example.cobolmigration.service.SerializationService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller demonstrating the non-SQL COBOL module equivalents:
 * JSON generation, XML generation, report writing, search table, and
 * CLI argument handling.
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final SerializationService serializationService;
    private final ReportService reportService;

    public DemoController(SerializationService serializationService,
                          ReportService reportService) {
        this.serializationService = serializationService;
        this.reportService = reportService;
    }

    /**
     * Demonstrates JSON generation replacing json_generate/json_generate.cbl.
     */
    @GetMapping("/json")
    public ResponseEntity<JsonRecord> jsonDemo() {
        JsonRecord sample = serializationService.createSampleJsonRecord();
        return ResponseEntity.ok(sample);
    }

    /**
     * Demonstrates XML generation replacing xml_generate/xml_generate.cbl.
     * Returns the raw XML string with the XML declaration.
     */
    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> xmlDemo() {
        String xml = serializationService.generateXml(
                serializationService.createSampleXmlRecord());
        return ResponseEntity.ok(xml);
    }

    /**
     * Generates a text report replacing report_writer/report_test.cbl.
     * Accepts an optional format parameter (text or csv).
     */
    @GetMapping(value = "/report", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> reportDemo(
            @RequestParam(value = "format", defaultValue = "text") String format) {
        List<StudentRecord> sampleData = List.of(
                new StudentRecord(334500, "Alice Johnson", "CSC", 5),
                new StudentRecord(334501, "Bob Smith", "PHY", 12),
                new StudentRecord(334502, "Carol White", "MAT", 8),
                new StudentRecord(334503, "David Brown", "ENG", 3));

        String report;
        if ("csv".equalsIgnoreCase(format)) {
            report = reportService.generateCsvReport(sampleData);
        } else {
            report = reportService.generateTextReport(sampleData);
        }
        return ResponseEntity.ok(report);
    }

    /**
     * Demonstrates binary search on a sorted table, replacing
     * search/search.cbl SEARCH ALL behaviour.
     *
     * Populates a list with the same test data as the COBOL program's
     * setup-test-data paragraph and performs a binary search by id1.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchDemo(
            @RequestParam(value = "id1", defaultValue = "0") int id1) {
        List<SearchItem> table = new ArrayList<>();
        table.add(new SearchItem(1000, 2000, 9000, "First Item", "2021/08/30"));
        table.add(new SearchItem(2000, 3000, 8000, "Second Item", "2021/09/15"));
        table.add(new SearchItem(3000, 4000, 7000, "Third Item", "2021/10/01"));
        Collections.sort(table);

        SearchItem key = new SearchItem(id1, 0, 0, null, null);
        int index = Collections.binarySearch(table, key,
                (a, b) -> Integer.compare(a.getId1(), b.getId1()));

        if (index >= 0) {
            return ResponseEntity.ok(table.get(index));
        }
        return ResponseEntity.ok("Item not found.");
    }

    /**
     * Demonstrates CLI argument handling replacing read_command_args/ module.
     * In a REST context the "arguments" are query parameters.
     * The COBOL program checks for '--test' in the command line args.
     */
    @GetMapping("/args")
    public ResponseEntity<String> argsDemo(
            @RequestParam(value = "args", defaultValue = "") String args) {
        StringBuilder response = new StringBuilder();
        response.append("Full command line args: ").append(args).append("\n");

        if (args.toLowerCase().contains("--test")) {
            response.append("You entered the '--test' cmd arg!\n");
        }
        return ResponseEntity.ok(response.toString());
    }
}
