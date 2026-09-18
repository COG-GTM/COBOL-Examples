package com.cobolexamples.support;

import java.util.ArrayList;
import java.util.List;

public class Unstring {

    private record Delim(String text, boolean all) {}

    private static class Dest {
        final int width;
        final StringBuilder delimIn;
        final int[] countIn;
        String value;

        Dest(int width, StringBuilder delimIn, int[] countIn) {
            this.width = width;
            this.delimIn = delimIn;
            this.countIn = countIn;
        }
    }

    private final String source;
    private final List<Delim> delims = new ArrayList<>();
    private final List<Dest> dests = new ArrayList<>();

    public Unstring(String source) {
        this.source = source;
    }

    public static Unstring of(String source) {
        return new Unstring(source);
    }

    public Unstring delimit(String text) {
        delims.add(new Delim(text, false));
        return this;
    }

    public Unstring delimitAll(String text) {
        delims.add(new Delim(text, true));
        return this;
    }

    public Unstring dest(int width) {
        dests.add(new Dest(width, null, null));
        return this;
    }

    public Unstring dest(int width, StringBuilder delimIn, int[] countIn) {
        dests.add(new Dest(width, delimIn, countIn));
        return this;
    }

    public String destValue(int i) {
        return dests.get(i).value;
    }

    public boolean execute() {
        return execute(new int[]{1}, null);
    }

    public boolean execute(int[] pointer, int[] tallying) {
        int pos = Math.max(pointer[0], 1) - 1;
        int len = source.length();
        int filled = 0;

        for (Dest d : dests) {
            int start = pos;
            Delim matched = null;
            int matchAt = -1;
            int scan = pos;
            while (scan < len) {
                Delim found = null;
                for (Delim del : delims) {
                    if (source.startsWith(del.text, scan)) {
                        found = del;
                        break;
                    }
                }
                if (found != null) {
                    matched = found;
                    matchAt = scan;
                    break;
                }
                scan++;
            }
            int end = (matchAt >= 0) ? matchAt : len;
            String field = source.substring(start, Math.min(end, len));
            d.value = Pic.x(field, d.width);
            if (d.countIn != null) d.countIn[0] = field.length();
            if (d.delimIn != null) {
                d.delimIn.setLength(0);
                d.delimIn.append(matched != null ? matched.text : " ");
            }
            if (matched != null) {
                pos = matchAt + matched.text.length();
                if (matched.all) {
                    while (source.startsWith(matched.text, pos)) {
                        pos += matched.text.length();
                    }
                }
            } else {
                pos = len;
            }
            filled++;
            if (matched == null) {
                for (int i = filled; i < dests.size(); i++) {
                    Dest rest = dests.get(i);
                    rest.value = " ".repeat(rest.width);
                    if (rest.countIn != null) rest.countIn[0] = 0;
                    if (rest.delimIn != null) {
                        rest.delimIn.setLength(0);
                        rest.delimIn.append(' ');
                    }
                }
                break;
            }
        }

        pointer[0] = pos + 1;
        if (tallying != null) tallying[0] += filled;
        return pointer[0] <= len;
    }
}
