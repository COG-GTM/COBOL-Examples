package com.cobolmigration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for SystemInfoController verifying endpoints that replace
 * COBOL ACCEPT FROM DATE/TIME/ENVIRONMENT statements.
 */
@WebMvcTest(SystemInfoController.class)
class SystemInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDate_shouldReturnDateFormats() throws Exception {
        mockMvc.perform(get("/api/system/date"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date_yymmdd").exists())
                .andExpect(jsonPath("$.date_yyyymmdd").exists())
                .andExpect(jsonPath("$.day_yyddd").exists())
                .andExpect(jsonPath("$.day_yyyyddd").exists())
                .andExpect(jsonPath("$.day_of_week").exists())
                .andExpect(jsonPath("$.iso_date").exists());
    }

    @Test
    void getTime_shouldReturnTimeFormats() throws Exception {
        mockMvc.perform(get("/api/system/time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time_hhmmssnn").exists())
                .andExpect(jsonPath("$.iso_time").exists())
                .andExpect(jsonPath("$.iso_datetime").exists());
    }

    @Test
    void getEnvVariable_shouldReturnVariableInfo() throws Exception {
        mockMvc.perform(get("/api/system/env/PATH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.variable").value("PATH"))
                .andExpect(jsonPath("$.exists").exists());
    }

    @Test
    void getEnvVariable_nonExistent_shouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/system/env/COBOL_NONEXISTENT_VAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(""))
                .andExpect(jsonPath("$.exists").value("false"));
    }

    @Test
    void getUserInfo_shouldReturnUserData() throws Exception {
        mockMvc.perform(get("/api/system/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_name").exists())
                .andExpect(jsonPath("$.os_name").exists());
    }
}
