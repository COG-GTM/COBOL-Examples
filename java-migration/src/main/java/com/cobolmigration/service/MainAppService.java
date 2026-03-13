package com.cobolmigration.service;

import org.springframework.stereotype.Service;

/**
 * Demonstrates calling SubProgramService with different parameter passing semantics.
 *
 * Migrated from: sub_program/main_app.cbl
 *
 * The original COBOL main program demonstrates:
 *   1. CALL BY CONTENT: passes copies of variables; sub program modifications
 *      do NOT affect the caller's variables.
 *   2. CALL BY REFERENCE (default): passes references; sub program modifications
 *      DO affect the caller's variables. Working-storage values in the sub program
 *      persist between calls.
 *   3. CANCEL: resets the sub program's working-storage to initial values.
 */
@Service
public class MainAppService {

    private final SubProgramService subProgramService;

    public MainAppService(SubProgramService subProgramService) {
        this.subProgramService = subProgramService;
    }

    /**
     * Demonstrates the full COBOL main_app flow:
     * 1. Call by content (immutable) - caller's values unchanged
     * 2. Call by reference (mutable) - caller's values modified, WS retained
     * 3. Cancel and call again - WS values reset
     *
     * @param item1 first input value
     * @param item2 second input value
     * @return a summary of all three call results
     */
    public CallDemoResult runDemo(String item1, String item2) {
        // Step 1: Call by content - subprogram cannot modify caller's variables
        SubProgramService.ExecutionResult contentResult =
                subProgramService.executeByContent(item1, item2);
        // After by-content call, original values are unchanged
        String afterContentItem1 = item1;
        String afterContentItem2 = item2;

        // Step 2: Call by reference - subprogram can modify caller's variables
        // Working-storage retains values from the first call
        SubProgramService.MutableString ref1 = new SubProgramService.MutableString(item1);
        SubProgramService.MutableString ref2 = new SubProgramService.MutableString(item2);
        SubProgramService.ExecutionResult referenceResult =
                subProgramService.executeByReference(ref1, ref2);
        // After by-reference call, values are modified by the subprogram
        String afterRefItem1 = ref1.getValue();
        String afterRefItem2 = ref2.getValue();

        // Step 3: Cancel and call again - working-storage should be reset
        subProgramService.cancel();
        SubProgramService.MutableString ref3 = new SubProgramService.MutableString(afterRefItem1);
        SubProgramService.MutableString ref4 = new SubProgramService.MutableString(afterRefItem2);
        SubProgramService.ExecutionResult afterCancelResult =
                subProgramService.executeByReference(ref3, ref4);

        return new CallDemoResult(
                contentResult, afterContentItem1, afterContentItem2,
                referenceResult, afterRefItem1, afterRefItem2,
                afterCancelResult
        );
    }

    /**
     * Result of the full demo run, capturing all three call results and
     * the state of the caller's variables after each call.
     */
    public record CallDemoResult(
            SubProgramService.ExecutionResult contentCallResult,
            String afterContentItem1,
            String afterContentItem2,
            SubProgramService.ExecutionResult referenceCallResult,
            String afterReferenceItem1,
            String afterReferenceItem2,
            SubProgramService.ExecutionResult afterCancelCallResult
    ) {
    }
}
