(() => {
  const $ = (id) => document.getElementById(id);

  const ticketCode = $("ticketCode");
  const resultText = $("resultText");

  const btnVerify  = $("btnVerify");
  const btnCheckin = $("btnCheckin");

  // ===== Auth (đúng key cine_*) =====
  function getToken() {
    return localStorage.getItem("cine_token") || sessionStorage.getItem("cine_token");
  }
  function authHeaders() {
    const t = getToken();
    return t ? { "Authorization": "Bearer " + t } : {};
  }

  // ✅ ĐÚNG endpoint theo controller bạn gửi
  const API_VERIFY  = (code) => `/api/staff/tickets/verify?code=${encodeURIComponent(code)}`;
  const API_CHECKIN = `/api/staff/tickets/check-in`;

  function setResult(text) {
    if (resultText) resultText.textContent = text || "";
  }

  async function verify() {
    const code = (ticketCode?.value || "").trim();
    if (!code) { setResult("❌ Vui lòng nhập ticketCode"); return; }

    const token = getToken();
    if (!token) { setResult("❌ Chưa có token staff. Hãy đăng nhập."); return; }

    try {
      const res = await fetch(API_VERIFY(code), {
        method: "GET",
        headers: { ...authHeaders() }
      });

      const text = await res.text();
      if (!res.ok) throw new Error(`HTTP ${res.status}\n${text}`);

      // BE trả JSON -> show đẹp
      let data;
      try { data = JSON.parse(text); } catch { data = text; }
      setResult(" VERIFY OK\n" + JSON.stringify(data, null, 2));

      // log localStorage (nếu bạn đang dùng dashboard log)
      pushLog("VERIFY", code, true, "API OK");

    } catch (e) {
      setResult("❌ VERIFY FAIL\n" + (e?.message || e));
      pushLog("VERIFY", code, false, "API FAIL");
    }
  }

  async function checkin() {
    const code = (ticketCode?.value || "").trim();
    if (!code) { setResult("❌ Vui lòng nhập ticketCode"); return; }

    const token = getToken();
    if (!token) { setResult("❌ Chưa có token staff. Hãy đăng nhập."); return; }

    try {
      const res = await fetch(API_CHECKIN, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...authHeaders()
        },
        body: JSON.stringify({ ticketCode: code })
      });

      const text = await res.text();
      if (!res.ok) throw new Error(`HTTP ${res.status}\n${text}`);

      let data;
      try { data = JSON.parse(text); } catch { data = text; }
      setResult(" CHECK-IN OK\n" + JSON.stringify(data, null, 2));

      pushLog("CHECKIN", code, true, "API OK");
    } catch (e) {
      setResult("❌ CHECK-IN FAIL\n" + (e?.message || e));
      pushLog("CHECKIN", code, false, "API FAIL");
    }
  }

  // ===== Local log (để dashboard hiển thị) =====
  const LOG_KEY = "cine_staff_logs";
  function readLogs() {
    try { return JSON.parse(localStorage.getItem(LOG_KEY) || "[]"); } catch { return []; }
  }
  function pushLog(action, code, ok, note) {
    const logs = readLogs();
    logs.push({ ts: Date.now(), action, ticketCode: code, ok: !!ok, note: note || "" });
    localStorage.setItem(LOG_KEY, JSON.stringify(logs));
  }

  btnVerify?.addEventListener("click", verify);
  btnCheckin?.addEventListener("click", checkin);
})();
