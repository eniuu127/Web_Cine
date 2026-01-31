console.log("✅ admin_login.js loaded");

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("adminLoginForm");
  const emailEl = document.getElementById("email");
  const passwordEl = document.getElementById("password");
  const rememberEl = document.getElementById("remember");
  const errorBox = document.getElementById("errorBox");
  const btnLogin = document.getElementById("btnLogin");
  const btnText = btnLogin?.querySelector(".btn-text");
  const spinner = btnLogin?.querySelector(".spinner");
  const togglePw = document.getElementById("togglePw");

  if (!form || !emailEl || !passwordEl || !errorBox || !btnLogin) {
    console.error("❌ Missing elements:", { form, emailEl, passwordEl, errorBox, btnLogin });
    return;
  }

  function showError(msg) {
    errorBox.textContent = msg;
    errorBox.style.display = "block";
  }

  function clearError() {
    errorBox.textContent = "";
    errorBox.style.display = "none";
  }

  function setLoading(isLoading) {
    btnLogin.disabled = isLoading;
    if (btnText) btnText.textContent = isLoading ? "Đang đăng nhập..." : "Đăng nhập";
    if (spinner) spinner.style.display = isLoading ? "inline-block" : "none";
  }

  function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  // Toggle show/hide password
  if (togglePw) {
    togglePw.addEventListener("click", (e) => {
      e.preventDefault();
      const isHidden = passwordEl.type === "password";
      passwordEl.type = isHidden ? "text" : "password";
      togglePw.textContent = isHidden ? "🙈" : "👁";
    });
  }

  function saveToken(token, role) {
    const storage = rememberEl?.checked ? localStorage : sessionStorage;
    storage.setItem("cine_token", token);
    storage.setItem("cine_role", role);

    const other = rememberEl?.checked ? sessionStorage : localStorage;
    other.removeItem("cine_token");
    other.removeItem("cine_role");
  }

  async function safeJson(res) {
    try { return await res.json(); } catch { return null; }
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearError();

    const email = (emailEl.value || "").trim();
    const password = passwordEl.value || "";

    // ✅ Custom validation (không dùng tooltip browser)
    if (!email) {
      showError("Vui lòng nhập Email.");
      emailEl.focus();
      return;
    }
    if (!isValidEmail(email)) {
      showError("Email không hợp lệ. Vui lòng nhập lại !");
      emailEl.focus();
      return;
    }
    if (!password) {
      showError("Vui lòng nhập Mật khẩu.");
      passwordEl.focus();
      return;
    }

    setLoading(true);

    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      const data = await safeJson(res);

      if (!res.ok) {
        showError(data?.message || "Đăng nhập thất bại. Kiểm tra lại email/mật khẩu.");
        return;
      }

      const token = data?.token || data?.accessToken;
      const role = (data?.role || "").toUpperCase();

      if (!token || !role) {
        showError("Response login thiếu token/role. Kiểm tra AuthController trả JSON.");
        return;
      }

      if (role !== "ADMIN") {
        showError("Tài khoản này không có quyền ADMIN.");
        return;
      }

      saveToken(token, role);
      window.location.href = "./admin_dashboard.html";
    } catch (err) {
      console.error(err);
      showError("Không kết nối được server. Kiểm tra backend có chạy không.");
    } finally {
      setLoading(false);
    }
  });
});
