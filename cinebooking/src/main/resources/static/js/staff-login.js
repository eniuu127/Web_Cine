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

    const email = $("identifier").value.trim();   // ✅ đổi identifier -> email
    const password = $("password").value;

    // ✅ validate trước khi gọi API
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
        // ✅ body gửi đúng field BE hay dùng: email + password
        body: JSON.stringify({ email, password })
      });

      const data = await res.json().catch(() => ({}));

      if (!res.ok) {
        setMsg(data.message || data.error || "Đăng nhập thất bại", "err");
        return;
      }

      // ✅ Kỳ vọng BE trả về: { token, role, userId, fullName ... }
      const token = data.token || data.accessToken;
      const role  = String(data.role || "").toUpperCase();

      if (!token) {
        setMsg("Thiếu token từ server (BE chưa trả token).", "err");
        return;
      }

      // ✅ Chỉ cho Staff vào
      if (role && role !== "STAFF" && role !== "ROLE_STAFF") {
        setMsg("Tài khoản này không phải STAFF.", "err");
        return;
      }

      // Remember
      const remember = $("remember").checked;
      const store = remember ? localStorage : sessionStorage;

      store.setItem("cb_staff_token", token);
      store.setItem("cb_staff_role", role || "STAFF");
      store.setItem("cb_staff_name", data.fullName || data.name || "");

      setMsg("✅ Đăng nhập thành công. Đang chuyển trang...", "ok");

      window.location.href = "/staff/staff_dashboard.html";
    } catch (err) {
      setMsg("Lỗi kết nối server: " + err.message, "err");
    } finally {
      btn.disabled = false;
      btn.textContent = "Đăng nhập";
    }
  });
})();
