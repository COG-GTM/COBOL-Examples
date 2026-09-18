package com.cobolexamples.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class Console {

    private static final BufferedReader IN =
            new BufferedReader(new InputStreamReader(System.in));
    private static final Map<String, String> ENV_OVERLAY = new HashMap<>();

    private Console() {}

    public static void display(Object... parts) {
        StringBuilder sb = new StringBuilder();
        for (Object p : parts) sb.append(p == null ? "" : p.toString());
        System.out.println(sb);
    }

    public static void displayNoAdv(Object... parts) {
        StringBuilder sb = new StringBuilder();
        for (Object p : parts) sb.append(p == null ? "" : p.toString());
        System.out.print(sb);
        System.out.flush();
    }

    public static String accept(String prompt) {
        if (prompt != null) displayNoAdv(prompt);
        try {
            String line = IN.readLine();
            return line == null ? "" : line;
        } catch (IOException e) {
            return "";
        }
    }

    public static String accept() {
        return accept(null);
    }

    public static String acceptField(String prompt, int n) {
        return Pic.x(accept(prompt), n);
    }

    public static String env(String name) {
        if (ENV_OVERLAY.containsKey(name)) return ENV_OVERLAY.get(name);
        String v = System.getenv(name);
        return v == null ? "" : v;
    }

    public static void setEnv(String name, String value) {
        ENV_OVERLAY.put(name, value);
    }
}
