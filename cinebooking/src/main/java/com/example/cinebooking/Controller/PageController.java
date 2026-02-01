package com.example.cinebooking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    @GetMapping({"/", "/home"})
    public String root() {
        return "redirect:/trangchu";
    }

    @GetMapping("/trangchu")
    public String trangChu() {
        return "trangchu";
    }
    // trang chi tiết phim
    @GetMapping("/movies/{movieId}")
    public String movieDetail(@PathVariable Long movieId, Model model) {
        model.addAttribute("movieId", movieId);

        var bcItems = java.util.List.of(
                java.util.Map.of("label", "Phim", "href", "/trangchu"),
                java.util.Map.of("label", "Phim đang chiếu", "href", "/trangchu#now-showing")
        );

        model.addAttribute("bcItems", bcItems);
        model.addAttribute("bcCurrent", "ĐANG TẢI...");

        return "movie_detail";
    }

    // trang seat + hold
    @GetMapping("/showtimes/{showtimeId}/seats")
    public String seatmap(@PathVariable Long showtimeId, Model model) {
        model.addAttribute("showtimeId", showtimeId);

        // breadcrumb tạm (JS sẽ update đúng movieId + title sau khi load API)
    var bcItems = java.util.List.of(
                java.util.Map.of("label", "Phim", "href", "/trangchu"),
                java.util.Map.of("label", "Chi tiết phim", "href", "/trangchu")
        );
        model.addAttribute("bcItems", bcItems);
        model.addAttribute("bcCurrent", "CHỌN GHẾ");

        return "seatmap";
    }

    // login 
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/auth")
    public String authPage() {
        return "auth";
    }

    @GetMapping("/checkout/{bookingCode}")
    public String checkoutPage(@PathVariable String bookingCode, Model model) {
        model.addAttribute("bookingCode", bookingCode);
        return "checkout";
    }

    @GetMapping("/tickets/{bookingCode}")
    public String ticketSuccessPage(@PathVariable String bookingCode, Model model) {
        model.addAttribute("bookingCode", bookingCode);
        return "ticket_success";
    }

    @GetMapping("/my-bookings")
    public String myBookingsPage() {
        return "my_bookings";
    }

     @GetMapping("/showtimes")
    public String showtimes() {
        return "showtimes"; // templates/showtimes.html
    }

     @GetMapping("/prices")
    public String pricingPage() {
        return "prices"; // templates/prices.html
    }

    @GetMapping("/seatmap/{showtimeId}")
    public String seatmapPage(@PathVariable Long showtimeId, Model model) {
        model.addAttribute("showtimeId", showtimeId);
        return "seatmap"; // templates/seatmap.html
    }
}