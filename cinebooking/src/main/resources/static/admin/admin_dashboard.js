// ===== Auth helpers =====
function getToken() {
  return localStorage.getItem("cine_token") || sessionStorage.getItem("cine_token");
}
function clearAuth() {
  localStorage.removeItem("cine_token"); localStorage.removeItem("cine_role");
  sessionStorage.removeItem("cine_token"); sessionStorage.removeItem("cine_role");
}
async function adminFetch(path, options = {}) {
  const token = getToken();
  const res = await fetch(path, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
      Authorization: `Bearer ${token}`,
    },
  });

  if (res.status === 401 || res.status === 403) {
    clearAuth();
    window.location.replace("./admin_login.html");
    return null;
  }
  return res;
}
async function safeJson(res) {
  try { return await res.json(); } catch { return null; }
}
function toast(msg) {
  const t = document.getElementById("toast");
  t.textContent = msg;
  t.style.display = "block";
  clearTimeout(toast._tm);
  toast._tm = setTimeout(() => (t.style.display = "none"), 2200);
}

// ===== UI navigation =====
const pages = ["dashboard", "movies", "showtimes", "rooms", "seats", "staff"];
const pageTitleMap = {
  dashboard: ["Dashboard", "Thống kê tổng quan hệ thống"],
  movies: ["Movies", "CRUD phim (/api/admin/movies)"],
  showtimes: ["Showtimes", "CRUD suất chiếu (/api/admin/showtimes)"],
  rooms: ["Rooms", "CRUD phòng (/api/admin/rooms)"],
  seats: ["Seats", "Generate/Clear seat map theo room"],
  staff: ["Staff", "CRUD nhân viên (/api/admin/staff)"],
};

function showPage(key) {
  pages.forEach(p => {
    document.getElementById(`page-${p}`).classList.toggle("hidden", p !== key);
  });
  document.querySelectorAll(".nav-item").forEach(btn => {
    btn.classList.toggle("active", btn.dataset.page === key);
  });
  document.getElementById("pageTitle").textContent = pageTitleMap[key][0];
  document.getElementById("pageDesc").textContent = pageTitleMap[key][1];

  // load data theo tab
  if (key === "dashboard") loadDashboard();
  if (key === "movies") loadMovies();
  if (key === "showtimes") loadShowtimes();
  if (key === "rooms") loadRooms();
  if (key === "seats") loadSeatRoomsAndGrid();
  if (key === "staff") window.__loadStaff?.();

}

document.querySelectorAll(".nav-item").forEach(btn => {
  btn.addEventListener("click", () => showPage(btn.dataset.page));
});

document.getElementById("btnLogout").addEventListener("click", () => {
  clearAuth();
  window.location.replace("./admin_login.html");
});
document.getElementById("btnRefresh").addEventListener("click", () => {
  const active = document.querySelector(".nav-item.active")?.dataset.page || "dashboard";
  showPage(active);
});

// ===== Modal helpers =====
const modal = document.getElementById("modal");
const modalTitle = document.getElementById("modalTitle");
const modalBody = document.getElementById("modalBody");
const modalFoot = document.getElementById("modalFoot");

function openModal(title, bodyHtml, footHtml) {
  modalTitle.textContent = title;
  modalBody.innerHTML = bodyHtml;
  modalFoot.innerHTML = footHtml;
  modal.classList.remove("hidden");
}
function closeModal() {
  modal.classList.add("hidden");
  modalBody.innerHTML = "";
  modalFoot.innerHTML = "";
}
document.getElementById("modalClose").addEventListener("click", closeModal);
modal.querySelector(".modal-backdrop").addEventListener("click", closeModal);

// ===== Dashboard =====
async function loadDashboard() {
  const [movies, rooms, showtimes] = await Promise.all([
    fetchAdminMovies(),
    fetchAdminRooms(),
    fetchAdminShowtimes(),
  ]);

  document.getElementById("kpiMovies").textContent = movies?.length ?? "—";
  document.getElementById("kpiRooms").textContent = rooms?.length ?? "—";
  document.getElementById("kpiShowtimes").textContent = showtimes?.length ?? "—";

  // breakdowns
  if (movies) {
    const byStatus = groupCount(movies, m => (m.status || "UNKNOWN"));
    document.getElementById("movieStatusBreakdown").textContent = formatBreakdown(byStatus);
  }
  if (rooms) {
    const byType = groupCount(rooms, r => (r.screenType || "UNKNOWN"));
    document.getElementById("roomTypeBreakdown").textContent = formatBreakdown(byType);
  }
}
function groupCount(arr, keyFn) {
  const map = {};
  arr.forEach(x => { const k = String(keyFn(x)); map[k] = (map[k] || 0) + 1; });
  return map;
}
function formatBreakdown(map) {
  const parts = Object.entries(map).sort((a,b)=>b[1]-a[1]).map(([k,v]) => `${k}: ${v}`);
  return parts.length ? parts.join(" • ") : "—";
}

// ===== Movies CRUD =====
let _moviesCache = [];
async function fetchAdminMovies() {
  const res = await adminFetch("/api/admin/movies");
  if (!res) return null;
  const data = await safeJson(res);
  _moviesCache = Array.isArray(data) ? data : [];
  return _moviesCache;
}

async function loadMovies() {
  const data = await fetchAdminMovies();
  renderMovieTable(data || []);
}

function renderMovieTable(list) {
  const tbody = document.querySelector("#movieTable tbody");
  tbody.innerHTML = "";

  const keyword = (document.getElementById("movieSearch").value || "").trim().toLowerCase();
  const filtered = keyword ? list.filter(m => (m.title || "").toLowerCase().includes(keyword)) : list;

  filtered.forEach(m => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${m.movieId ?? ""}</td>

      <td>
        <div style="font-weight:900">${escapeHtml(m.title || "")}</div>
        <div class="muted" style="margin-top:4px; word-break:break-all">
          Poster: ${escapeHtml(m.posterUrl || "")}<br>
          Trailer: ${escapeHtml(m.trailerUrl || "")}
        </div>
      </td>

      <!-- Trailer column (đúng header) -->
      <td>
        ${
          m.trailerUrl
            ? `<a class="link" href="${escapeHtml(m.trailerUrl)}" target="_blank" rel="noopener">Open</a>`
            : `<span class="muted">—</span>`
        }
      </td>

      <!-- Runtime column -->
      <td>${m.runtime ?? ""}</td>

      <!-- Status column -->
      <td><span class="badge">${escapeHtml(m.status || "")}</span></td>

      <!-- Actions column -->
      <td>
        <div class="actions">
          <button class="btn-sm primary" data-act="edit" data-id="${m.movieId}">Edit</button>
          <button class="btn-sm" data-act="status" data-id="${m.movieId}">Set Status</button>
          <button class="btn-sm danger" data-act="del" data-id="${m.movieId}">Delete</button>
        </div>
      </td>
    `;

    tbody.appendChild(tr);
  });

  tbody.querySelectorAll("button").forEach(btn => {
    const id = Number(btn.dataset.id);
    const act = btn.dataset.act;
    if (act === "edit") btn.addEventListener("click", () => openMovieEdit(id));
    if (act === "status") btn.addEventListener("click", () => openMovieStatus(id));
    if (act === "del") btn.addEventListener("click", () => deleteMovie(id));
  });
}

document.getElementById("movieSearch").addEventListener("input", () => renderMovieTable(_moviesCache));

document.getElementById("btnMovieAdd").addEventListener("click", () => {
  openModal("Add Movie",
    movieFormHtml({ title:"", posterUrl:"", runtime: 120, status:"NOW_SHOWING" }),
    `
      <button class="btn btn-ghost" id="mCancel">Cancel</button>
      <button class="btn" id="mSave">Create</button>
    `
  );

  document.getElementById("mCancel").onclick = closeModal;
  document.getElementById("mSave").onclick = async () => {
    const payload = readMovieForm();
    const res = await adminFetch("/api/admin/movies", { method:"POST", body: JSON.stringify(payload) });
    if (!res) return;
    if (!res.ok) { toast("Create movie failed"); return; }
    closeModal();
    toast("Created");
    await loadMovies();
    await loadDashboard();
  };
});

function openMovieEdit(id) {
  const m = _moviesCache.find(x => x.movieId === id);
  if (!m) return;

  openModal(`Edit Movie #${id}`,
    movieFormHtml(m),
    `
      <button class="btn btn-ghost" id="mCancel">Cancel</button>
      <button class="btn" id="mSave">Save</button>
    `
  );
  document.getElementById("mCancel").onclick = closeModal;
  document.getElementById("mSave").onclick = async () => {
    const payload = readMovieForm();
    payload.movieId = id;
    const res = await adminFetch(`/api/admin/movies/${id}`, { method:"PUT", body: JSON.stringify(payload) });
    if (!res) return;
    if (!res.ok) { toast("Update movie failed"); return; }
    closeModal();
    toast("Saved");
    await loadMovies();
    await loadDashboard();
  };
}

function openMovieStatus(id) {
  const m = _moviesCache.find(x => x.movieId === id);
  if (!m) return;

  openModal(`Set Status Movie #${id}`,
    `
      <div class="field">
        <label>Status</label>
        <select id="mvStatus" class="select">
          ${["NOW_SHOWING","COMING_SOON","STOPPED","OPEN","CLOSED", (m.status||"")].filter(Boolean)
            .filter((v,i,a)=>a.indexOf(v)===i)
            .map(v => `<option value="${v}" ${v===m.status?"selected":""}>${v}</option>`).join("")}
        </select>
        <div class="muted" style="margin-top:8px">API: PATCH /api/admin/movies/{id}/status?status=...</div>
      </div>
    `,
    `
      <button class="btn btn-ghost" id="mCancel">Cancel</button>
      <button class="btn" id="mSave">Apply</button>
    `
  );
  document.getElementById("mCancel").onclick = closeModal;
  document.getElementById("mSave").onclick = async () => {
    const status = document.getElementById("mvStatus").value;
    const res = await adminFetch(`/api/admin/movies/${id}/status?status=${encodeURIComponent(status)}`, { method:"PATCH" });
    if (!res) return;
    if (!res.ok) { toast("Set status failed"); return; }
    closeModal();
    toast("Updated status");
    await loadMovies();
    await loadDashboard();
  };
}

async function deleteMovie(id) {
  if (!confirm(`Delete movie #${id}?`)) return;
  const res = await adminFetch(`/api/admin/movies/${id}`, { method:"DELETE" });
  if (!res) return;
  if (!res.ok && res.status !== 204) { toast("Delete failed"); return; }
  toast("Deleted");
  await loadMovies();
  await loadDashboard();
}

function movieFormHtml(m) {
  return `
    <div class="form-grid">
      <div class="field span-2">
        <label>Title</label>
        <input id="mvTitle" class="input" value="${escapeAttr(m.title||"")}" />
      </div>

      <div class="field span-2">
        <label>Poster URL</label>
        <input id="mvPoster" class="input"
               value="${escapeAttr(m.posterUrl||"")}"
               placeholder="https://..." />
      </div>

      <!-- ✅ NEW: Trailer URL -->
      <div class="field span-2">
        <label>Trailer URL (YouTube / Embed)</label>
        <input id="mvTrailer" class="input"
               value="${escapeAttr(m.trailerUrl||"")}"
               placeholder="https://www.youtube.com/embed/xxxx" />
      </div>

      <div class="field">
        <label>Runtime (minutes)</label>
        <input id="mvRuntime" class="input" type="number" min="1"
               value="${Number(m.runtime ?? 120)}" />
      </div>

      <div class="field">
        <label>Status</label>
        <input id="mvStatus2" class="input"
               value="${escapeAttr(m.status||"")}"
               placeholder="NOW_SHOWING / COMING_SOON / STOPPED" />
      </div>
    </div>
  `;
}

function readMovieForm() {
  return {
    title: document.getElementById("mvTitle").value.trim(),
    posterUrl: document.getElementById("mvPoster").value.trim(),
    trailerUrl: document.getElementById("mvTrailer").value.trim(), // ✅ NEW
    runtime: Number(document.getElementById("mvRuntime").value || 0),
    status: document.getElementById("mvStatus2").value.trim(),
  };
}


// ===== Showtimes CRUD =====
let _showtimesCache = [];
let _roomsCache = [];
async function fetchAdminShowtimes() {
  const res = await adminFetch("/api/admin/showtimes");
  if (!res) return null;
  const data = await safeJson(res);
  _showtimesCache = Array.isArray(data) ? data : [];
  return _showtimesCache;
}
async function fetchAdminRooms() {
  const res = await adminFetch("/api/admin/rooms");
  if (!res) return null;
  const data = await safeJson(res);
  _roomsCache = Array.isArray(data) ? data : [];
  return _roomsCache;
}
async function loadShowtimes() {
  await Promise.all([fetchAdminShowtimes(), fetchAdminMovies(), fetchAdminRooms()]);
  renderShowtimeTable(_showtimesCache);
}
function renderShowtimeTable(list) {
  const tbody = document.querySelector("#showtimeTable tbody");
  tbody.innerHTML = "";
  list.forEach(s => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${s.showtimeId ?? ""}</td>
      <td>
        <div style="font-weight:900">${escapeHtml(s.title || "")}</div>
        <div class="muted">movieId: ${s.movieId ?? ""}</div>
      </td>
      <td>
        <div style="font-weight:900">${escapeHtml(s.roomName || "")}</div>
        <div class="muted">roomId: ${s.roomId ?? ""}</div>
      </td>
      <td>${formatDateTime(s.startTime)}</td>
      <td>${formatDateTime(s.endTime)}</td>
      <td>
        <div class="actions">
          <button class="btn-sm primary" data-act="edit" data-id="${s.showtimeId}">Edit</button>
          <button class="btn-sm danger" data-act="del" data-id="${s.showtimeId}">Delete</button>
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  });
  tbody.querySelectorAll("button").forEach(btn => {
    const id = Number(btn.dataset.id);
    const act = btn.dataset.act;
    if (act === "edit") btn.addEventListener("click", () => openShowtimeEdit(id));
    if (act === "del") btn.addEventListener("click", () => deleteShowtime(id));
  });
}

document.getElementById("btnShowtimeAdd").addEventListener("click", async () => {
  await Promise.all([fetchAdminMovies(), fetchAdminRooms()]);
  openModal("Add Showtime",
    showtimeFormHtml({ movieId:"", roomId:"", startTime:"", endTime:"", basePrice: 70000 }),
    `
      <button class="btn btn-ghost" id="sCancel">Cancel</button>
      <button class="btn" id="sSave">Create</button>
    `
  );
  document.getElementById("sCancel").onclick = closeModal;
  document.getElementById("sSave").onclick = async () => {
    const payload = readShowtimeForm(); // LocalDateTime string
    const res = await adminFetch("/api/admin/showtimes", { method:"POST", body: JSON.stringify(payload) });
    if (!res) return;
    if (!res.ok) { toast("Create showtime failed"); return; }
    closeModal();
    toast("Created");
    await loadShowtimes();
    await loadDashboard();
  };
});

function openShowtimeEdit(id) {
  const s = _showtimesCache.find(x => x.showtimeId === id);
  if (!s) return;
  openModal(`Edit Showtime #${id}`,
    showtimeFormHtml({
      movieId: s.movieId,
      roomId: s.roomId,
      startTime: toLocalInputValue(s.startTime),
      endTime: toLocalInputValue(s.endTime),
      basePrice: 70000
    }),
    `
      <button class="btn btn-ghost" id="sCancel">Cancel</button>
      <button class="btn" id="sSave">Save</button>
    `
  );
  document.getElementById("sCancel").onclick = closeModal;
  document.getElementById("sSave").onclick = async () => {
    const payload = readShowtimeForm();
    const res = await adminFetch(`/api/admin/showtimes/${id}`, { method:"PUT", body: JSON.stringify(payload) });
    if (!res) return;
    if (!res.ok) { toast("Update showtime failed"); return; }
    closeModal();
    toast("Saved");
    await loadShowtimes();
    await loadDashboard();
  };
}

async function deleteShowtime(id) {
  if (!confirm(`Delete showtime #${id}?`)) return;
  const res = await adminFetch(`/api/admin/showtimes/${id}`, { method:"DELETE" });
  if (!res) return;
  if (!res.ok && res.status !== 204) { toast("Delete failed"); return; }
  toast("Deleted");
  await loadShowtimes();
  await loadDashboard();
}

function showtimeFormHtml(s) {
  // LocalDateTime input: dùng datetime-local (yyyy-MM-ddTHH:mm)
  return `
    <div class="form-grid">
      <div class="field">
        <label>Movie</label>
        <select id="stMovieId" class="select">
          <option value="">-- select --</option>
          ${_moviesCache.map(m => `<option value="${m.movieId}" ${String(m.movieId)===String(s.movieId)?"selected":""}>#${m.movieId} - ${escapeHtml(m.title||"")}</option>`).join("")}
        </select>
      </div>

      <div class="field">
        <label>Room</label>
        <select id="stRoomId" class="select">
          <option value="">-- select --</option>
          ${_roomsCache.map(r => `<option value="${r.roomId}" ${String(r.roomId)===String(s.roomId)?"selected":""}>#${r.roomId} - ${escapeHtml(r.roomName||"")}</option>`).join("")}
        </select>
      </div>

      <div class="field">
        <label>Start time</label>
        <input id="stStart" class="input" type="datetime-local" value="${escapeAttr(s.startTime||"")}" />
      </div>

      <div class="field">
        <label>End time (optional)</label>
        <input id="stEnd" class="input" type="datetime-local" value="${escapeAttr(s.endTime||"")}" />
      </div>

      <div class="field span-2">
        <label>Base price</label>
        <input id="stPrice" class="input" type="number" min="0" value="${Number(s.basePrice ?? 70000)}" />
        <div class="muted" style="margin-top:6px">
          Backend nhận LocalDateTime JSON (vd: "2026-01-31T19:00:00")
        </div>
      </div>
    </div>
  `;
}

function readShowtimeForm() {
  const movieId = Number(document.getElementById("stMovieId").value);
  const roomId = Number(document.getElementById("stRoomId").value);
  const start = document.getElementById("stStart").value; // yyyy-MM-ddTHH:mm
  const end = document.getElementById("stEnd").value;
  const price = Number(document.getElementById("stPrice").value || 0);

  // Convert to LocalDateTime string with seconds ":00"
  const toLdt = (v) => v ? (v.length === 16 ? (v + ":00") : v) : null;

  return {
    movieId,
    roomId,
    startTime: toLdt(start),
    endTime: toLdt(end),
    basePrice: price
  };
}

// ===== Rooms CRUD =====
async function loadRooms() {
  const data = await fetchAdminRooms();
  renderRoomTable(data || []);
  await loadSeatRoomSelect(); // keep seats tab in sync
}
function renderRoomTable(list) {
  const tbody = document.querySelector("#roomTable tbody");
  tbody.innerHTML = "";
  list.forEach(r => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${r.roomId ?? ""}</td>
      <td style="font-weight:900">${escapeHtml(r.roomName || "")}</td>
      <td><span class="badge">${escapeHtml(r.screenType || "")}</span></td>
      <td>
        <div class="actions">
          <button class="btn-sm primary" data-act="edit" data-id="${r.roomId}">Edit</button>
          <button class="btn-sm danger" data-act="del" data-id="${r.roomId}">Delete</button>
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  });
  tbody.querySelectorAll("button").forEach(btn => {
    const id = Number(btn.dataset.id);
    const act = btn.dataset.act;
    if (act === "edit") btn.addEventListener("click", () => openRoomEdit(id));
    if (act === "del") btn.addEventListener("click", () => deleteRoom(id));
  });
}

document.getElementById("btnRoomAdd").addEventListener("click", () => {
  openModal("Add Room",
    roomFormHtml({ roomName:"", screenType:"2D" }),
    `
      <button class="btn btn-ghost" id="rCancel">Cancel</button>
      <button class="btn" id="rSave">Create</button>
    `
  );
  document.getElementById("rCancel").onclick = closeModal;
  document.getElementById("rSave").onclick = async () => {
    const payload = readRoomForm();
    const res = await adminFetch("/api/admin/rooms", { method:"POST", body: JSON.stringify(payload) });
    if (!res) return;
    if (!res.ok) { toast("Create room failed"); return; }
    closeModal();
    toast("Created");
    await loadRooms();
    await loadDashboard();
  };
});

function openRoomEdit(id) {
  const r = _roomsCache.find(x => x.roomId === id);
  if (!r) return;
  openModal(`Edit Room #${id}`,
    roomFormHtml(r),
    `
      <button class="btn btn-ghost" id="rCancel">Cancel</button>
      <button class="btn" id="rSave">Save</button>
    `
  );
  document.getElementById("rCancel").onclick = closeModal;
  document.getElementById("rSave").onclick = async () => {
    const payload = readRoomForm();
    payload.roomId = id;
    const res = await adminFetch(`/api/admin/rooms/${id}`, { method:"PUT", body: JSON.stringify(payload) });
    if (!res) return;
    if (!res.ok) { toast("Update room failed"); return; }
    closeModal();
    toast("Saved");
    await loadRooms();
    await loadDashboard();
  };
}

async function deleteRoom(id) {
  if (!confirm(`Delete room #${id}?`)) return;
  const res = await adminFetch(`/api/admin/rooms/${id}`, { method:"DELETE" });
  if (!res) return;
  if (!res.ok && res.status !== 204) { toast("Delete failed"); return; }
  toast("Deleted");
  await loadRooms();
  await loadDashboard();
}

function roomFormHtml(r) {
  return `
    <div class="form-grid">
      <div class="field span-2">
        <label>Room name</label>
        <input id="rmName" class="input" value="${escapeAttr(r.roomName||"")}" />
      </div>
      <div class="field span-2">
        <label>Screen type</label>
        <input id="rmType" class="input" value="${escapeAttr(r.screenType||"")}" placeholder="2D / 3D / IMAX ..." />
      </div>
    </div>
  `;
}
function readRoomForm() {
  return {
    roomName: document.getElementById("rmName").value.trim(),
    screenType: document.getElementById("rmType").value.trim(),
  };
}

// ===== Seats (Generate/Clear + view) =====
async function loadSeatRoomsAndGrid() {
  await fetchAdminRooms();
  await loadSeatRoomSelect();
  await loadSeatGrid();
}
async function loadSeatRoomSelect() {
  const sel = document.getElementById("seatRoomSelect");
  if (!sel) return;
  sel.innerHTML = _roomsCache.map(r => `<option value="${r.roomId}">#${r.roomId} - ${escapeHtml(r.roomName||"")}</option>`).join("");
}
document.getElementById("seatRoomSelect").addEventListener("change", loadSeatGrid);

document.getElementById("btnGenerateSeats").addEventListener("click", async () => {
  const roomId = Number(document.getElementById("seatRoomSelect").value);
  const rows = Number(document.getElementById("seatRows").value);
  const cols = Number(document.getElementById("seatCols").value);
  const seatType = document.getElementById("seatType").value;

  const url = `/api/admin/rooms/${roomId}/seats/generate?rows=${rows}&cols=${cols}&seatType=${encodeURIComponent(seatType)}`;
  const res = await adminFetch(url, { method:"POST" });
  if (!res) return;
  if (!res.ok) { toast("Generate failed"); return; }
  toast("Generated");
  await loadSeatGrid();
});

document.getElementById("btnClearSeats").addEventListener("click", async () => {
  const roomId = Number(document.getElementById("seatRoomSelect").value);
  if (!confirm(`Clear all seats in room #${roomId}?`)) return;

  const res = await adminFetch(`/api/admin/rooms/${roomId}/seats`, { method:"DELETE" });
  if (!res) return;
  if (!res.ok && res.status !== 204) { toast("Clear failed"); return; }
  toast("Cleared");
  await loadSeatGrid();
});

document.getElementById("btnReloadSeats").addEventListener("click", loadSeatGrid);

async function loadSeatGrid() {
  const roomId = Number(document.getElementById("seatRoomSelect").value);
  if (!roomId) return;

  // seats list lấy từ public endpoint: GET /api/rooms/{roomId}/seats
  const res = await fetch(`/api/rooms/${roomId}/seats`);
  const seats = await safeJson(res);
  const list = Array.isArray(seats) ? seats : [];

  // sort by rowIndex, colIndex
  list.sort((a,b) => (a.rowIndex-b.rowIndex) || (a.colIndex-b.colIndex));

  document.getElementById("seatMeta").textContent = `Room #${roomId} • seats: ${list.length}`;
  const grid = document.getElementById("seatGrid");
  grid.innerHTML = "";

  list.forEach(s => {
    const div = document.createElement("div");
    div.className = `seat ${(s.seatType||"").toUpperCase()==="VIP" ? "vip" : ""}`;
    div.title = `#${s.seatId} • ${s.seatType} • row ${s.rowIndex} col ${s.colIndex}`;
    div.textContent = s.seatCode || "";
    grid.appendChild(div);
  });
}

// ===== bootstrap =====
showPage("dashboard"); // default load

// ===== utils =====
function escapeHtml(s) {
  return String(s).replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;");
}
function escapeAttr(s) {
  return escapeHtml(s).replaceAll('"', "&quot;");
}
function formatDateTime(v) {
  if (!v) return "";
  // backend trả ISO "2026-01-31T19:00:00"
  return String(v).replace("T", " ").slice(0, 19);
}
function toLocalInputValue(v) {
  if (!v) return "";
  // "2026-01-31T19:00:00" -> "2026-01-31T19:00"
  const s = String(v);
  return s.length >= 16 ? s.slice(0,16) : s;
}

// ================== REVENUE (ADD-ON, keep old code) ==================
(function initRevenueUI() {
  // Endpoints theo backend bạn đang dùng
  const API_DAILY = "/api/admin/staff/revenue/daily";
  const API_MONTHLY = "/api/admin/staff/revenue/monthly";

  // Elements (đã có trong admin_dashboard.html)
  const elFrom = document.getElementById("revFrom");
  const elTo = document.getElementById("revTo");
  const elYear = document.getElementById("revYear");

  const elDailyTotal = document.getElementById("revDailyTotal");
  const elDailyOrders = document.getElementById("revDailyOrders");
  const elMonthlyTotal = document.getElementById("revMonthlyTotal");
  const elMonthlyOrders = document.getElementById("revMonthlyOrders");

  const elDailyEmpty = document.getElementById("revDailyEmpty");
  const elMonthlyEmpty = document.getElementById("revMonthlyEmpty");

  const btnDaily = document.getElementById("btnRevDaily");
  const btnMonthly = document.getElementById("btnRevMonthly");
  const btnReloadAll = document.getElementById("btnRevReloadAll");

  const btnToday = document.getElementById("btnToday");
  const btn7Days = document.getElementById("btn7Days");
  const btnThisMonth = document.getElementById("btnThisMonth");

  // Nếu trang chưa có section revenue thì thôi (an toàn)
  if (!elFrom || !elTo || !elYear || !btnDaily || !btnMonthly) return;

  // Helpers
  const fmtMoney = (n) => (Number(n || 0)).toLocaleString("vi-VN") + " đ";
  const toISODate = (d) => {
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
  };

  async function fetchRevenueJson(url) {
    const res = await adminFetch(url, { method: "GET" });
    if (!res) return null;

    // Hiện lỗi cũ rõ ràng (400 do sai format ngày, 500 do query, v.v.)
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      toast(`Revenue API lỗi ${res.status}`);
      console.error("Revenue API error:", res.status, text);
      return null;
    }
    return await safeJson(res);
  }

  // Chart.js instances
  let chartDaily = null;
  let chartMonthly = null;

  function destroyChart(ch) {
    if (ch) try { ch.destroy(); } catch (e) {}
  }

  function renderDailyChart(labels, values) {
    const canvas = document.getElementById("chartDaily");
    if (!canvas || !window.Chart) return;

    destroyChart(chartDaily);
    chartDaily = new Chart(canvas, {
      type: "bar",
      data: {
        labels,
        datasets: [{ label: "Doanh thu", data: values }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          x: {
            ticks: {
              color: "rgba(255,255,255,.85)",
              font: { size: 12, weight: "600" }
            },
            grid: { color: "rgba(255,255,255,.06)" }
          },
          y: {
            ticks: {
              color: "rgba(255,255,255,.85)",
              callback: (v) => Number(v).toLocaleString("vi-VN")
            },
            grid: { color: "rgba(255,255,255,.06)" }
          }
        }
      }
    });
  }

  function renderMonthlyChart(labels, values) {
    const canvas = document.getElementById("chartMonthly");
    if (!canvas || !window.Chart) return;

    destroyChart(chartMonthly);
    chartMonthly = new Chart(canvas, {
      type: "line",
      data: {
        labels,
        datasets: [{ label: "Doanh thu", data: values }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          x: {
            ticks: {
              color: "rgba(255, 255, 255, 0.85)",
              font: { size: 12, weight: "600" }
            },
            grid: { color: "rgba(255,255,255,.06)" }
          },
          y: {
            ticks: {
              color: "rgba(255,255,255,.85)",
              callback: (v) => Number(v).toLocaleString("vi-VN")
            },
            grid: { color: "rgba(255,255,255,.06)" }
          }
        }
      }
    });
  }

  async function loadDaily() {
    const from = elFrom.value; // type=date => yyyy-MM-dd
    const to = elTo.value;

    if (!from || !to) { toast("Chọn ngày"); return; }
    if (from > to) { toast("from phải <= to"); return; }

    const data = await fetchRevenueJson(`${API_DAILY}?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`);
    if (!data) return;

    elDailyTotal.textContent = fmtMoney(data.totalRevenue);
    elDailyOrders.textContent = `${data.totalOrders} đơn`;

    const points = Array.isArray(data.points) ? data.points : [];
    const labels = points.map(p => p.label);
    const values = points.map(p => Number(p.revenue || 0));

    const hasAny = values.some(v => v > 0);
    if (elDailyEmpty) elDailyEmpty.style.display = hasAny ? "none" : "block";

    renderDailyChart(labels, values);
  }

  async function loadMonthly() {
    const year = Number(elYear.value || 0);
    if (!year) { toast("Chọn năm"); return; }

    const data = await fetchRevenueJson(`${API_MONTHLY}?year=${encodeURIComponent(year)}`);
    if (!data) return;

    elMonthlyTotal.textContent = fmtMoney(data.totalRevenue);
    elMonthlyOrders.textContent = `${data.totalOrders} đơn`;

    const points = Array.isArray(data.points) ? data.points : [];
    const labels = points.map(p => p.label);
    const values = points.map(p => Number(p.revenue || 0));

    const hasAny = values.some(v => v > 0);
    if (elMonthlyEmpty) elMonthlyEmpty.style.display = hasAny ? "none" : "block";

    renderMonthlyChart(labels, values);
  }

  // Fill year select: current ± 2
  (function initYearSelect() {
    const now = new Date();
    const y = now.getFullYear();
    elYear.innerHTML = "";
    for (let i = y - 2; i <= y + 1; i++) {
      const opt = document.createElement("option");
      opt.value = String(i);
      opt.textContent = String(i);
      if (i === y) opt.selected = true;
      elYear.appendChild(opt);
    }
  })();

  // Default date range: last 7 days
  (function initDefaultRange() {
    const now = new Date();
    const to = new Date(now);
    const from = new Date(now);
    from.setDate(from.getDate() - 6);
    elFrom.value = toISODate(from);
    elTo.value = toISODate(to);
  })();

  // Quick buttons
  btnToday?.addEventListener("click", () => {
    const d = new Date();
    const iso = toISODate(d);
    elFrom.value = iso;
    elTo.value = iso;
    loadDaily();
  });

  btn7Days?.addEventListener("click", () => {
    const d = new Date();
    const to = new Date(d);
    const from = new Date(d);
    from.setDate(from.getDate() - 6);
    elFrom.value = toISODate(from);
    elTo.value = toISODate(to);
    loadDaily();
  });

  btnThisMonth?.addEventListener("click", () => {
    const d = new Date();
    const year = d.getFullYear();
    const month = d.getMonth(); // 0-based
    const first = new Date(year, month, 1);
    const last = new Date(year, month + 1, 0);
    elFrom.value = toISODate(first);
    elTo.value = toISODate(last);
    elYear.value = String(year);
    Promise.all([loadDaily(), loadMonthly()]);
  });

  // Manual buttons
  btnDaily.addEventListener("click", loadDaily);
  btnMonthly.addEventListener("click", loadMonthly);
  btnReloadAll?.addEventListener("click", () => Promise.all([loadDaily(), loadMonthly()]));

  // Auto-load (để vừa vào dashboard là có chart)
  // Chỉ chạy khi Chart.js đã load
  const boot = () => Promise.all([loadDaily(), loadMonthly()]).catch(console.error);
  if (window.Chart) boot();
  else window.addEventListener("load", boot);
})();

// ================== STAFF CRUD (ADD-ON, keep old code) ==================
(function initStaffCRUD() {
  const API_BASE = "/api/admin/staff";

  const elQ = document.getElementById("staffQ");
  const btnSearch = document.getElementById("btnStaffSearch");
  const btnCreate = document.getElementById("btnStaffCreate");
  const tbody = document.getElementById("staffTbody");

  const meta = document.getElementById("staffMeta");
  const btnPrev = document.getElementById("staffPrev");
  const btnNext = document.getElementById("staffNext");
  const elPage = document.getElementById("staffPage");

  const modal = document.getElementById("staffModal");
  const modalTitle = document.getElementById("staffModalTitle");
  const modalHint = document.getElementById("staffModalHint");

  const inFullName = document.getElementById("staffFullName");
  const inEmail = document.getElementById("staffEmail");
  const inRole = document.getElementById("staffRole");
  const wrapPw = document.getElementById("staffPasswordWrap");
  const inPw = document.getElementById("staffPassword");
  const btnSave = document.getElementById("btnStaffSave");

  if (!tbody || !btnCreate) return;

  let state = { page: 0, size: 10, q: "" };
  let editingId = null;

  function openModal(mode, row) {
    editingId = (mode === "edit") ? row.userId : null;

    modalTitle.textContent = mode === "edit" ? `Sửa staff #${row.userId}` : "Thêm staff";
    modalHint.textContent = mode === "edit"
      ? "Chỉnh sửa thông tin staff. Mật khẩu không đổi ở chế độ sửa."
      : "Tạo staff mới (bắt buộc email + password).";

    inFullName.value = row?.fullName || "";
    inEmail.value = row?.email || "";
    inRole.value = row?.role || "STAFF";
    inPw.value = "";

    // create: show password, edit: hide password
    wrapPw.style.display = (mode === "edit") ? "none" : "block";

    modal.style.display = "block";
  }

  function closeModal() {
    modal.style.display = "none";
    editingId = null;
  }

  modal?.addEventListener("click", (e) => {
    const t = e.target;
    if (t && t.getAttribute && t.getAttribute("data-close") === "1") closeModal();
  });

  async function api(url, opt) {
    const res = await adminFetch(url, opt);
    if (!res) return null;
    if (!res.ok) {
      const txt = await res.text().catch(() => "");
      toast(`Staff API lỗi ${res.status}`);
      console.error("Staff API error:", res.status, txt);
      return null;
    }
    return await safeJson(res);
  }

  function esc(s) {
    return String(s ?? "").replace(/[&<>"']/g, m => ({
      "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[m]));
  }

  function renderRows(pageData) {
    const items = pageData?.content || [];
    if (!items.length) {
      tbody.innerHTML = `<tr><td colspan="6" class="cb-muted">Không có staff.</td></tr>`;
      meta.textContent = "0 kết quả";
      elPage.textContent = String((state.page || 0) + 1);
      return;
    }

    tbody.innerHTML = items.map(u => {
      const statusBadge = u.isActive
        ? `<span class="cb-badge cb-badge--on">ACTIVE</span>`
        : `<span class="cb-badge cb-badge--off">DISABLED</span>`;

      const role = esc(u.role || "");
      const name = esc(u.fullName || "");
      const email = esc(u.email || "");

      return `
        <tr>
          <td>${u.userId}</td>
          <td>${name}</td>
          <td>${email}</td>
          <td>${role}</td>
          <td>${statusBadge}</td>
          <td style="text-align:right;">
            <div class="cb-actions">
              <button class="cb-btn cb-btn--white" data-act="edit" data-id="${u.userId}">Sửa</button>
              <button class="cb-btn cb-btn--white" data-act="toggle" data-id="${u.userId}" data-active="${u.isActive}">
                ${u.isActive ? "Disable" : "Enable"}
              </button>
              <button class="cb-btn cb-btn--red" data-act="del" data-id="${u.userId}">Xoá</button>
            </div>
          </td>
        </tr>
      `;
    }).join("");

    meta.textContent = `Tổng: ${pageData.totalElements} | Trang ${pageData.number + 1}/${pageData.totalPages}`;
    elPage.textContent = String(pageData.number + 1);

    // enable/disable paging buttons
    btnPrev.disabled = pageData.first;
    btnNext.disabled = pageData.last;
  }

  async function load() {
    const q = (state.q || "").trim();
    const params = new URLSearchParams();
    if (q) params.set("q", q);
    params.set("page", String(state.page));
    params.set("size", String(state.size));

    const data = await api(`${API_BASE}?${params.toString()}`, { method: "GET" });
    if (!data) return;
    renderRows(data);
  }

  async function createStaff() {
    const fullName = inFullName.value.trim();
    const email = inEmail.value.trim();
    const role = inRole.value;
    const password = inPw.value;

    if (!email) { toast("Thiếu email"); return; }
    if (!password || password.length < 6) { toast("Mật khẩu >= 6 ký tự"); return; }

    const payload = { fullName, email, role, password };
    const data = await api(API_BASE, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (!data) return;
    toast("Tạo staff thành công");
    closeModal();
    await load();
  }

  async function updateStaff(id) {
    const fullName = inFullName.value.trim();
    const email = inEmail.value.trim();
    const role = inRole.value;

    if (!email) { toast("Thiếu email"); return; }

    const payload = { fullName, email, role };
    const data = await api(`${API_BASE}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (!data) return;
    toast("Cập nhật staff thành công");
    closeModal();
    await load();
  }

  async function toggleStatus(id, currentActive) {
    const next = !currentActive;
    const payload = { isActive: next }; // SetStaffStatusRequest
    const data = await api(`${API_BASE}/${id}/status`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (!data) return;
    toast(next ? "Đã Enable" : "Đã Disable");
    await load();
  }

  async function deleteStaff(id) {
    if (!confirm(`Xoá staff #${id}?`)) return;
    const res = await adminFetch(`${API_BASE}/${id}`, { method: "DELETE" });
    if (!res) return;
    if (!res.ok) {
      const txt = await res.text().catch(() => "");
      toast(`Xoá thất bại (${res.status})`);
      console.error("delete staff error:", res.status, txt);
      return;
    }
    toast("Đã xoá staff");
    await load();
  }

  // events
  btnCreate.addEventListener("click", () => openModal("create", null));
  btnSearch?.addEventListener("click", () => { state.q = elQ.value; state.page = 0; load(); });
  elQ?.addEventListener("keydown", (e) => { if (e.key === "Enter") { state.q = elQ.value; state.page = 0; load(); } });

  btnPrev?.addEventListener("click", () => { state.page = Math.max(0, state.page - 1); load(); });
  btnNext?.addEventListener("click", () => { state.page = state.page + 1; load(); });

  btnSave.addEventListener("click", () => {
    if (editingId) updateStaff(editingId);
    else createStaff();
  });

  tbody.addEventListener("click", async (e) => {
    const btn = e.target?.closest?.("button[data-act]");
    if (!btn) return;
    const act = btn.getAttribute("data-act");
    const id = Number(btn.getAttribute("data-id"));
    if (!id) return;

    if (act === "edit") {
      // lấy row data từ dom: gọi lại list để có data (đơn giản)
      // nhanh nhất: fetch page hiện tại rồi tìm id
      const q = (state.q || "").trim();
      const params = new URLSearchParams();
      if (q) params.set("q", q);
      params.set("page", String(state.page));
      params.set("size", String(state.size));
      const pageData = await api(`${API_BASE}?${params.toString()}`, { method: "GET" });
      const row = pageData?.content?.find(x => x.userId === id);
      if (!row) { toast("Không tìm thấy staff"); return; }
      openModal("edit", row);
    }

    if (act === "toggle") {
      const currentActive = (btn.getAttribute("data-active") === "true");
      toggleStatus(id, currentActive);
    }

    if (act === "del") {
      deleteStaff(id);
    }
  });

  // init
  load();
})();
