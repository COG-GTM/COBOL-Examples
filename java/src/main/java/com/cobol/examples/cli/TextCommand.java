package com.cobol.examples.cli;

import com.cobol.examples.core.text.TextUtils;
import com.cobol.examples.core.text.TextUtils.TrimMode;
import com.cobol.examples.ui.ConsoleView;
import picocli.CommandLine.Command;

@Command(name = "text", description = "TRIM and UNSTRING string-handling examples.")
public final class TextCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        String sample = "    hello world       ";

        view.banner("TRIM");
        view.keyValue("original", "--" + sample + "--");
        view.keyValue("both", "--" + TextUtils.trim(sample, TrimMode.BOTH) + "--");
        view.keyValue("leading", "--" + TextUtils.trim(sample, TrimMode.LEADING) + "--");
        view.keyValue("trailing", "--" + TextUtils.trim(sample, TrimMode.TRAILING) + "--");

        view.banner("UNSTRING with multiple delimiters");
        String source = "A<B<CD>E!FG|HIJ";
        view.keyValue("source", source);
        view.info(TextUtils.unstring(source, true, '<', '>', '!', '|').toString());
    }
}
