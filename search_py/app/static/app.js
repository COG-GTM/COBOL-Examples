"use strict";

const result = document.getElementById("result");

function escapeHtml(text) {
  return text.replace(/[&<>"']/g, (ch) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;",
  })[ch]);
}

function renderAccepted(accepted) {
  const parts = Object.entries(accepted).map(([key, value]) => `${key} accepted as ${value}`);
  return `<p class="accepted">${escapeHtml(parts.join(" · "))}</p>`;
}

function render(payload) {
  const pane = `<pre class="mono">${escapeHtml(payload.lines.join("\n"))}</pre>`;
  if (payload.found) {
    result.innerHTML =
      `<div class="result-found-head">${escapeHtml(payload.message.trim())}</div>` +
      pane +
      renderAccepted(payload.accepted);
  } else {
    // The COBOL's AT END wording: an empty result, not an error.
    result.innerHTML =
      `<div class="result-empty">${escapeHtml(payload.message)}</div>` + renderAccepted(payload.accepted);
  }
}

function renderError(message) {
  result.innerHTML = `<div class="result-error">${escapeHtml(message)}</div>`;
}

async function submit(form) {
  const mode = form.dataset.mode;
  const params = new URLSearchParams();
  for (const input of form.querySelectorAll("input")) {
    params.set(input.name, input.value);
  }
  try {
    const response = await fetch(`/api/search/${mode}?${params.toString()}`);
    if (!response.ok) {
      const body = await response.json().catch(() => ({}));
      renderError(body.detail ? String(body.detail) : `Request failed (${response.status}).`);
      return;
    }
    render(await response.json());
  } catch (error) {
    renderError(`Service unavailable: ${error.message}`);
  }
}

for (const form of document.querySelectorAll("form.lookup")) {
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    submit(form);
  });
}

for (const tab of document.querySelectorAll(".tab")) {
  tab.addEventListener("click", () => {
    for (const other of document.querySelectorAll(".tab")) {
      other.classList.toggle("is-active", other === tab);
      other.setAttribute("aria-selected", String(other === tab));
    }
    for (const panel of document.querySelectorAll(".panel")) {
      panel.classList.toggle("is-hidden", panel.id !== `panel-${tab.dataset.mode}`);
    }
    result.innerHTML = '<div class="result-idle">Run a lookup to see the record.</div>';
  });
}

async function loadTables() {
  try {
    const response = await fetch("/api/tables");
    const data = await response.json();
    document.getElementById("items-pane").textContent = data.ws_item_table
      .map((row) => `${row.item_id_1} ${row.item_id_2} ${row.item_id_3} ${row.name}${row.date}`)
      .join("\n");
    document.getElementById("no-key-pane").textContent = data.ws_no_key_item_table
      .map((row) => `${row.no_key_id} ${row.no_key_value}`)
      .join("\n");
  } catch (error) {
    document.getElementById("items-pane").textContent = `unavailable: ${error.message}`;
    document.getElementById("no-key-pane").textContent = "";
  }
}

loadTables();
