package com.cobolexamples.support;

import java.math.BigDecimal;

public final class Pic {

    private Pic() {}

    public static String x(String v, int n) {
        if (v == null) v = "";
        if (v.length() >= n) return v.substring(0, n);
        return v + " ".repeat(n - v.length());
    }

    public static String nine(long v, int n) {
        String s = Long.toString(Math.abs(v));
        if (s.length() > n) s = s.substring(s.length() - n);
        return "0".repeat(n - s.length()) + s;
    }

    public static String z(long v, int n) {
        String s = Long.toString(Math.abs(v));
        if (s.length() > n) s = s.substring(s.length() - n);
        return " ".repeat(n - s.length()) + s;
    }

    public static String justifiedRight(String v, int n) {
        if (v == null) v = "";
        if (v.length() >= n) return v.substring(v.length() - n);
        return " ".repeat(n - v.length()) + v;
    }

    public static boolean isNumeric(String v) {
        if (v == null || v.isEmpty()) return false;
        for (int i = 0; i < v.length(); i++) {
            if (!Character.isDigit(v.charAt(i))) return false;
        }
        return true;
    }

    public static BigDecimal numval(String v) {
        if (v == null) return BigDecimal.ZERO;
        String s = v.trim();
        if (s.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(s);
    }

    public static String trim(String v) {
        return trimLeading(trimTrailing(v));
    }

    public static String trimLeading(String v) {
        int i = 0;
        while (i < v.length() && v.charAt(i) == ' ') i++;
        return v.substring(i);
    }

    public static String trimTrailing(String v) {
        int i = v.length();
        while (i > 0 && v.charAt(i - 1) == ' ') i--;
        return v.substring(0, i);
    }
}
