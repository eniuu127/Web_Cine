package com.example.cinebooking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.Booking.BookingDetailDTO;
import com.example.cinebooking.DTO.Booking.CreateBookingRequest;
import com.example.cinebooking.DTO.Booking.CreateBookingResponse;
import com.example.cinebooking.domain.entity.Booking;
import com.example.cinebooking.domain.entity.BookingItem;
import com.example.cinebooking.domain.entity.PaymentMethod;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.domain.entity.Ticket;
import com.example.cinebooking.domain.entity.User;
import com.example.cinebooking.repository.BookingItemRepository;
import com.example.cinebooking.repository.BookingRepository;
import com.example.cinebooking.repository.PaymentMethodRepository;
import com.example.cinebooking.repository.SeatRepository;
import com.example.cinebooking.repository.ShowtimeRepository;
import com.example.cinebooking.repository.TicketRepository;
import com.example.cinebooking.repository.UserRepository;

@Service
public class BookingService {

    // ===== STATUS CONSTANTS (BOOKING) =====
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final RedisSeatHoldService holdService;
    private final BookingItemRepository bookingItemRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TicketRepository ticketRepository;

    public BookingService(
            BookingRepository bookingRepository,
            ShowtimeRepository showtimeRepository,
            SeatRepository seatRepository,
            UserRepository userRepository,
            RedisSeatHoldService holdService,
            BookingItemRepository bookingItemRepository,
            PaymentMethodRepository paymentMethodRepository,
            TicketRepository ticketRepository) {

        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.holdService = holdService;
        this.bookingItemRepository = bookingItemRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * TASK 2:
     * - Nhận holdId + userId
     * - Lấy HoldPayload từ Redis
     * - Validate owner để chống “cướp hold”
     * - Tính total
     * - Tạo Booking(PENDING)
     * - Lưu BookingItem (ghế trong đơn) để phục vụ Booking Detail khi chưa PAID
     * - KHÔNG tạo Ticket (Ticket chỉ tạo khi payment success)
     */
    @Transactional
    public CreateBookingResponse createBooking(CreateBookingRequest req) {

        if (req == null) {
            throw badRequest("Request is required");
        }
        if (isBlank(req.getHoldId())) {
            throw badRequest("holdId is required");
        }
        if (req.getUserId() == null) {
            throw badRequest("userId is required (login required)");
        }

        // 1) Lấy hold từ Redis
        RedisSeatHoldService.HoldPayload hold = holdService.getHoldOrThrow(req.getHoldId());

        // 2) Hold phải thuộc user (chống cướp hold)
        if (hold.getUserId() == null) {
            throw badRequest("Guest hold is not supported (login required)");
        }
        if (!Objects.equals(hold.getUserId(), req.getUserId())) {
            throw forbidden("Hold does not belong to this user");
        }

        // 3) Validate hold payload
        if (hold.getShowtimeId() == null) {
            throw badRequest("Invalid hold: showtimeId missing");
        }
        if (hold.getSeatIds() == null || hold.getSeatIds().isEmpty()) {
            throw badRequest("Invalid hold: seatIds empty");
        }

        // 4) Lấy showtime
        Showtime showtime = showtimeRepository.findById(hold.getShowtimeId())
                .orElseThrow(() -> badRequest("Showtime not found: " + hold.getShowtimeId()));

        // 5) Lấy seats từ hold payload
        List<Seat> seats = seatRepository.findAllById(hold.getSeatIds());
        if (seats.size() != hold.getSeatIds().size()) {
            throw badRequest("Some seats not found");
        }

        // 6) Tính tổng tiền
        Integer basePrice = showtime.getBasePrice();
        if (basePrice == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Showtime basePrice is not set");
        }
        Integer totalAmount = hold.getSeatIds().size() * basePrice;

        // 7) Tạo booking (PENDING)
        Booking booking = new Booking();
        booking.setBookingCode(generateBookingCode());
        booking.setShowtime(showtime);
        booking.setHoldId(req.getHoldId());
        booking.setStatus(STATUS_PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        booking.setTotalAmount(totalAmount);

        // 8) Gán USER
        User user = userRepository.findById(hold.getUserId())
                .orElseThrow(() -> badRequest("User not found: " + hold.getUserId()));
        booking.setUser(user);

        Booking savedBooking = bookingRepository.save(booking);

        // 9) Lưu booking_items (để booking detail có ghế kể cả khi chưa PAID)
        List<BookingItem> items = seats.stream().map(seat -> {
            BookingItem bi = new BookingItem();
            bi.setBooking(savedBooking);
            bi.setSeat(seat);
            return bi;
        }).toList();

        bookingItemRepository.saveAll(items);

        // 10) Response
        CreateBookingResponse res = new CreateBookingResponse();
        res.setBookingCode(savedBooking.getBookingCode());
        res.setTotalAmount(savedBooking.getTotalAmount());
        res.setStatus(savedBooking.getStatus());
        res.setExpiresAt(savedBooking.getExpiresAt());
        res.setSeatCodes(seats.stream().map(Seat::getSeatCode).toList());
        return res;
    }

    // ===== TASK 3: BOOKING DETAIL =====
    @Transactional(readOnly = true)
    public BookingDetailDTO getBookingDetail(String bookingCode) {

        if (isBlank(bookingCode)) {
            throw badRequest("bookingCode is required");
        }

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Showtime st = booking.getShowtime();
        var movie = st.getMovie();
        var room = st.getRoom();

        // ghế từ booking_items (tồn tại cả khi chưa PAID)
        List<BookingItem> items = bookingItemRepository.findByBooking_BookingId(booking.getBookingId());

        List<BookingDetailDTO.SeatDTO> seatDtos = items.stream()
                .map(it -> new BookingDetailDTO.SeatDTO(
                        it.getSeat().getSeatId(),
                        it.getSeat().getSeatCode()))
                .toList();

        String pmCode = null;
        String pmName = null;
        if (booking.getPaymentMethod() != null) {
            pmCode = booking.getPaymentMethod().getCode();
            pmName = booking.getPaymentMethod().getName();
        }

        Integer total = booking.getTotalAmount();

        return new BookingDetailDTO(
                booking.getBookingCode(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getExpiresAt(),

                st.getShowtimeId(),
                st.getStartTime(),
                st.getEndTime(),

                movie.getMovieId(),
                movie.getTitle(),
                movie.getPosterUrl(),

                room.getRoomId(),
                room.getRoomName(),

                pmCode,
                pmName,

                total,
                seatDtos
        );
    }

    // ===== TASK 4: SET PAYMENT METHOD (chỉ cho PENDING) =====
    @Transactional
    public void setPaymentMethod(String bookingCode, String paymentMethodId) {

        if (isBlank(bookingCode)) {
            throw badRequest("bookingCode is required");
        }
        if (isBlank(paymentMethodId)) {
            throw badRequest("paymentMethodId is required");
        }

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!STATUS_PENDING.equalsIgnoreCase(booking.getStatus())) {
            throw badRequest("Cannot change payment method when booking is not PENDING");
        }

        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment method not found"));

        booking.setPaymentMethod(pm);
        bookingRepository.save(booking);
    }

    /**
     * TASK 5 (INTERNAL):
     * Hoàn tất đơn sau khi payment SUCCESS:
     * - chống double pay
     * - check hết hạn booking
     * - lấy hold từ redis + check showtime
     * - chống double sell
     * - tạo ticket
     * - update booking -> PAID
     * - release hold
     *
     * IMPORTANT: KHÔNG expose method này cho Controller.
     * Controller confirm-paid phải gọi PaymentService.confirmPaid().
     */
    @Transactional
    void finalizePaidInternal(String bookingCode) {

        if (isBlank(bookingCode)) {
            throw badRequest("bookingCode is required");
        }

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        // 1) chống double pay (idempotent)
        if (STATUS_PAID.equalsIgnoreCase(booking.getStatus())) {
            return; // đã PAID rồi thì coi như OK, không tạo ticket lại
        }
        if (STATUS_CANCELLED.equalsIgnoreCase(booking.getStatus())) {
            throw badRequest("Booking cancelled");
        }

        // 2) hết hạn booking?
        if (booking.getExpiresAt() != null && booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            // optional: có thể set CANCELLED ở đây, nhưng tránh side-effect nếu bạn đang có flow khác
            throw new ResponseStatusException(HttpStatus.GONE, "Booking expired");
        }

        // 3) lấy hold
        String holdId = booking.getHoldId();
        if (isBlank(holdId)) {
            throw badRequest("HoldId missing in booking");
        }

        RedisSeatHoldService.HoldPayload hold = holdService.getHoldOrThrow(holdId);

        // 4) check showtime khớp
        if (hold.getShowtimeId() == null
                || !hold.getShowtimeId().equals(booking.getShowtime().getShowtimeId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hold does not match showtime");
        }

        if (hold.getSeatIds() == null || hold.getSeatIds().isEmpty()) {
            throw badRequest("Invalid hold: seatIds empty");
        }

        // 5) chống double sell
        Set<Long> soldSeatIds = ticketRepository.findSeatIdsSoldByShowtimeId(booking.getShowtime().getShowtimeId());
        for (Long seatId : hold.getSeatIds()) {
            if (soldSeatIds.contains(seatId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already sold: " + seatId);
            }
        }

        // 6) tạo ticket
        List<Ticket> tickets = new ArrayList<>();
        for (Long seatId : hold.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found: " + seatId));

            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setShowtime(booking.getShowtime());
            ticket.setSeat(seat);
            ticket.setPrice(booking.getShowtime().getBasePrice());
            tickets.add(ticket);
        }
        ticketRepository.saveAll(tickets);

        // 7) update booking -> PAID
        booking.setStatus(STATUS_PAID);
        bookingRepository.save(booking);

        // 8) release hold
        holdService.releaseHold(holdId);
    }

    // ===== utils =====
    private String generateBookingCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private static ResponseStatusException forbidden(String msg) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
    }
}
