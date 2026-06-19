package com.cobol.examples.core.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cobol.examples.core.text.TextUtils.TrimMode;
import java.util.List;
import org.junit.jupiter.api.Test;

class TextUtilsTest {

    @Test
    void trimModes() {
        String s = "  hi  ";
        assertEquals("hi", TextUtils.trim(s, TrimMode.BOTH));
        assertEquals("hi  ", TextUtils.trim(s, TrimMode.LEADING));
        assertEquals("  hi", TextUtils.trim(s, TrimMode.TRAILING));
    }

    @Test
    void unstringSimpleSplit() {
        assertEquals(List.of("Hello", "World"), TextUtils.unstring("Hello World", false, ' '));
    }

    @Test
    void unstringCollapsesConsecutiveDelimiters() {
        assertEquals(
                List.of("A", "B", "CD", "E", "FG", "HIJ"),
                TextUtils.unstring("A<B<CD>E!FG|HIJ", true, '<', '>', '!', '|'));
    }

    @Test
    void unstringKeepsEmptyFieldsWhenNotCollapsing() {
        assertEquals(List.of("a", "", "b"), TextUtils.unstring("a,,b", false, ','));
    }
}
