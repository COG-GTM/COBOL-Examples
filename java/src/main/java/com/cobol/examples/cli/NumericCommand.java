package com.cobol.examples.cli;

import com.cobol.examples.core.numeric.NumericUtils;
import com.cobol.examples.ui.ConsoleView;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "numeric", description = "NUMVAL conversion and IS NUMERIC checks (BigDecimal).")
public final class NumericCommand implements Runnable {

    @Option(names = "--first", description = "First (possibly formatted) number.", defaultValue = "$1,234.56")
    private String first;

    @Option(names = "--second", description = "Second number.", defaultValue = "10")
    private String second;

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();

        view.banner("NUMVAL");
        view.keyValue("numval(first)", NumericUtils.numval(first));
        view.keyValue("numval(second)", NumericUtils.numval(second));
        view.keyValue("total", NumericUtils.sum(first, second));

        view.banner("IS NUMERIC variants for first input");
        view.keyValue("plain", NumericUtils.isNumericPlain(first));
        view.keyValue("zero-filled(width 10)", NumericUtils.isNumericZeroFilled(first, 10));
        view.keyValue("trimmed", NumericUtils.isNumericTrimmed(first));
    }
}
