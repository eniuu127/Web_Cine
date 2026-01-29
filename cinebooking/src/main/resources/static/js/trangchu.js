(async function () {
  const grid = document.getElementById("movieGrid");
  const tabs = document.querySelectorAll(".cb-tab");

  let allMovies = [];
  let currentFilter = "NOW_SHOWING";

  function esc(s) {
    return String(s ?? "").replace(/[&<>"']/g, m => ({
      "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    }[m]));
  }

  function poster(m) {
    return (m.posterUrl && String(m.posterUrl).trim())
      ? m.posterUrl
      : "/image/trangchu/no-poster.png";
  }

  function render(list) {
    if (!list || list.length === 0) {
      grid.innerHTML = `<div class="cb-muted">Chưa có phim</div>`;
      return;
    }

    grid.innerHTML = list.map(m => `
      <div class="cb-movie-card" data-id="${m.movieId}">
        <div class="cb-movie-poster">
          <img src="${poster(m)}" alt="${esc(m.title)}"
               onerror="this.src='/image/trangchu/no-poster.png'">
        </div>
        <div class="cb-movie-info">
          <div class="cb-movie-title">${esc(m.title)}</div>
          <div class="cb-muted">Thời lượng: ${esc(m.runtime)} phút</div>
          <div class="cb-muted">Status: ${esc(m.status)}</div>
        </div>
      </div>
    `).join("");
  }

  // ✅ Delegation click: đặt ngoài render (full-time)
  document.addEventListener("click", (e) => {
    const card = e.target.closest(".cb-movie-card");
    if (!card) return;

    const id = card.dataset.id;
    if (id) window.location.href = `/movies/${id}`;
  });

  function applyFilter() {
    const f = (currentFilter || "").toUpperCase();

    if (f === "SPECIAL") {
      render(allMovies);
      return;
    }

    const list = allMovies.filter(m => (m.status || "").toUpperCase() === f);
    render(list);
  }

  // tab events
  tabs.forEach(btn => {
    btn.addEventListener("click", () => {
      tabs.forEach(b => b.classList.remove("cb-tab--active"));
      btn.classList.add("cb-tab--active");
      currentFilter = (btn.dataset.filter || "NOW_SHOWING").toUpperCase();
      applyFilter();
    });
  });

  // load movies
  try {
    const res = await fetch("/api/movies", { headers: { "Accept": "application/json" } });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    allMovies = await res.json();
    applyFilter();
  } catch (e) {
    grid.innerHTML = `<div class="cb-muted">Không tải được phim: ${esc(e.message)}</div>`;
  }
})();
