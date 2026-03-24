package com.cobolmigration.service;

import com.cobolmigration.model.MutableHolder;
import org.springframework.stereotype.Service;

/**
 * Service demonstrating COBOL main program calling sub-program.
 * Replaces: sub_program/main_app.cbl (main-app)
 *
 * Flow from main_app.cbl lines 32-50:
 *   1. Call sub-app BY CONTENT (variables not modified on return)
 *   2. Call sub-app BY REFERENCE (variables modified on return, WS persists)
 *   3. CANCEL sub-app (reset working-storage)
 *   4. Call sub-app again (WS should be reset)
 */
@Service
public class MainAppService {

    private final SubProgramService subProgramService;

    public MainAppService(SubProgramService subProgramService) {
        this.subProgramService = subProgramService;
    }

    /**
     * Demonstrate the full calling sequence from main_app.cbl.
     */
    public DemoResult runDemo(String item1, String item2) {
        DemoResult result = new DemoResult();

        // Step 1: Call by content - originals should not be modified
        SubProgramService.SubResult contentResult = subProgramService.processByContent(item1, item2);
        result.afterContentCall_item1 = item1;
        result.afterContentCall_item2 = item2;
        result.contentResult = contentResult;

        // Step 2: Call by reference - originals should be modified
        MutableHolder<String> holder1 = new MutableHolder<>(item1);
        MutableHolder<String> holder2 = new MutableHolder<>(item2);
        SubProgramService.SubResult refResult = subProgramService.processByReference(holder1, holder2);
        result.afterRefCall_item1 = holder1.getValue();
        result.afterRefCall_item2 = holder2.getValue();
        result.referenceResult = refResult;

        // Step 3: Cancel and call again - WS should be reset
        subProgramService.cancel();
        SubProgramService.SubResult resetResult = subProgramService.processByContent(item1, item2);
        result.afterCancelCall = resetResult;

        return result;
    }

    public static class DemoResult {
        private String afterContentCall_item1;
        private String afterContentCall_item2;
        private SubProgramService.SubResult contentResult;
        private String afterRefCall_item1;
        private String afterRefCall_item2;
        private SubProgramService.SubResult referenceResult;
        private SubProgramService.SubResult afterCancelCall;

        public String getAfterContentCallItem1() { return afterContentCall_item1; }
        public String getAfterContentCallItem2() { return afterContentCall_item2; }
        public SubProgramService.SubResult getContentResult() { return contentResult; }
        public String getAfterRefCallItem1() { return afterRefCall_item1; }
        public String getAfterRefCallItem2() { return afterRefCall_item2; }
        public SubProgramService.SubResult getReferenceResult() { return referenceResult; }
        public SubProgramService.SubResult getAfterCancelCall() { return afterCancelCall; }
    }
}
