package com.migration.services;

import org.springframework.stereotype.Service;

/**
 * Phase 6: Subprogram Architecture -> Service Layer
 *
 * Migrates COBOL sub_program/main_app.cbl to a Spring @Service bean.
 *
 * The COBOL main application:
 * 1. Accepts user input for two values
 * 2. Calls sub-app BY CONTENT (values not modified on return)
 * 3. Calls sub-app BY REFERENCE (values can be modified)
 * 4. CANCELs sub-app (resets working storage)
 * 5. Calls sub-app again (working storage is fresh)
 *
 * In Java/Spring:
 * - User input is replaced by method parameters
 * - CALL BY CONTENT -> pass immutable values (String is inherently immutable)
 * - CALL BY REFERENCE -> return modified values via CallResult
 * - CANCEL -> call reset() on the SubAppService
 */
@Service
public class MainAppService {

    private final SubAppService subAppService;

    public MainAppService(SubAppService subAppService) {
        this.subAppService = subAppService;
    }

    /**
     * Demonstrates the full COBOL main-app flow:
     * 1. Call by content (values not modified)
     * 2. Call by reference (values modified)
     * 3. Cancel and call again (working storage reset)
     */
    public MainAppResult runMainFlow(String item1, String item2) {
        // Step 1: Call BY CONTENT - original values should not change
        SubAppService.CallResult byContentResult = subAppService.processCall(item1, item2, true);

        // Step 2: Call BY REFERENCE - values may be modified by sub-program
        // Working-storage in sub-app should retain values from step 1
        SubAppService.CallResult byReferenceResult = subAppService.processCall(item1, item2, false);

        // Step 3: CANCEL sub-app (reset working storage) and call again
        subAppService.reset();
        SubAppService.CallResult afterCancelResult = subAppService.processCall(item1, item2, false);

        return new MainAppResult(byContentResult, byReferenceResult, afterCancelResult);
    }

    /**
     * Result of the complete main application flow showing all three call patterns.
     */
    public record MainAppResult(
            SubAppService.CallResult byContentResult,
            SubAppService.CallResult byReferenceResult,
            SubAppService.CallResult afterCancelResult
    ) {}
}
