package com.cobolexamples.numvaltest;

import java.math.BigDecimal;

import com.cobolexamples.support.Console;
import com.cobolexamples.support.Pic;

public class NumvalExample {

    public static void main(String[] args) {
        String wsXVal = Console.acceptField("Enter first number: ", 10);
        String ws9Val = Console.acceptField("Enter second number: ", 10);

        BigDecimal second = Pic.isNumeric(Pic.trim(ws9Val))
                ? new BigDecimal(Pic.trim(ws9Val))
                : BigDecimal.ZERO;
        BigDecimal wsTotal = Pic.numval(wsXVal).add(second);

        Console.display("Total: ", wsTotal.stripTrailingZeros().toPlainString());
    }
}
