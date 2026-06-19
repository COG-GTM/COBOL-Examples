package com.cobol.examples.cli;

import com.cobol.examples.core.subprogram.SubProgram;
import com.cobol.examples.core.subprogram.SubProgram.CallResult;
import com.cobol.examples.core.subprogram.SubProgram.MutableField;
import com.cobol.examples.ui.ConsoleView;
import picocli.CommandLine.Command;

@Command(name = "sub-program", description = "CALL BY CONTENT vs BY REFERENCE and CANCEL semantics.")
public final class SubProgramCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        SubProgram sub = new SubProgram();

        view.banner("CALL BY CONTENT (caller data unchanged)");
        String content1 = "value-1";
        String content2 = "value-2";
        // Passing immutable copies: the callee cannot affect the originals.
        sub.call(new MutableField(content1), new MutableField(content2));
        view.keyValue("caller #1", content1);
        view.keyValue("caller #2", content2);

        view.banner("CALL BY REFERENCE (caller data mutated)");
        MutableField ref1 = new MutableField("value-1");
        MutableField ref2 = new MutableField("value-2");
        CallResult result = sub.call(ref1, ref2);
        view.keyValue("WS on entry", result.workingStorageOnEntry1() + " / " + result.workingStorageOnEntry2());
        view.keyValue("caller #1 after", ref1.value());
        view.keyValue("caller #2 after", ref2.value());

        view.banner("CANCEL resets working storage");
        sub.cancel();
        CallResult afterCancel = sub.call(new MutableField("x"), new MutableField("y"));
        view.keyValue("WS on entry after cancel",
                "'" + afterCancel.workingStorageOnEntry1() + "' / '" + afterCancel.workingStorageOnEntry2() + "'");
    }
}
