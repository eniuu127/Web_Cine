package com.example.cinebooking.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Payment.PaymentResponse;
import com.example.cinebooking.DTO.Payment.SelectPaymentMethodRequest;
import com.example.cinebooking.domain.entity.Booking;
import com.example.cinebooking.domain.entity.Payment;
import com.example.cinebooking.domain.entity.PaymentMethod;
import com.example.cinebooking.repository.BookingRepository;
import com.example.cinebooking.repository.PaymentMethodRepository;
import com.example.cinebooking.repository.PaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    private final PaymentMethodRepository methodRepo;
    private final PaymentRepository paymentRepo;
    private final BookingRepository bookingRepo;

    public PaymentService(PaymentMethodRepository methodRepo,
                          PaymentRepository paymentRepo,
                          BookingRepository bookingRepo) {
        this.methodRepo = methodRepo;
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
    }
    @Transactional
    public PaymentResponse selectMethod(SelectPaymentMethodRequest req){
        Booking booking = bookingRepo.findByBookingCode(req.getBookingCode())
        .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        // chỉ cho chọn method khi booking còn hiệu lực hold seat
        if(!"PENDING".equals(booking.getStatus())){
            throw new IllegalArgumentException("Booking is not PENDING");
        }
        // nếu đã hết hạn hold ghế => không cho thanh toán
        if(booking.getExpiresAt() != null && booking.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Booking expired");
        }

        PaymentMethod method = methodRepo.findById(req.getMethodCode())
        .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        if(!Boolean.TRUE.equals(method.isActive())){
            throw new IllegalArgumentException("Payment method is not active");
        }

        // tìm payment hiện có hoặc tạo mới
        Payment payment = paymentRepo.findByBooking_BookingId(booking.getBookingId())
        .orElseGet(() -> {
            Payment p = new Payment();
            p.setBooking(booking);
            p.setStatus("INIT");
            return p;
        });
        // gắn method + amount = lấy từ booking
        payment.setPaymentMethod(method);
        payment.setAmount(booking.getTotalAmount());
        paymentRepo.save(payment);
        return toResponse(booking, payment);
    }
    // xác nhận thanh toán ( thành công )
    @Transactional
    public PaymentResponse confirmPaid (String bookingCode) {
        Booking booking = bookingRepo.findByBookingCode(bookingCode)
        .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // chi cho xác nhận khi booking dang cho thanh toan
        if(!"PENDING".equals(booking.getStatus())){
            throw new IllegalArgumentException("Booking is not PENDING");
        }

        // neu booking het han hold ghe
        if(booking.getExpiresAt() != null && booking.getExpiresAt().isBefore(LocalDateTime.now())){
            Payment payment = paymentRepo.findByBooking_BookingId(booking.getBookingId()).orElse(null);
            if(payment != null && "INIT".equals(payment.getStatus())){

                payment.setStatus("FAILED");
                paymentRepo.save(payment);

            }
            throw new IllegalArgumentException("Booking expired");
        }
        
        // phai co payment moi xac nhan ( vi user phai chon phuong thuc truoc khi confirm)
        Payment payment = paymentRepo.findByBooking_BookingId(booking.getBookingId())
        .orElseThrow(() -> new IllegalStateException("Payment not initialized. Please select method first."));
        
        // thanh toan thanh cong
        payment.setStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepo.save(payment);

        // booking -> PAID va bo hold
        booking.setStatus("PAID");
        booking.setExpiresAt(null);
        bookingRepo.save(booking);

        return toResponse(booking, payment);
    }
        private PaymentResponse toResponse(Booking booking, Payment payment){
             PaymentResponse res = new PaymentResponse();
            res.setBookingCode(booking.getBookingCode());
            res.setMethodCode(payment.getPaymentMethod().getCode());
            res.setAmount(payment.getAmount());
            res.setPaymentStatus(payment.getStatus());
            res.setBookingStatus(booking.getStatus());
            return res;
        }
}
