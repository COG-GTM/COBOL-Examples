package com.example.cobolmigration.cli;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Spring Shell component mapping sub_program/main_app.cbl and sub_program/sub.cbl.
 *
 * Demonstrates:
 * - By-content calling: pass copies of values, originals unchanged
 * - By-reference calling: pass mutable objects, callee can modify
 * - Cancel command: resets the service's internal state (mapping COBOL CANCEL
 *   which resets working-storage)
 *
 * In COBOL, working-storage persists between CALL invocations and is only reset
 * on CANCEL. Local-storage is re-initialized on each call. We simulate this with
 * instance fields (working-storage) that persist between commands and can be
 * explicitly reset via the cancel command.
 */
@ShellComponent
public class SubProgramCommands {

    // Simulates COBOL working-storage variables that persist between calls
    private String wsItem1 = "";
    private String wsItem2 = "";

    @ShellMethod("Call sub-program by content (copies values, originals unchanged)")
    public String callByContent(@ShellOption String item1, @ShellOption String item2) {
        // By-content: work with copies, don't modify the passed values
        String localItem1 = item1;
        String localItem2 = item2;

        StringBuilder sb = new StringBuilder();
        sb.append("CALL BY CONTENT:\n");
        sb.append("  Input item-1: ").append(localItem1).append("\n");
        sb.append("  Input item-2: ").append(localItem2).append("\n");
        sb.append("  Working-storage before: ws-item-1=\"").append(wsItem1)
                .append("\", ws-item-2=\"").append(wsItem2).append("\"\n");

        // Sub-program copies linkage values to working-storage
        wsItem1 = localItem1;
        wsItem2 = localItem2;

        // Sub-program modifies linkage values, but by-content means originals are unchanged
        String modifiedItem1 = "replace1";
        String modifiedItem2 = "replace2";

        sb.append("  Working-storage after: ws-item-1=\"").append(wsItem1)
                .append("\", ws-item-2=\"").append(wsItem2).append("\"\n");
        sb.append("  Modified copies: item-1=\"").append(modifiedItem1)
                .append("\", item-2=\"").append(modifiedItem2).append("\"\n");
        sb.append("  Originals unchanged: item-1=\"").append(item1)
                .append("\", item-2=\"").append(item2).append("\"");
        return sb.toString();
    }

    @ShellMethod("Call sub-program by reference (callee can modify values)")
    public String callByReference(@ShellOption String item1, @ShellOption String item2) {
        StringBuilder sb = new StringBuilder();
        sb.append("CALL BY REFERENCE:\n");
        sb.append("  Input item-1: ").append(item1).append("\n");
        sb.append("  Input item-2: ").append(item2).append("\n");
        sb.append("  Working-storage before: ws-item-1=\"").append(wsItem1)
                .append("\", ws-item-2=\"").append(wsItem2).append("\"\n");

        // Sub-program copies linkage values to working-storage
        wsItem1 = item1;
        wsItem2 = item2;

        // By-reference: sub-program modifies the passed values
        String modifiedItem1 = "replace1";
        String modifiedItem2 = "replace2";

        sb.append("  Working-storage after: ws-item-1=\"").append(wsItem1)
                .append("\", ws-item-2=\"").append(wsItem2).append("\"\n");
        sb.append("  Modified (by reference): item-1=\"").append(modifiedItem1)
                .append("\", item-2=\"").append(modifiedItem2).append("\"");
        return sb.toString();
    }

    @ShellMethod("Cancel sub-program (resets working-storage)")
    public String cancelSubProgram() {
        wsItem1 = "";
        wsItem2 = "";
        return "Sub-program cancelled. Working-storage reset to initial values.\n"
                + "ws-item-1=\"" + wsItem1 + "\", ws-item-2=\"" + wsItem2 + "\"";
    }

    @ShellMethod("Show current working-storage state")
    public String showState() {
        return "Current working-storage state:\n"
                + "  ws-item-1=\"" + wsItem1 + "\"\n"
                + "  ws-item-2=\"" + wsItem2 + "\"";
    }
}
