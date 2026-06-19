package com.cobol.examples.core.subprogram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cobol.examples.core.subprogram.SubProgram.CallResult;
import com.cobol.examples.core.subprogram.SubProgram.MutableField;
import org.junit.jupiter.api.Test;

class SubProgramTest {

    @Test
    void byReferenceMutatesArguments() {
        SubProgram sub = new SubProgram();
        MutableField a = new MutableField("value-1");
        MutableField b = new MutableField("value-2");

        sub.call(a, b);

        assertEquals("replace1", a.value());
        assertEquals("replace2", b.value());
    }

    @Test
    void workingStoragePersistsBetweenCallsUntilCancel() {
        SubProgram sub = new SubProgram();

        CallResult first = sub.call(new MutableField("alpha"), new MutableField("beta"));
        assertEquals("", first.workingStorageOnEntry1());

        CallResult second = sub.call(new MutableField("x"), new MutableField("y"));
        assertEquals("alpha", second.workingStorageOnEntry1());
        assertEquals("beta", second.workingStorageOnEntry2());

        sub.cancel();
        CallResult afterCancel = sub.call(new MutableField("x"), new MutableField("y"));
        assertEquals("", afterCancel.workingStorageOnEntry1());
        assertEquals("", afterCancel.workingStorageOnEntry2());
    }
}
