(() => {
  const $ = (id) => document.getElementById(id);

  const kpiAuth  = $("kpiAuth");
  const kpiToday = $("kpiToday");
  const kpiOk    = $("kpiOk");
  const kpiRate  = $("kpiRate");
  const pingOut  = $("pingOut");
  const logBody  = $("logBody");

  const hello    = $("hello");
  const staffName= $("staffName");
  const staffRole= $("staffRole");
  const avatar   = $("avatar");

  const btnPing      = $("btnPing");
  const btnReload    = $("btnReload");
  const btnLogout    = $("btnLogout");
  const btnClearLogs = $("btnClearLogs");

  // ===== Auth helpers =====
  function getToken() {
    return localStorage.getItem("cine_token") || sessionStorage.getItem("cine_token");
  }
  function getRole() {
    return localStorage.getItem("cine_role") || sessionStorage.getItem("cine_role");
  }
  function clearAuth() {
    localStorage.removeItem("cine_token"); localStorage.removeItem("cine_role");
    sessionStorage.removeItem("cine_token"); sessionStorage.removeItem("cine_role");
    localStorage.removeItem("cine_staffName"); sessionStorage.removeItem("cine_staffName");
  }
  function authHeaders() {
    const t = getToken();
    return t ? { "Authorization": "Bearer " + t } : {};
  }

  // ✅ luôn có dấu /
  const API_PING = "/api/staff/ping";

  // ===== Logs (localStorage) =====
  const LOG_KEY = "cine_staff_logs";
  function readLogs() {
    try { return JSON.parse(localStorage.getItem(LOG_KEY) || "[]"); }
    catch { return []; }
  }
  function writeLogs(arr) {
    localStorage.setItem(LOG_KEY, JSON.stringify(arr));
  }

  function fmtTime(ms) {
    const d = new Date(ms);
    return d.toLocaleString("vi-VN");
  }

  function isToday(ms) {
    const d = new Date(ms);
    const now = new Date();
    return d.getFullYear() === now.getFullYear()
      && d.getMonth() === now.getMonth()
      && d.getDate() === now.getDate();
  }

  function renderLogsAndKpi() {
    const logs = readLogs().slice().reverse(); // newest first
    const today = logs.filter(x => isToday(x.ts));

    const total = today.length;
    const ok = today.filter(x => x.ok === true).length;
    const rate = total === 0 ? 0 : Math.round(ok * 100 / total);

    if (kpiToday) kpiToday.textContent = String(total);
    if (kpiOk)    kpiOk.textContent = String(ok);
    if (kpiRate)  kpiRate.textContent = rate + "%";

    if (!logBody) return;

    if (logs.length === 0) {
      logBody.innerHTML = `<tr><td colspan="5" style="padding:12px; opacity:.75;">Chưa có dữ liệu</td></tr>`;
      return;
    }

    const rows = logs.slice(0, 12).map(x => {
      const badge = x.ok ? "OK" : "FAIL";
      const note = (x.note || "").replace(/[<>&]/g, s => ({'<':'&lt;','>':'&gt;','&':'&amp;'}[s]));
      return `
        <tr style="border-top:1px solid rgba(255,255,255,.08);">
          <td style="padding:12px; white-space:nowrap;">${fmtTime(x.ts)}</td>
          <td style="padding:12px;">${x.action || ""}</td>
          <td style="padding:12px; font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;">
            ${x.ticketCode || ""}
          </td>
          <td style="padding:12px; white-space:nowrap;">${badge}</td>
          <td style="padding:12px; opacity:.8;">${note}</td>
        </tr>
      `;
    }).join("");

    logBody.innerHTML = rows;
  }

  function setAuthUI(ok, reason) {
    if (kpiAuth) kpiAuth.textContent = ok ? "OK" : "FAIL";
    if (pingOut && reason) pingOut.textContent = reason;
  }

  async function ping() {
    const token = getToken();
    if (!token) {
      setAuthUI(false, "Chưa có token. Hãy đăng nhập staff.");
      return;
    }
    try {
      const res = await fetch(API_PING, { headers: authHeaders() });
      const text = await res.text();
      if (!res.ok) {
        setAuthUI(false, `HTTP ${res.status}\n${text}`);
        return;
      }
      setAuthUI(true, text);
    } catch (e) {
      setAuthUI(false, "Không gọi được API /api/staff/ping.\n" + (e?.message || e));
    }
  }

  function loadProfile() {
    const name =
      localStorage.getItem("cine_staffName") ||
      sessionStorage.getItem("cine_staffName") ||
      "Staff";

    const role = getRole() || "ROLE_STAFF";

    if (staffName) staffName.textContent = name;
    if (staffRole) staffRole.textContent = role;
    if (avatar) avatar.textContent = (name || "S").trim().charAt(0).toUpperCase();
    if (hello) hello.textContent = `Xin chào, ${name}`;
  }

  // events
  btnPing?.addEventListener("click", ping);
  btnReload?.addEventListener("click", () => location.reload());
  btnLogout?.addEventListener("click", () => {
    clearAuth();
    location.href = "/staff/staff_login.html";
  });
  btnClearLogs?.addEventListener("click", () => {
    writeLogs([]);
    renderLogsAndKpi();
  });

  // init
  loadProfile();
  renderLogsAndKpi();
  ping();
})();
