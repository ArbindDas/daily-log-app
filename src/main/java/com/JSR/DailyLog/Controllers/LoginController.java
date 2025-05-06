package com.JSR.DailyLog.Controllers;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;  // Correct import for Model
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginController {

    @GetMapping("/me")
    public String userInfo(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("principal", principal);
        return "login-success";
    }

    @GetMapping("/login-success")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("principal", principal); // even if null
        return "login-success"; // Thymeleaf template
    }




}
