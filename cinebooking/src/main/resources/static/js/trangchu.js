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

  // ===== HERO TRAILER – THÊM THỦ CÔNG =====
  (function heroTrailerSlider() {
    const slides = document.getElementById("slides");
    const dots = document.getElementById("dots");
    if (!slides) return;

    const HERO_TRAILERS = [
      { title: "Tiếng Yêu Này, Anh Dịch Được Không?", trailerUrl: "https://www.youtube.com/embed/XZ3fczVZcH0", movieId: 13 },
      { title: "Khi Cuộc Đời Cho Bạn Quả Quýt", trailerUrl: "https://www.youtube.com/embed/4ECAaQkNAbc", movieId: 12 },
      { title: "Cung Điện Ma Ám", trailerUrl: "https://www.youtube.com/embed/efgh5678", movieId: 3 },
      { title: "Ngưu Lang Chức Nữ", trailerUrl: "https://www.youtube.com/embed/ijkl9012", movieId: 4 },
      { title: "Dear Hongrang", trailerUrl: "https://www.youtube.com/embed/mnop3456", movieId: 5 }
    ];

    let idx = 0;
    let timer = null;

    function withAutoplay(url) {
      const join = url.includes("?") ? "&" : "?";
      return `${url}${join}autoplay=1&mute=1&controls=1&rel=0&modestbranding=1`;
    }

    function renderHero(i) {
      const m = HERO_TRAILERS[i];

      slides.innerHTML = `
        <div class="cb-slide cb-slide--active">
          <div class="cb-hero">
            <iframe
              src="${withAutoplay(m.trailerUrl)}"
              title="Trailer ${esc(m.title)}"
              frameborder="0"
              allow="autoplay; encrypted-media; picture-in-picture"
              allowfullscreen>
            </iframe>

            <div class="cb-hero-overlay">
              <h2>${esc(m.title)}</h2>
              <a class="cb-hero-btn" href="/movies/${m.movieId}">Xem chi tiết</a>
            </div>
          </div>
        </div>
      `;

      if (dots) {
        dots.innerHTML = HERO_TRAILERS.map((_, k) =>
          `<span class="cb-dot ${k === i ? "cb-dot--active" : ""}" data-hero-dot="${k}"></span>`
        ).join("");
      }
    }

    function next() {
      idx = (idx + 1) % HERO_TRAILERS.length;
      renderHero(idx);
    }

    function start() {
      clearInterval(timer);
      timer = setInterval(next, 8000);
    }

    document.addEventListener("click", (e) => {
      const dot = e.target.closest("[data-hero-dot]");
      if (!dot) return;
      const to = Number(dot.dataset.heroDot);
      if (Number.isNaN(to)) return;
      idx = to;
      renderHero(idx);
      start();
    });

    renderHero(idx);
    start();
  })();

  function poster(m) {
    return (m.posterUrl && String(m.posterUrl).trim())
      ? m.posterUrl
      : "/image/trangchu/no-poster.png";
  }

  // ===== Trailer Modal (CHỈ 1 BỘ) =====
  // ===== TRAILER MODAL (CENTER) =====
const trailerModal = document.getElementById("trailerModal");
const trailerBackdrop = document.getElementById("trailerBackdrop");
const trailerClose = document.getElementById("trailerClose");
const trailerTitle = document.getElementById("trailerTitle");
const trailerIframe = document.getElementById("trailerIframe");

function openTrailerModal(title, trailerUrl) {
  if (!trailerModal || !trailerIframe) {
    console.error("Trailer modal elements not found");
    return;
  }

  if (!trailerUrl) {
    alert("Phim này chưa có trailer");
    return;
  }

  trailerTitle.textContent = "TRAILER - " + title;
  trailerIframe.src = trailerUrl + "?autoplay=1";

  trailerModal.classList.add("is-open");
  trailerModal.setAttribute("aria-hidden", "false");
  document.body.style.overflow = "hidden";
}

function closeTrailerModal() {
  trailerModal.classList.remove("is-open");
  trailerModal.setAttribute("aria-hidden", "true");
  trailerIframe.src = ""; // stop video
  document.body.style.overflow = "";
}

trailerBackdrop.addEventListener("click", closeTrailerModal);
trailerClose.addEventListener("click", closeTrailerModal);
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") closeTrailerModal();
});


  // ===== render grid =====
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

          <button class="cb-trailer-btn"
                  type="button"
                  data-open-trailer="1"
                  data-trailer-url="${esc(m.trailerUrl || '')}"
                  data-title="${esc(m.title)}"
                  ${m.trailerUrl ? "" : "disabled"}
                  title="${m.trailerUrl ? "Xem trailer" : "Chưa có trailer"}">
            <span class="cb-trailer-icon">▶</span>
          </button>
        </div>

        <div class="cb-movie-info">
          <div class="cb-movie-title">${esc(m.title)}</div>
          <div class="cb-muted">Thời lượng: ${esc(m.runtime)} phút</div>
          <div class="cb-muted">Status: ${esc(m.status)}</div>
        </div>

        <div class="cb-movie-actions">
          <a class="cb-buy-btn" href="/movies/${m.movieId}" data-buy-ticket="1">MUA VÉ</a>
        </div>
      </div>
    `).join("");
  }

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

  // ✅ CHỈ add click listener 1 LẦN (không nằm trong render)
  document.addEventListener("click", (e) => {
    const tbtn = e.target.closest("[data-open-trailer]");
    if (tbtn) {
      const url = (tbtn.dataset.trailerUrl || "").trim();
      const title = tbtn.dataset.title || "";
      if (!url) return;
      openTrailerModal(title, url);
      return;
    }

    const buyBtn = e.target.closest("[data-buy-ticket]");
    if (buyBtn) return;

    const card = e.target.closest(".cb-movie-card");
    if (!card) return;

    const id = card.dataset.id;
    if (id) window.location.href = `/movies/${id}`;
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
