package com.cobolmigration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST controller replacing COBOL terminal UI programs for system information.
 * Replaces:
 *   - accept/accept_from.cbl: ACCEPT ws-date FROM DATE, ACCEPT ws-time FROM TIME
 *   - accept/accept_from.cbl: ACCEPT FROM ENVIRONMENT
 *   - screen_size/get_screen_size.cbl: CBL_GET_SCR_SIZE
 *   - display_test/display-test.cbl: terminal display operations
 *
 * The terminal-based ACCEPT/DISPLAY statements are replaced by REST endpoints
 * that return the same system information as JSON.
 */
@RestController
@RequestMapping("/api/system")
public class SystemInfoController {

    /**
     * Get current date in multiple COBOL formats.
     * Replaces:
     *   ACCEPT ws-input FROM DATE          -> YYMMDD
     *   ACCEPT ws-input FROM DATE YYYYMMDD -> YYYYMMDD
     *   ACCEPT ws-input FROM DAY           -> YYDDD
     *   ACCEPT ws-input FROM DAY YYYYDDD   -> YYYYDDD
     *   ACCEPT ws-input FROM DAY-OF-WEEK   -> 1-7 (Mon=1, Sun=7)
     */
    @GetMapping("/date")
    public ResponseEntity<Map<String, String>> getDate() {
        LocalDate now = LocalDate.now();
        Map<String, String> dateInfo = new LinkedHashMap<>();
        dateInfo.put("date_yymmdd", now.format(DateTimeFormatter.ofPattern("yyMMdd")));
        dateInfo.put("date_yyyymmdd", now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        dateInfo.put("day_yyddd", now.format(DateTimeFormatter.ofPattern("yyDDD")));
        dateInfo.put("day_yyyyddd", now.format(DateTimeFormatter.ofPattern("yyyyDDD")));
        dateInfo.put("day_of_week", String.valueOf(now.getDayOfWeek().getValue()));
        dateInfo.put("iso_date", now.toString());
        return ResponseEntity.ok(dateInfo);
    }

    /**
     * Get current time.
     * Replaces: ACCEPT ws-input FROM TIME -> hhmmssnn format
     */
    @GetMapping("/time")
    public ResponseEntity<Map<String, String>> getTime() {
        LocalTime now = LocalTime.now();
        LocalDateTime dateTime = LocalDateTime.now();
        Map<String, String> timeInfo = new LinkedHashMap<>();
        timeInfo.put("time_hhmmssnn", now.format(DateTimeFormatter.ofPattern("HHmmssnn")));
        timeInfo.put("iso_time", now.toString());
        timeInfo.put("iso_datetime", dateTime.toString());
        return ResponseEntity.ok(timeInfo);
    }

    /**
     * Get environment variable value.
     * Replaces: ACCEPT ws-input FROM ENVIRONMENT "VAR_NAME"
     */
    @GetMapping("/env/{varName}")
    public ResponseEntity<Map<String, String>> getEnvVariable(@PathVariable String varName) {
        String value = System.getenv(varName);
        Map<String, String> envInfo = new LinkedHashMap<>();
        envInfo.put("variable", varName);
        envInfo.put("value", value != null ? value : "");
        envInfo.put("exists", String.valueOf(value != null));
        return ResponseEntity.ok(envInfo);
    }

    /**
     * Get current user name.
     * Replaces: ACCEPT ws-input FROM USER NAME
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, String>> getUserInfo() {
        Map<String, String> userInfo = new LinkedHashMap<>();
        userInfo.put("user_name", System.getProperty("user.name", ""));
        userInfo.put("user_home", System.getProperty("user.home", ""));
        userInfo.put("os_name", System.getProperty("os.name", ""));
        return ResponseEntity.ok(userInfo);
    }
}
