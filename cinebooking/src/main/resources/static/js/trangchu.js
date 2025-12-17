// Dữ liệu demo (sau này thay bằng API từ Spring Boot)
const MOVIES = [
  {
    id: 1,
    category: "NOW_SHOWING",
    title: "Bộ gia",
    genre: "Tình cảm",
    duration: 100,
    releaseDate: "12-07-2025",
    poster:
      "https://thethaovanhoa.mediacdn.vn/372676912336973824/2023/8/28/3dcg-shin-cau-be-but-chi-dai-chien-sieu-nang-luc-sushi-bay-16932064940051318927628.jpeg",
  },
  {
    id: 2,
    category: "NOW_SHOWING",
    title: "ABC",
    genre: "Hành động",
    duration: 120,
    releaseDate: "10-07-2025",
    poster:
      "https://thethaovanhoa.mediacdn.vn/372676912336973824/2023/8/28/3dcg-shin-cau-be-but-chi-dai-chien-sieu-nang-luc-sushi-bay-16932064940051318927628.jpeg",
  },
  {
    id: 3,
    category: "COMING_SOON",
    title: "XYZ",
    genre: "Kinh dị",
    duration: 95,
    releaseDate: "20-07-2025",
    poster:
      "https://thethaovanhoa.mediacdn.vn/372676912336973824/2023/8/28/3dcg-shin-cau-be-but-chi-dai-chien-sieu-nang-luc-sushi-bay-16932064940051318927628.jpeg",
  },
  {
    id: 4,
    category: "SPECIAL",
    title: "Suất đặc biệt 1",
    genre: "Sự kiện",
    duration: 110,
    releaseDate: "15-07-2025",
    poster:
      "https://thethaovanhoa.mediacdn.vn/372676912336973824/2023/8/28/3dcg-shin-cau-be-but-chi-dai-chien-sieu-nang-luc-sushi-bay-16932064940051318927628.jpeg",
  },
];

const grid = document.getElementById("movieGrid");
const tabs = document.querySelectorAll(".tab");

function renderMovies(filter) {
  const list = MOVIES.filter((m) => m.category === filter);

  if (list.length === 0) {
    grid.innerHTML = `<div style="grid-column: 1 / -1; color:#ccc; padding:12px 0;">
      Chưa có phim trong mục này.
    </div>`;
    return;
  }

  grid.innerHTML = list
    .map(
      (m) => `
      <div class="movie-card">
        <div class="poster" style="background-image:url('${m.poster}')"></div>
        <div class="meta">
          <div><b>Tên phim</b>: ${m.title}</div>
          <div><b>Thể loại</b>: ${m.genre}</div>
          <div><b>Thời lượng</b>: ${m.duration}p</div>
          <div><b>Ngày chiếu</b>: ${m.releaseDate}</div>
        </div>
      </div>
    `
    )
    .join("");
}

function setActiveTab(clickedBtn) {
  tabs.forEach((t) => t.classList.remove("active"));
  clickedBtn.classList.add("active");
}

tabs.forEach((btn) => {
  btn.addEventListener("click", () => {
    const filter = btn.dataset.filter;
    setActiveTab(btn);
    renderMovies(filter);
  });
});

// Mặc định load "Phim đang chiếu"
renderMovies("NOW_SHOWING");
// ===== SLIDER (Trailer + Poster) =====

// Lưu ý: trailer YouTube muốn autoplay thường phải mute=1

const SLIDES = [
  {
    type: "video",
    title: "Trailer 1",
    src: "https://youtube.com/embed/NgPOfHm2bvY?autoplay=1&mute=1&controls=1&rel=0"
  },
  {
    type: "image",
    title: "Banner 1",
    src: "https://azoka.vn/files/2023/10/kich-thuoc-poster-phim.jpg"
  },
  {
    type: "video",
    title: "Trailer 2",
    src: "https://www.youtube.com/embed/JZ5FqFH_9kU?autoplay=1&mute=1&controls=1&rel=0"
  },
  {
    type: "image",
    title: "Banner 2",
    src: "https://images.unsplash.com/photo-1517602302552-471fe67acf66?q=80&w=1400&auto=format&fit=crop"
  }
];

const slidesEl = document.getElementById("slides");
const dotsEl = document.getElementById("dots");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const slider = document.getElementById("slider");

let current = 0;
let timer = null;
const INTERVAL_MS = 25000;

function buildSlide(s) {
  if (s.type === "image") {
    return `<div class="slide bg" style="background-image:url('${s.src}')"></div>`;
  }
  // video
  return `
    <div class="slide">
      <iframe class="slide-media"
        src="${s.src}"
        title="${s.title || "Trailer"}"
        allow="autoplay; encrypted-media; picture-in-picture"
        allowfullscreen>
      </iframe>
    </div>
  `;
}

function renderSlider() {
  slidesEl.innerHTML = SLIDES.map(buildSlide).join("");

  dotsEl.innerHTML = SLIDES.map((_, i) =>
    `<span class="dot ${i === current ? "active" : ""}" data-index="${i}"></span>`
  ).join("");

  updateSlide();
}

function updateSlide() {
  slidesEl.style.transform = `translateX(-${current * 100}%)`;

  // update dots
  [...dotsEl.querySelectorAll(".dot")].forEach((d, i) => {
    d.classList.toggle("active", i === current);
  });

  // DỪNG tất cả video KHÔNG phải slide hiện tại
  SLIDES.forEach((s, i) => {
    if (s.type !== "video") return;

    const slide = slidesEl.children[i];
    if (!slide) return;

    const iframe = slide.querySelector("iframe");
    if (!iframe) return;

    if (i !== current) {
      const src = iframe.getAttribute("src");
      iframe.setAttribute("src", src); // reload iframe => dừng video
    }
  });
}


function goTo(index) {
  current = (index + SLIDES.length) % SLIDES.length;
  updateSlide();
  restartAuto();
}
function next() { goTo(current + 1); }
function prev() { goTo(current - 1); }

function startAuto() {
  timer = setInterval(next, INTERVAL_MS);
}
function stopAuto() {
  if (timer) clearInterval(timer);
  timer = null;
}
function restartAuto() {
  stopAuto();
  startAuto();
}

prevBtn?.addEventListener("click", prev);
nextBtn?.addEventListener("click", next);

dotsEl?.addEventListener("click", (e) => {
  const dot = e.target.closest(".dot");
  if (!dot) return;
  goTo(Number(dot.dataset.index));
});

// Hover thì dừng, rời chuột thì chạy tiếp
slider?.addEventListener("mouseenter", stopAuto);
slider?.addEventListener("mouseleave", startAuto);

renderSlider();
startAuto();
