package com.example.cinebooking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {
@GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Trang chủ rạp chiếu phim");
        return "home"; // -> home.html trong thư mục templates
    }
    @GetMapping("/trangchu")
    public String trangchu(Model model) {
        model.addAttribute("pageTitle", "Trang chủ rạp chiếu phim");
        return "trangchu"; // -> home.html trong thư mục templates
    }
}
