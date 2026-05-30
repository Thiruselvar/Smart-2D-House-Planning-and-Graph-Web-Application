package com.houseplan.controller;

import com.houseplan.model.dto.RegisterDto;
import com.houseplan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto registerDto,
                           BindingResult result,
                           RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(registerDto);
            redirect.addFlashAttribute("message", "Registration successful. Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes redirect) {
        try {
            String token = userService.createPasswordResetToken(email);
            String resetLink = baseUrl + "/reset-password/" + token;
            redirect.addFlashAttribute("message",
                    "If an account exists, use this reset link (demo): " + resetLink);
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password/{token}")
    public String resetPasswordForm(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password/{token}")
    public String resetPassword(@PathVariable String token,
                                @RequestParam String password,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirect) {
        if (!password.equals(confirmPassword)) {
            redirect.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password/" + token;
        }
        try {
            userService.resetPassword(token, password);
            redirect.addFlashAttribute("message", "Password updated. Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/reset-password/" + token;
        }
    }
}
