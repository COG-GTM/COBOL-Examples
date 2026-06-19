package com.cobol.examples.cli;

import com.cobol.examples.core.mergesort.CustomerRecord;
import com.cobol.examples.core.mergesort.MergeSortService;
import com.cobol.examples.core.mergesort.SampleCustomers;
import com.cobol.examples.ui.ConsoleView;
import java.util.List;
import picocli.CommandLine.Command;

@Command(name = "merge-sort", description = "Merge two customer files by id, then sort by contract id (desc).")
public final class MergeSortCommand implements Runnable {

    @Override
    public void run() {
        ConsoleView view = new ConsoleView();
        List<CustomerRecord> merged = MergeSortService.mergeById(SampleCustomers.east(), SampleCustomers.west());

        view.banner("Merge on ascending customer id");
        merged.forEach(r -> view.info(r.toString()));

        view.banner("Sort merged result on descending contract id");
        MergeSortService.sortByContractIdDescending(merged).forEach(r -> view.info(r.toString()));

        view.blank();
        view.success("Done.");
    }
}
