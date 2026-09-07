"use strict";

// merge-sort-example console replacement. Every message the program itself produces is
// rendered verbatim; only messages with no COBOL counterpart (client-side required-field
// validation) are our own wording — see ruling D-009 in docs/phase0-contracts.md.

const PANES = [
  { file: "merge-output.txt", path: "merge-output" },
  { file: "sorted-contract-id.txt", path: "sorted-contract-id" },
];

const state = {
  runId: null,
  panes: {
    "merge-output.txt": { page: 1, pageSize: 25, pageCount: 0, recordCount: 0, request: 0 },
    "sorted-contract-id.txt": { page: 1, pageSize: 25, pageCount: 0, recordCount: 0, request: 0 },
  },
};

const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => Array.from(root.querySelectorAll(selector));

function paneElement(file) {
  return $(`.pane[data-file="${file}"]`);
}

function setError(id, message) {
  const node = $(id);
  node.textContent = message || "";
  node.hidden = !message;
}

function clearErrors() {
  ["#file1-error", "#file2-error", "#form-error"].forEach((id) => setError(id, ""));
}

function pad(value, width) {
  const text = String(value);
  return text.length >= width ? text.slice(0, width) : text + " ".repeat(width - text.length);
}

function zeroPad(value, width) {
  return String(value).padStart(width, "0").slice(-width);
}

function buildRecord(customerId, lastName, firstName, contractId, comment) {
  const raw =
    zeroPad(customerId, 5) +
    pad(lastName, 50) +
    pad(firstName, 50) +
    zeroPad(contractId, 5) +
    pad(comment, 25);
  return raw.replace(/ +$/, "");
}

// The literal rows of the COBOL create-test-data paragraph, so "supply your own" starts
// from something that is known to round-trip.
const SAMPLE_EAST = [
  [1, "last-1", "first-1", 5423, "comment-1"],
  [5, "last-5", "first-5", 12323, "comment-5"],
  [10, "last-10", "first-10", 653, "comment-10"],
  [50, "last-50", "first-50", 5050, "comment-50"],
  [25, "last-25", "first-25", 7725, "comment-25"],
  [75, "last-75", "first-75", 1175, "comment-75"],
];
const SAMPLE_WEST = [
  [999, "last-999", "first-999", 1610, "comment-99"],
  [3, "last-03", "first-03", 3331, "comment-03"],
  [30, "last-30", "first-30", 8765, "comment-30"],
  [85, "last-85", "first-85", 4567, "comment-85"],
  [24, "last-24", "first-24", 247, "comment-24"],
];

function sampleFile(rows) {
  return rows.map((row) => buildRecord(...row)).join("\n") + "\n";
}

function currentMode() {
  return $('input[name="mode"]:checked').value;
}

function renderStatuses(statuses) {
  const body = $("#status-body");
  body.textContent = "";
  statuses.forEach((entry) => {
    const row = document.createElement("tr");

    const file = document.createElement("td");
    file.textContent = entry.file;

    const status = document.createElement("td");
    status.textContent = entry.file_status;
    status.className = entry.file_status === "00" ? "status-ok" : "status-bad";

    const message = document.createElement("td");
    // The program displays nothing while the status is "00"; say so rather than inventing text.
    message.textContent = entry.message || "— (no message; the program only displays one on failure)";

    row.append(file, status, message);
    body.append(row);
  });
  $("#status-empty").hidden = true;
  $("#status-table").hidden = false;
}

function renderConsole(lines) {
  const node = $("#console");
  node.textContent = "";
  if (!lines.length) {
    const empty = document.createElement("span");
    empty.className = "empty-state";
    empty.textContent = "The program displayed nothing.";
    node.append(empty);
    return;
  }
  node.textContent = lines.join("\n") + "\n";
}

function renderPane(file, page) {
  const pane = paneElement(file);
  const body = $("[data-pane]", pane);
  const paneState = state.panes[file];
  paneState.page = page.page;
  paneState.pageCount = page.page_count;
  paneState.recordCount = page.record_count;

  body.textContent = "";
  if (page.record_count === 0) {
    const empty = document.createElement("span");
    empty.className = "empty-state";
    // An empty result is an empty state, not an error.
    empty.textContent = "No records were written to this file.";
    body.append(empty);
  } else {
    body.textContent = page.raw;
  }

  if (page.file_status !== "00" && page.message) {
    const message = document.createElement("span");
    message.className = "pane-message";
    message.textContent = page.message;
    body.prepend(message);
  }

  $("[data-count]", pane).textContent = `${page.record_count} record${page.record_count === 1 ? "" : "s"}`;
  $("[data-page-label]", pane).textContent =
    page.record_count === 0
      ? "No pages"
      : `Page ${page.page} of ${page.page_count} · showing ${page.records.length} of ${page.record_count}`;
  $("[data-page-prev]", pane).disabled = page.page <= 1;
  $("[data-page-next]", pane).disabled = !page.more_data;
}

async function loadPane(file, page) {
  const pane = PANES.find((entry) => entry.file === file);
  const paneState = state.panes[file];
  // A pane load that started before the current run — or before a newer load of the same
  // pane — must not paint: its records belong to a result the user has already left.
  const runId = state.runId;
  const request = ++paneState.request;
  const stale = () => runId !== state.runId || request !== paneState.request;

  const url = `/api/runs/${runId}/${pane.path}?page=${page}&page_size=${paneState.pageSize}`;
  const response = await fetch(url);
  if (stale()) {
    return;
  }
  if (!response.ok) {
    const detail = await response.json().catch(() => ({ detail: response.statusText }));
    if (!stale()) {
      setError("#form-error", `${file}: ${detail.detail}`);
    }
    return;
  }
  const body = await response.json();
  if (!stale()) {
    renderPane(file, body);
  }
}

function resetPanes(message) {
  PANES.forEach(({ file }) => {
    const pane = paneElement(file);
    const body = $("[data-pane]", pane);
    body.textContent = "";
    const empty = document.createElement("span");
    empty.className = "empty-state";
    empty.textContent = message;
    body.append(empty);
    $("[data-count]", pane).textContent = "—";
    $("[data-page-label]", pane).textContent = "Page 0 of 0";
    $("[data-page-prev]", pane).disabled = true;
    $("[data-page-next]", pane).disabled = true;
  });
}

function validate(mode) {
  clearErrors();
  if (mode !== "supply") {
    return true;
  }
  let ok = true;
  if (!$("#file1").value.trim()) {
    setError("#file1-error", "test-file-1.txt is required.");
    ok = false;
  }
  if (!$("#file2").value.trim()) {
    setError("#file2-error", "test-file-2.txt is required.");
    ok = false;
  }
  if (!ok) {
    setError("#form-error", "Supply both input files, or switch to generated test data.");
  }
  return ok;
}

async function run() {
  const mode = currentMode();
  if (!validate(mode)) {
    return;
  }

  const button = $("#run");
  button.disabled = true;
  $("#run-state").className = "run-state";
  $("#run-state").textContent = "Running…";
  resetPanes("Running…");

  const payload =
    mode === "generate"
      ? { generate: true }
      : { test_file_1: $("#file1").value, test_file_2: $("#file2").value };

  try {
    const response = await fetch("/api/runs", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    const data = await response.json();
    if (!response.ok) {
      // A non-"00" open makes the program stop; show its own message.
      setError("#form-error", data.detail || response.statusText);
      $("#run-state").textContent = "";
      resetPanes("The run did not complete.");
      return;
    }

    state.runId = data.run_id;
    renderStatuses(data.file_statuses);
    renderConsole(data.console);
    $("#run-state").className = "run-state ok";
    $("#run-state").textContent = `Done. ${data.merged_count} record${data.merged_count === 1 ? "" : "s"} merged.`;
    await Promise.all(PANES.map(({ file }) => loadPane(file, 1)));
  } catch (error) {
    setError("#form-error", `Request failed: ${error.message}`);
    $("#run-state").textContent = "";
    resetPanes("The run did not complete.");
  } finally {
    button.disabled = false;
  }
}

function wire() {
  $$('input[name="mode"]').forEach((radio) => {
    radio.addEventListener("change", () => {
      $("#supply-panel").hidden = currentMode() !== "supply";
      clearErrors();
    });
  });

  $("#sample").addEventListener("click", () => {
    $('input[name="mode"][value="supply"]').checked = true;
    $("#supply-panel").hidden = false;
    $("#file1").value = sampleFile(SAMPLE_EAST);
    $("#file2").value = sampleFile(SAMPLE_WEST);
    clearErrors();
  });

  [
    ["#file1-upload", "#file1"],
    ["#file2-upload", "#file2"],
  ].forEach(([input, target]) => {
    $(input).addEventListener("change", async (event) => {
      const file = event.target.files[0];
      if (file) {
        $(target).value = await file.text();
        clearErrors();
      }
    });
  });

  $("#run").addEventListener("click", run);

  PANES.forEach(({ file }) => {
    const pane = paneElement(file);
    $("[data-page-prev]", pane).addEventListener("click", () => {
      const paneState = state.panes[file];
      if (state.runId && paneState.page > 1) {
        loadPane(file, paneState.page - 1);
      }
    });
    $("[data-page-next]", pane).addEventListener("click", () => {
      const paneState = state.panes[file];
      if (state.runId && paneState.page < paneState.pageCount) {
        loadPane(file, paneState.page + 1);
      }
    });
    $("[data-page-size]", pane).addEventListener("change", (event) => {
      state.panes[file].pageSize = Number(event.target.value);
      if (state.runId) {
        loadPane(file, 1);
      }
    });
  });
}

wire();
