package com.cobolmigration.service;

import com.cobolmigration.service.SubProgramService.MutableString;
import com.cobolmigration.service.SubProgramService.SubProgramResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for SubProgramService validating CALL by-content/by-reference patterns
 * from sub_program/main_app.cbl and sub_program/sub.cbl.
 */
class SubProgramServiceTest {

    private SubProgramService subProgramService;

    @BeforeEach
    void setUp() {
        subProgramService = new SubProgramService();
    }

    @Test
    @DisplayName("callByContent does not modify caller's variables (CALL BY CONTENT)")
    void testCallByContent() {
        String item1 = "value1";
        String item2 = "value2";

        SubProgramResult result = subProgramService.callByContent(item1, item2);

        // Working-storage was empty at start (first call)
        assertThat(result.getWsItem1AtStart()).isEmpty();
        assertThat(result.getWsItem2AtStart()).isEmpty();

        // Working-storage was updated during call
        assertThat(result.getWsItem1AtEnd()).isEqualTo("value1");
        assertThat(result.getWsItem2AtEnd()).isEqualTo("value2");

        // Caller's variables are NOT modified (by content = copy)
        assertThat(item1).isEqualTo("value1");
        assertThat(item2).isEqualTo("value2");
    }

    @Test
    @DisplayName("callByReference modifies caller's variables (CALL BY REFERENCE)")
    void testCallByReference() {
        MutableString item1 = new MutableString("value1");
        MutableString item2 = new MutableString("value2");

        SubProgramResult result = subProgramService.callByReference(item1, item2);

        // Caller's variables ARE modified (by reference)
        assertThat(item1.getValue()).isEqualTo("replace1");
        assertThat(item2.getValue()).isEqualTo("replace2");
    }

    @Test
    @DisplayName("working-storage persists between calls until cancel")
    void testWorkingStoragePersistence() {
        // First call sets working-storage values
        subProgramService.callByContent("first1", "first2");
        assertThat(subProgramService.getWsTestItem1()).isEqualTo("first1");

        // Second call sees working-storage from first call
        SubProgramResult result = subProgramService.callByContent("second1", "second2");
        assertThat(result.getWsItem1AtStart()).isEqualTo("first1");
        assertThat(result.getWsItem2AtStart()).isEqualTo("first2");
    }

    @Test
    @DisplayName("cancel resets working-storage (CANCEL 'sub-app')")
    void testCancel() {
        subProgramService.callByContent("value1", "value2");
        assertThat(subProgramService.getWsTestItem1()).isEqualTo("value1");

        subProgramService.cancel();

        assertThat(subProgramService.getWsTestItem1()).isEmpty();
        assertThat(subProgramService.getWsTestItem2()).isEmpty();

        // After cancel, working-storage starts empty again
        SubProgramResult result = subProgramService.callByContent("new1", "new2");
        assertThat(result.getWsItem1AtStart()).isEmpty();
    }
}
