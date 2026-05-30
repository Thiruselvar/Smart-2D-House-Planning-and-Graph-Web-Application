package com.houseplan.controller;

import com.houseplan.repository.HousePlanRepository;
import com.houseplan.repository.UserRepository;
import com.houseplan.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final HousePlanRepository planRepository;
    private final UserService userService;

    public AdminController(UserRepository userRepository,
                           HousePlanRepository planRepository,
                           UserService userService) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.userService = userService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("plans", planRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("planCount", planRepository.count());
        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        userRepository.findById(id).ifPresent(user -> {
            if (!"admin".equalsIgnoreCase(user.getUsername())) {
                userService.deleteUser(id);
            }
        });
        redirect.addFlashAttribute("message", "User removed.");
        return "redirect:/admin";
    }

    @PostMapping("/plans/{id}/delete")
    public String deletePlan(@PathVariable Long id, RedirectAttributes redirect) {
        planRepository.deleteById(id);
        redirect.addFlashAttribute("message", "Plan removed.");
        return "redirect:/admin";
    }
}
