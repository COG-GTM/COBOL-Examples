"use strict";

const form = document.getElementById("run-form");
const statusLine = document.getElementById("status");
const resultsPanel = document.getElementById("results-panel");
const consolePanel = document.getElementById("console-panel");
const consolePane = document.getElementById("console");
const examplesHost = document.getElementById("examples");
const exampleNav = document.getElementById("example-nav");
const emptyState = document.getElementById("empty-state");

const fields = {
  simple_source: document.getElementById("simple-source"),
  multi_delim_source: document.getElementById("multi-delim-source"),
  multi_dest_source: document.getElementById("multi-dest-source"),
  delimiter: document.getElementById("delimiter"),
  amount: document.getElementById("amount"),
};

let defaults = null;
let currentRun = null;
let currentExample = 1;
let latestRequest = 0;

function setStatus(text, kind) {
  statusLine.textContent = text;
  statusLine.className = kind ? `status ${kind}` : "status";
}

function fillDefaults() {
  if (!defaults) return;
  for (const key of Object.keys(fields)) {
    fields[key].value = defaults[key];
  }
}

async function loadDefaults() {
  const response = await fetch("/api/defaults");
  defaults = await response.json();
  fillDefaults();
}

function element(tag, className, text) {
  const node = document.createElement(tag);
  if (className) node.className = className;
  if (text !== undefined) node.textContent = text;
  return node;
}

function flag(text, kind) {
  return element("span", `flag flag-${kind}`, text);
}

function table(headers, rows) {
  const node = element("table");
  const head = element("thead");
  const headRow = element("tr");
  headers.forEach((header) => headRow.appendChild(element("th", header.mono ? "mono" : null, header.label)));
  head.appendChild(headRow);
  node.appendChild(head);
  const body = element("tbody");
  rows.forEach((row) => {
    const tr = element("tr");
    row.forEach((cell, index) => {
      const td = element("td", headers[index].mono ? "mono" : null);
      if (cell instanceof Node) td.appendChild(cell);
      else td.textContent = cell;
      tr.appendChild(td);
    });
    body.appendChild(tr);
  });
  node.appendChild(body);
  return node;
}

function renderStats(example) {
  const host = element("div");
  const stats = example.stats || {};

  if (example.number === 1) {
    host.appendChild(
      table(
        [{ label: "Field" }, { label: "Value", mono: true }],
        [
          ["ws-part-1", stats.part_1],
          ["ws-part-2", stats.part_2],
        ]
      )
    );
  } else if (example.number === 2) {
    host.appendChild(
      table(
        [{ label: "Pass" }, { label: "Status" }, { label: "ws-part-1", mono: true }, { label: "Pointer", mono: true }],
        (stats.passes || []).map((pass, index) => [
          String(index + 1),
          flag(pass.message, pass.overflow ? "overflow" : "ok"),
          pass.part_value,
          String(pass.pointer).padStart(5, "0"),
        ])
      )
    );
  } else if (example.number === 3) {
    host.appendChild(
      table(
        [{ label: "Field" }, { label: "Value", mono: true }],
        [
          ["status", flag(stats.message, stats.overflow ? "overflow" : "ok")],
          ["ws-part-1", stats.part_1],
          ["ws-part-2", stats.part_2],
          ["ws-pointer", String(stats.pointer).padStart(5, "0")],
        ]
      )
    );
  } else if (example.number === 4) {
    host.appendChild(
      table(
        [
          { label: "#" },
          { label: "Value", mono: true },
          { label: "Delimiter", mono: true },
          { label: "Count", mono: true },
          { label: "Pointer", mono: true },
          { label: "Tallying", mono: true },
          { label: "Notes" },
        ],
        (stats.iterations || []).map((row, index) => {
          const notes = element("span");
          if (row.truncated) notes.appendChild(flag(`truncated to PIC X(5) from ${row.examined_length}`, "truncated"));
          if (row.count_truncated) notes.appendChild(flag("COUNT IN wrapped past PIC 9", "truncated"));
          return [
            String(index + 1),
            row.value,
            row.delimiter,
            row.char_count,
            String(row.pointer).padStart(5, "0"),
            String(row.fields_filled).padStart(2, "0"),
            notes,
          ];
        })
      )
    );
  } else if (example.number === 5) {
    host.appendChild(
      table(
        [
          { label: "STRING NUMBER" },
          { label: "Value", mono: true },
          { label: "Delimiter", mono: true },
          { label: "Count", mono: true },
          { label: "Notes" },
        ],
        (stats.entries || []).map((row) => {
          const notes = element("span");
          if (!row.filled) notes.appendChild(flag("not filled", "truncated"));
          if (row.truncated) notes.appendChild(flag(`truncated to PIC X(5) from ${row.examined_length}`, "truncated"));
          return [String(row.index), row.value, row.delimiter, row.char_count, notes];
        })
      )
    );
    const totals = element("p");
    totals.appendChild(element("strong", null, "FIELDS FILLED: "));
    totals.appendChild(element("span", null, String(stats.fields_filled).padStart(2, "0")));
    if (stats.table_overflow) {
      totals.appendChild(document.createTextNode(" "));
      totals.appendChild(flag("more tokens than the OCCURS 6 table holds", "overflow"));
    }
    host.appendChild(totals);
  } else if (example.number === 6) {
    host.appendChild(
      table(
        [{ label: "Field" }, { label: "Value", mono: true }],
        [
          ["ws-source-num (PIC $999,999.99)", stats.edited_value],
          ["ws-dest-num(1)", (stats.parts || [])[0]],
          ["ws-dest-num(2)", (stats.parts || [])[1]],
          ["ws-dest-num(3)", (stats.parts || [])[2]],
        ]
      )
    );
  }
  return host;
}

function renderExample(number) {
  currentExample = number;
  examplesHost.replaceChildren();
  const example = currentRun.examples.find((item) => item.number === number);
  if (!example) return;

  const section = element("div", "example");
  const heading = element("h3", null, `EX ${example.number} : ${example.title}`);
  if (example.overflow) heading.appendChild(document.createTextNode(" "));
  if (example.overflow) heading.appendChild(flag("ERROR: OVERFLOW", "overflow"));
  section.appendChild(heading);
  section.appendChild(element("p", "source-line", `SOURCE: ${example.source}`));
  section.appendChild(renderStats(example));
  section.appendChild(element("pre", "mono-pane", example.lines.join("\n")));
  examplesHost.appendChild(section);

  Array.from(exampleNav.children).forEach((button) => {
    button.setAttribute("aria-current", String(Number(button.dataset.number) === number));
  });
}

function renderRun(run) {
  currentRun = run;
  exampleNav.replaceChildren();
  run.examples.forEach((example) => {
    const button = element("button", null, `EX ${example.number}`);
    button.type = "button";
    button.dataset.number = String(example.number);
    button.addEventListener("click", () => renderExample(example.number));
    exampleNav.appendChild(button);
  });
  consolePane.textContent = run.console.join("\n");
  emptyState.hidden = true;
  resultsPanel.hidden = false;
  consolePanel.hidden = false;
  renderExample(run.examples.some((item) => item.number === currentExample) ? currentExample : 1);
}

async function runProgram(event) {
  event.preventDefault();
  const payload = {};
  for (const key of Object.keys(fields)) {
    payload[key] = fields[key].value;
  }
  if (payload.delimiter.length !== 1) {
    setStatus("ws-delimiter is PIC X: enter exactly one character.", "error");
    return;
  }
  const request = ++latestRequest;
  setStatus("Running…");
  try {
    const response = await fetch("/api/runs", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (!response.ok) {
      const detail = await response.json().catch(() => ({}));
      if (request !== latestRequest) return;
      setStatus(`Request rejected: ${JSON.stringify(detail.detail || response.statusText)}`, "error");
      return;
    }
    const run = await response.json();
    // A slower earlier submission must not overwrite the newest results.
    if (request !== latestRequest) return;
    renderRun(run);
    setStatus("Successfully unstrung.", "ok");
  } catch (error) {
    if (request !== latestRequest) return;
    setStatus(`Service unavailable: ${error}`, "error");
  }
}

form.addEventListener("submit", runProgram);
document.getElementById("reset-button").addEventListener("click", () => {
  fillDefaults();
  // Restore the results too, so what is displayed always matches the inputs.
  form.requestSubmit();
});

loadDefaults().then(() => form.requestSubmit());
