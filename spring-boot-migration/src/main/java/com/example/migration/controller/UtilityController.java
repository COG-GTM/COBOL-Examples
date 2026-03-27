package com.example.migration.controller;

import com.example.migration.model.CustomerRecord;
import com.example.migration.model.SearchItem;
import com.example.migration.service.FileSortService;
import com.example.migration.service.ReportService;
import com.example.migration.service.ReportService.StudentRecord;
import com.example.migration.service.SubProgramService;
import com.example.migration.util.SearchUtils;
import com.example.migration.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller exposing utility operations migrated from various COBOL programs.
 * Demonstrates string processing, search, sort, and report generation.
 */
@RestController
@RequestMapping("/api/utility")
public class UtilityController {

    private final FileSortService fileSortService;
    private final ReportService reportService;
    private final SubProgramService subProgramService;

    public UtilityController(FileSortService fileSortService,
                             ReportService reportService,
                             SubProgramService subProgramService) {
        this.fileSortService = fileSortService;
        this.reportService = reportService;
        this.subProgramService = subProgramService;
    }

    /**
     * Demonstrates string trimming (replaces trim/trim.cbl).
     */
    @GetMapping("/trim")
    public ResponseEntity<Map<String, String>> trim(@RequestParam("input") String input) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("original", input);
        result.put("trimBoth", StringUtils.trim(input));
        result.put("trimLeading", StringUtils.trimLeading(input));
        result.put("trimTrailing", StringUtils.trimTrailing(input));
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates UNSTRING operation (replaces unstring/unstring.cbl).
     */
    @GetMapping("/unstring")
    public ResponseEntity<Map<String, Object>> unstring(
            @RequestParam("input") String input,
            @RequestParam("delimiters") List<String> delimiters) {
        StringUtils.UnstringResult result = StringUtils.unstring(
                input, delimiters.toArray(new String[0]));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("source", input);
        response.put("delimiters", delimiters);
        response.put("parts", result.getParts());
        response.put("delimitersFound", result.getDelimitersFound());
        response.put("charCounts", result.getCharCounts());
        response.put("fieldsFilled", result.getFieldsFilled());
        return ResponseEntity.ok(response);
    }

    /**
     * Demonstrates IS NUMERIC check (replaces is_numeric/is_numeric.cbl).
     */
    @GetMapping("/is-numeric")
    public ResponseEntity<Map<String, Object>> isNumeric(@RequestParam("input") String input) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("input", input);
        result.put("isNumeric", StringUtils.isNumeric(input));
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates NUMVAL conversion (replaces numval_test/numval_test.cbl).
     */
    @GetMapping("/numval")
    public ResponseEntity<Map<String, Object>> numval(@RequestParam("input") String input) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("input", input);
        result.put("numericValue", StringUtils.numval(input));
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates binary search (replaces search/search.cbl SEARCH ALL).
     */
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> binarySearch(
            @RequestBody List<SearchItem> items,
            @RequestParam("id1") int id1) {
        items.sort(Comparator.comparingInt(SearchItem::getId1));
        SearchItem key = new SearchItem(id1, 0, 0, null, null);
        int index = SearchUtils.binarySearch(items, key,
                Comparator.comparingInt(SearchItem::getId1));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("searchId", id1);
        result.put("found", index >= 0);
        if (index >= 0) {
            result.put("index", index);
            result.put("item", items.get(index));
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates merge and sort (replaces merge_sort/merge_sort_test.cbl).
     */
    @PostMapping("/merge-sort")
    public ResponseEntity<Map<String, Object>> mergeSort(
            @RequestBody MergeSortRequest request) {
        List<CustomerRecord> merged = fileSortService.mergeByCustomerId(
                request.file1Records(), request.file2Records());
        List<CustomerRecord> sortedByContract = fileSortService.sortByContractIdDescending(merged);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mergedByCustomerId", merged);
        result.put("sortedByContractIdDesc", sortedByContract);
        return ResponseEntity.ok(result);
    }

    /**
     * Demonstrates report generation (replaces report_writer/report_test.cbl).
     */
    @PostMapping("/report")
    public ResponseEntity<String> generateReport(@RequestBody List<StudentRecord> records) {
        String report = reportService.generateReport(records);
        return ResponseEntity.ok(report);
    }

    /**
     * Demonstrates subprogram call (replaces sub_program/).
     */
    @PostMapping("/subprogram")
    public ResponseEntity<SubProgramService.SubProgramResult> callSubProgram(
            @RequestParam("item1") String item1,
            @RequestParam("item2") String item2,
            @RequestParam(value = "byContent", defaultValue = "true") boolean byContent) {
        SubProgramService.SubProgramResult result;
        if (byContent) {
            result = subProgramService.processByContent(item1, item2);
        } else {
            String[] items = {item1, item2};
            result = subProgramService.processByReference(items);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Resets subprogram working-storage (replaces CANCEL "sub-app").
     */
    @PostMapping("/subprogram/reset")
    public ResponseEntity<String> resetSubProgram() {
        subProgramService.reset();
        return ResponseEntity.ok("Sub-program working storage reset.");
    }

    public record MergeSortRequest(List<CustomerRecord> file1Records,
                                   List<CustomerRecord> file2Records) {
    }
}
