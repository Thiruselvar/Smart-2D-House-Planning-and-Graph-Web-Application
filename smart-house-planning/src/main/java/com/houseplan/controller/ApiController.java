package com.houseplan.controller;

import com.houseplan.model.HousePlan;
import com.houseplan.model.User;
import com.houseplan.model.dto.PlanRequestDto;
import com.houseplan.service.HousePlanService;
import com.houseplan.service.PlanGeneratorService;
import com.houseplan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PlanGeneratorService planGeneratorService;
    private final HousePlanService housePlanService;
    private final UserService userService;

    public ApiController(PlanGeneratorService planGeneratorService,
                         HousePlanService housePlanService,
                         UserService userService) {
        this.planGeneratorService = planGeneratorService;
        this.housePlanService = housePlanService;
        this.userService = userService;
    }

    @PostMapping("/generate")
    public Map<String, Object> generate(@Valid @RequestBody PlanRequestDto request) {
        var result = planGeneratorService.generate(request);
        Map<String, Object> response = new HashMap<>();
        response.put("planJson", result.planJson());
        response.put("areaSummary", result.areaSummary());
        return response;
    }

    @PostMapping("/plans/{id}/layout")
    public Map<String, String> saveLayout(@PathVariable Long id,
                                          @RequestBody Map<String, Object> body,
                                          @AuthenticationPrincipal UserDetails principal) {
        User user = userService.findByUsername(principal.getUsername()).orElseThrow();
        HousePlan plan = housePlanService.findById(id)
                .filter(p -> p.getUser().getUserId().equals(user.getUserId()))
                .orElseThrow();

        String json = (String) body.get("planJson");
        double builtUp = ((Number) body.getOrDefault("builtUp", 0)).doubleValue();
        double free = ((Number) body.getOrDefault("free", 0)).doubleValue();
        housePlanService.updateRoomsFromJson(plan, json, builtUp, free);

        return Map.of("status", "ok");
    }
}
