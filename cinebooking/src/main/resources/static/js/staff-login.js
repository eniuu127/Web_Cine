(function () {
  const $ = (id) => document.getElementById(id);

  const form = $("staffLoginForm");
  const msg  = $("msg");
  const btn  = $("btnLogin");

  const toggle = $("togglePass");
  toggle?.addEventListener("click", () => {
    const p = $("password");
    p.type = (p.type === "password") ? "text" : "password";
  });

  function setMsg(text, type) {
    msg.className = "cb-msg " + (type || "");
    msg.textContent = text || "";
  }

  // ✅ endpoint login BE
  const LOGIN_API = "/api/auth/login";

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMsg("");

    const email = $("identifier").value.trim();
    const password = $("password").value;

    if (!email || !password) {
      setMsg("email/password required", "err");
      return;
    }

    btn.disabled = true;
    btn.textContent = "Đang đăng nhập...";

    try {
      const res = await fetch(LOGIN_API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      const dataText = await res.text();
      let data = {};
      try { data = JSON.parse(dataText); } catch { data = { raw: dataText }; }

      if (!res.ok) {
        setMsg(data.message || data.error || dataText || "Đăng nhập thất bại", "err");
        return;
      }

      const token = data.token || data.accessToken || data.jwt;
      const roleRaw = (data.role || data.authority || "").toString();
      const role = roleRaw.toUpperCase(); // STAFF / ROLE_STAFF

      if (!token) {
        setMsg("Thiếu token từ server (BE chưa trả token).", "err");
        return;
      }

      // ✅ Chỉ cho Staff vào
      if (role && role !== "STAFF" && role !== "ROLE_STAFF") {
        setMsg("Tài khoản này không phải STAFF.", "err");
        return;
      }

      const remember = $("remember")?.checked;
      const store = remember ? localStorage : sessionStorage;

      // ✅ LƯU ĐÚNG KEY dashboard đang đọc
      store.setItem("cine_token", token);
      store.setItem("cine_role", role || "ROLE_STAFF");
      store.setItem("cine_staffName", data.fullName || data.name || data.username || "Staff");

      setMsg("✅ Đăng nhập thành công. Đang chuyển trang...", "ok");
      window.location.href = "/staff/staff_dashboard.html";

    } catch (err) {
      setMsg("Lỗi kết nối server: " + (err?.message || err), "err");
    } finally {
      btn.disabled = false;
      btn.textContent = "Đăng nhập";
    }
  });
})();
