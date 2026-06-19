package com.cobol.examples.cli;

import com.cobol.examples.core.data.DataGenerator;
import com.cobol.examples.core.data.DataRecord;
import com.cobol.examples.ui.ConsoleView;
import picocli.CommandLine.Command;

@Command(name = "xml", description = "XML GENERATE equivalent using Jackson.")
public final class XmlCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        DataRecord record = new DataRecord("Test Name", "Test Value", true);

        view.banner("Generated XML");
        view.info(DataGenerator.toXml(record));
    }
}
