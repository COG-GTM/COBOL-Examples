package com.cobolexamples.subprogram;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Field;

public class SubApp {

    private static final Field wsTestItem1 = new Field(10);
    private static final Field wsTestItem2 = new Field(10);

    public static void call(Field lTestItem1, Field lTestItem2) {
        Field lsTestItem1 = new Field(10);
        Field lsTestItem2 = new Field(10);

        Console.display("In sub program: ", lTestItem1, " ", lTestItem2);
        Console.display(" ");
        Console.display("working-storage values at start:");
        Console.display("ws-test-item-1: ", wsTestItem1);
        Console.display("ws-test-item-2: ", wsTestItem2);
        Console.display(" ");
        Console.display("local-storage values at start:");
        Console.display("ls-test-item-1: ", lsTestItem1);
        Console.display("ls-test-item-2: ", lsTestItem2);
        Console.display(" ");
        Console.display("Moving linkage section values to ws and ls vars..");

        wsTestItem1.set(lTestItem1.get());
        wsTestItem2.set(lTestItem2.get());
        lsTestItem1.set(lTestItem1.get());
        lsTestItem2.set(lTestItem2.get());

        Console.display("setting input variables to new value...");
        lTestItem1.set("replace1");
        lTestItem2.set("replace2");

        Console.display(" ");
        Console.display("working-storage values at end:");
        Console.display("ws-test-item-1: ", wsTestItem1);
        Console.display("ws-test-item-2: ", wsTestItem2);
        Console.display(" ");
        Console.display("local-storage values at end:");
        Console.display("ls-test-item-1: ", lsTestItem1);
        Console.display("ls-test-item-2: ", lsTestItem2);
        Console.display(" ");
        Console.display("Exit sub program: ", lTestItem1, " ", lTestItem2);
    }

    public static void cancel() {
        wsTestItem1.set("");
        wsTestItem2.set("");
    }
}
