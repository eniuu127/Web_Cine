(function () {
  const $ = (id) => document.getElementById(id);

  const token =
    localStorage.getItem("cb_staff_token") ||
    sessionStorage.getItem("cb_staff_token");

  const role =
    localStorage.getItem("cb_staff_role") ||
    sessionStorage.getItem("cb_staff_role");

  const name =
    localStorage.getItem("cb_staff_name") ||
    sessionStorage.getItem("cb_staff_name") ||
    "Staff";

  // ✅ guard
  if (!token || (role && role !== "STAFF" && role !== "ROLE_STAFF")) {
    window.location.href = "/staff/staff_login.html";
    return;
  }

  // fill sidebar info
  $("staffName").textContent = name;
  $("staffRole").textContent = role || "ROLE_STAFF";
  $("avatar").textContent = (name || "S").trim().charAt(0).toUpperCase();

  $("btnReload").onclick = () => window.location.reload();

  $("btnLogout").onclick = () => {
    ["cb_staff_token", "cb_staff_role", "cb_staff_name"].forEach((k) => {
      localStorage.removeItem(k);
      sessionStorage.removeItem(k);
    });
    window.location.href = "/staff/staff_login.html";
  };

  async function api(url, method = "GET", body) {
    const res = await fetch(url, {
      method,
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token,
      },
      body: body ? JSON.stringify(body) : undefined,
    });

    // có thể BE trả text/html khi lỗi -> cố parse json rồi fallback
    const text = await res.text();
    let data = {};
    try { data = text ? JSON.parse(text) : {}; } catch (_) {}

    if (!res.ok) {
      throw new Error(data.message || data.error || text || (res.status + " " + res.statusText));
    }
    return data;
  }

  // helper normalize (cho QR dạng TICKET:xxx)
  function normalizeCode(raw) {
    raw = String(raw || "").trim();
    if (raw.toUpperCase().startsWith("TICKET:")) raw = raw.substring(7).trim();
    return raw;
  }

  // ===== token test =====
  $("btnPing").onclick = async () => {
    try {
      const data = await api("/api/staff/ping");
      $("resultText").textContent = "✅ TOKEN OK:\n" + JSON.stringify(data, null, 2);
    } catch (e) {
      $("resultText").textContent = "❌ " + e.message;
    }
  };

  // ===== verify =====
  $("btnVerify").onclick = async () => {
    const code = normalizeCode($("ticketCode").value);
    if (!code) {
      $("resultText").textContent = "❌ Vui lòng nhập mã vé";
      return;
    }
    try {
      const data = await api(
        "/api/staff/tickets/verify?code=" + encodeURIComponent(code),
        "GET"
      );
      $("resultText").textContent = JSON.stringify(data, null, 2);
    } catch (e) {
      $("resultText").textContent = "❌ " + e.message;
    }
  };

  // ✅ check-in (CHUẨN): POST /api/staff/tickets/check-in + JSON body
  $("btnCheckin").onclick = async () => {
    const code = normalizeCode($("ticketCode").value);
    if (!code) {
      $("resultText").textContent = "❌ Vui lòng nhập mã vé";
      return;
    }
    try {
      const data = await api(
        "/api/staff/tickets/check-in",
        "POST",
        { ticketCode: code }
      );
      $("resultText").textContent = JSON.stringify(data, null, 2);
    } catch (e) {
      $("resultText").textContent = "❌ " + e.message;
    }
  };

  // ===== camera UI (bật/tắt) =====
  const video = $("video");
  const camHint = $("camHint");
  const qrRaw = $("qrRaw");
  let stream = null;

  async function startCamera() {
    try {
      stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "environment" },
        audio: false,
      });
      video.srcObject = stream;
      await video.play();
      camHint.textContent = "Camera đang bật. (Muốn decode QR → tích hợp ZXing offline)";
    } catch (e) {
      camHint.textContent = "❌ Không bật được camera: " + e.message;
    }
  }

  function stopCamera() {
    if (stream) {
      stream.getTracks().forEach((t) => t.stop());
      stream = null;
      video.srcObject = null;
      camHint.textContent = "Đã tắt camera";
    }
  }

  $("btnStartCam").onclick = startCamera;
  $("btnStopCam").onclick = stopCamera;

  // Dùng QR vừa quét -> đưa vào ticketCode
  $("btnUseQr").onclick = () => {
    const raw = normalizeCode(qrRaw.value);
    if (!raw) {
      $("resultText").textContent =
        "Chưa có QR content. (Nếu muốn quét QR thật, tích hợp ZXing offline để tự fill vào đây)";
      return;
    }
    $("ticketCode").value = raw;
    $("resultText").textContent = "✅ Đã đưa QR content vào ô mã vé. Bấm Xác thực / Check-in.";
  };

  camHint.textContent = "Bấm 'Bật camera' để bắt đầu quét.";
})();
