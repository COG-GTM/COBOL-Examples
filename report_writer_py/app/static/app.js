"use strict";

const el = (id) => document.getElementById(id);
const state = { run: null, page: 1 };

function showMessage(text, kind) {
  const box = el("message");
  box.textContent = text;
  box.className = text ? `message ${kind}` : "message";
}

function renderPage() {
  const run = state.run;
  const pre = el("report");
  const label = el("page-label");
  if (!run || run.pages.length === 0) {
    pre.textContent = "Run the program to render the report.";
    label.textContent = "Page – of –";
    el("prev-btn").disabled = true;
    el("next-btn").disabled = true;
    return;
  }
  const page = run.pages[state.page - 1];
  pre.textContent = page.lines.join("\n");
  label.textContent = `Page ${state.page} of ${run.pages.length}`;
  el("prev-btn").disabled = state.page <= 1;
  el("next-btn").disabled = state.page >= run.pages.length;
}

function renderRecords(records) {
  const tbody = el("records");
  tbody.innerHTML = "";
  if (records.length === 0) {
    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = 5;
    cell.textContent = "No records.";
    row.appendChild(cell);
    tbody.appendChild(row);
    return;
  }
  records.forEach((record) => {
    const row = document.createElement("tr");
    [
      String(record.index + 1),
      record.student_id_raw,
      record.student_name,
      record.major,
      record.num_courses_raw,
    ].forEach((value, column) => {
      const cell = document.createElement("td");
      cell.textContent = value;
      if (column === 1 || column === 4) cell.className = "mono";
      row.appendChild(cell);
    });
    tbody.appendChild(row);
  });
}

function renderConsole(lines) {
  const list = el("console");
  list.innerHTML = "";
  lines.forEach((line) => {
    const item = document.createElement("li");
    item.textContent = line;
    list.appendChild(item);
  });
}

function renderRun(run) {
  state.run = run;
  state.page = 1;
  el("stat-records").textContent = run.record_count;
  el("stat-details").textContent = run.detail_count;
  el("stat-pages").textContent = run.page_count;
  renderConsole(run.console);
  renderRecords(run.records);
  renderPage();
  if (run.record_count === 0) {
    showMessage(
      "No input records were read. The COBOL program still emits one detail line from its unloaded record area — that line is shown above.",
      "empty"
    );
  } else {
    showMessage("", "");
  }
}

async function generateReport() {
  const button = el("run-btn");
  button.disabled = true;
  try {
    const response = await fetch("/api/reports", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ input_text: el("input-text").value }),
    });
    if (!response.ok) {
      const detail = await response.json().catch(() => ({}));
      showMessage(detail.detail || `Report generation failed (HTTP ${response.status}).`, "error");
      return;
    }
    renderRun(await response.json());
  } catch (error) {
    showMessage(`Report generation failed: ${error.message}`, "error");
  } finally {
    button.disabled = false;
  }
}

async function loadSample() {
  try {
    const response = await fetch("/api/sample-input");
    if (!response.ok) {
      showMessage("Sample input is unavailable.", "error");
      return;
    }
    const data = await response.json();
    el("input-text").value = data.input_text;
    showMessage("", "");
  } catch (error) {
    showMessage(`Could not load sample input: ${error.message}`, "error");
  }
}

el("run-btn").addEventListener("click", generateReport);
el("sample-btn").addEventListener("click", loadSample);
el("clear-btn").addEventListener("click", () => {
  el("input-text").value = "";
  showMessage("", "");
});
el("prev-btn").addEventListener("click", () => {
  if (state.page > 1) {
    state.page -= 1;
    renderPage();
  }
});
el("next-btn").addEventListener("click", () => {
  if (state.run && state.page < state.run.pages.length) {
    state.page += 1;
    renderPage();
  }
});
