package com.cobolexamples.jsongenerate;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class JsonGenerateExample {

    static class WsRecord {
        String name = Pic.x("Test Name", 10);
        String value = Pic.x("Test Value", 10);
        String blank = Pic.x("", 10);
        String flag = Pic.x("true", 5);

        String format() {
            return name + value + blank + flag;
        }
    }

    static String generateJson(WsRecord r) {
        return "{\"ws-record\":{\"name\":\"" + Pic.trim(r.name)
                + "\",\"value\":\"" + Pic.trim(r.value)
                + "\",\"ws-record-blank\":\"" + Pic.trim(r.blank)
                + "\",\"enabled\":\"" + Pic.trim(r.flag) + "\"}}";
    }

    public static void main(String[] args) {
        WsRecord wsRecord = new WsRecord();
        String wsJsonOutput = generateJson(wsRecord);
        int wsJsonCharCount = wsJsonOutput.length();

        Console.display("JSON document successfully generated.");
        Console.display("Generated JSON for record: ", wsRecord.format());
        Console.display("----------------------------");
        Console.display(Pic.trim(wsJsonOutput));
        Console.display("----------------------------");
        Console.display("JSON output character count: ", Pic.nine(wsJsonCharCount, 4));
        Console.display("Done.");
    }
}
