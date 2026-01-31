(() => {
  const grid = document.getElementById("stGrid");
  const empty = document.getElementById("stEmpty");
  const datePick = document.getElementById("datePick");
  const btnReload = document.getElementById("btnReload");

  // ===== utils =====
  function esc(s) {
    return String(s ?? "").replace(/[&<>"']/g, m => ({
      "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    }[m]));
  }

  function fmtTime(iso) {
    const d = new Date(iso);
    return d.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" });
  }

  function todayISO() {
    const d = new Date();
    return d.toISOString().slice(0, 10);
  }

  // ===== render =====
  function renderGrouped(list) {
    grid.innerHTML = "";
    empty.textContent = "";

    if (!list || list.length === 0) {
      empty.textContent = "Không có suất chiếu trong ngày này.";
      return;
    }

    // group theo movieId
    const map = {};
    list.forEach(it => {
      if (!map[it.movieId]) map[it.movieId] = [];
      map[it.movieId].push(it);
    });

    Object.values(map).forEach(items => {
      const m = items[0];

      const card = document.createElement("div");
      card.className = "cb-st-movie";
      card.innerHTML = `
        <div class="cb-st-left">
          <img class="cb-st-poster"
               src="${esc(m.posterUrl || '/img/no-poster.png')}"
               alt="${esc(m.title)}">
        </div>

        <div class="cb-st-right">
          <div class="cb-st-title">${esc(m.title)}</div>
          <div class="cb-muted" style="margin:6px 0 10px;">
            ${m.runtime ? esc(m.runtime) + " phút" : ""} 
            ${m.status ? " · " + esc(m.status) : ""}
          </div>

          <div class="cb-st-slots">
            ${items.map(s => `
              <a href="/seatmap/${s.showtimeId}"
                 class="cb-st-slot">
                ${fmtTime(s.startTime)}
              </a>
            `).join("")}
          </div>
        </div>
      `;

      grid.appendChild(card);
    });
  }

  // ===== load =====
  async function load() {
    const date = datePick.value || todayISO();
    empty.textContent = "Đang tải lịch chiếu...";
    grid.innerHTML = "";

    try {
      const res = await fetch(`/api/showtimes/by-date?date=${date}`);
      if (!res.ok) throw new Error(res.status);

      const data = await res.json();
      renderGrouped(data);
    } catch (e) {
      empty.textContent = "Lỗi tải lịch chiếu.";
      console.error(e);
    }
  }

  // ===== init =====
  datePick.value = todayISO();
  btnReload.addEventListener("click", load);
  datePick.addEventListener("change", load);

  load();
})();
