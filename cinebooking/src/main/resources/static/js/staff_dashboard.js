(function () {
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

  const $ = (id) => document.getElementById(id);

  $("staffName").textContent = name;
  $("hello").textContent = "Xin chào, " + name;
  $("staffRole").textContent = role || "ROLE_STAFF";
  $("avatar").textContent = (name || "S").trim().charAt(0).toUpperCase();

  $("btnReload").onclick = () => window.location.reload();

  $("btnLogout").onclick = () => {
    ["cb_staff_token","cb_staff_role","cb_staff_name"].forEach(k=>{
      localStorage.removeItem(k); sessionStorage.removeItem(k);
    });
    window.location.href = "/staff/staff_login.html";
  };

  async function ping() {
    $("pingOut").textContent = "Đang gọi /api/staff/ping ...";
    try {
      const res = await fetch("/api/staff/ping", {
        headers: { Authorization: "Bearer " + token }
      });
      const data = await res.json().catch(() => ({}));

      if (!res.ok) {
        $("kpiAuth").textContent = "FAIL";
        $("pingOut").textContent =
          "❌ " + (data.message || data.error || (res.status + " " + res.statusText));
        return;
      }

      $("kpiAuth").textContent = "OK";
      $("pingOut").textContent = "✅ OK:\n" + JSON.stringify(data, null, 2);
    } catch (e) {
      $("kpiAuth").textContent = "FAIL";
      $("pingOut").textContent = "❌ Lỗi kết nối: " + e.message;
    }
  }

  $("btnPing").onclick = ping;

  // auto ping khi vào trang
  ping();
})();
