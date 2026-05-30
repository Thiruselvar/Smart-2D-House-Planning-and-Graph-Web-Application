package com.houseplan.controller;

import com.houseplan.model.User;
import com.houseplan.service.HousePlanService;
import com.houseplan.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final HousePlanService housePlanService;
    private final UserService userService;

    public DashboardController(HousePlanService housePlanService, UserService userService) {
        this.housePlanService = housePlanService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByUsername(principal.getUsername()).orElseThrow();
        model.addAttribute("plans", housePlanService.findByUser(user));
        model.addAttribute("username", user.getUsername());
        return "dashboard";
    }
}
