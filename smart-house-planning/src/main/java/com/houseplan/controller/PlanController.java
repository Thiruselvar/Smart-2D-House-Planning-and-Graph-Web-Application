package com.houseplan.controller;

import com.houseplan.model.HousePlan;
import com.houseplan.model.User;
import com.houseplan.model.dto.PlanRequestDto;
import com.houseplan.service.AreaCalculationService;
import com.houseplan.service.HousePlanService;
import com.houseplan.service.PdfExportService;
import com.houseplan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/plans")
public class PlanController {

    private final HousePlanService housePlanService;
    private final UserService userService;
    private final AreaCalculationService areaCalculationService;
    private final PdfExportService pdfExportService;

    public PlanController(HousePlanService housePlanService,
                          UserService userService,
                          AreaCalculationService areaCalculationService,
                          PdfExportService pdfExportService) {
        this.housePlanService = housePlanService;
        this.userService = userService;
        this.areaCalculationService = areaCalculationService;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/new")
    public String newPlanForm(Model model) {
        model.addAttribute("planRequest", new PlanRequestDto());
        model.addAttribute("planJson", "{}");
        return "plan-editor";
    }

    @GetMapping("/{id}")
    public String editPlan(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails principal,
                           Model model,
                           RedirectAttributes redirect) {
        User user = userService.findByUsername(principal.getUsername()).orElseThrow();
        return housePlanService.findById(id)
                .filter(p -> p.getUser().getUserId().equals(user.getUserId()))
                .map(plan -> {
                    PlanRequestDto dto = toDto(plan);
                    model.addAttribute("planRequest", dto);
                    model.addAttribute("planJson", plan.getGeneratedPlanData() != null ? plan.getGeneratedPlanData() : "{}");
                    model.addAttribute("areaSummary", areaCalculationService.calculate(plan));
                    model.addAttribute("planId", plan.getPlanId());
                    return "plan-editor";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Plan not found.");
                    return "redirect:/dashboard";
                });
    }

    @PostMapping("/save")
    public String savePlan(@Valid @ModelAttribute("planRequest") PlanRequestDto request,
                           BindingResult result,
                           @AuthenticationPrincipal UserDetails principal,
                           Model model,
                           RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("planJson", "{}");
            return "plan-editor";
        }
        User user = userService.findByUsername(principal.getUsername()).orElseThrow();
        try {
            HousePlan saved = housePlanService.createOrUpdate(user, request);
            redirect.addFlashAttribute("message", "Plan saved successfully.");
            return "redirect:/plans/" + saved.getPlanId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("planJson", "{}");
            return "plan-editor";
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePlan(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails principal,
                             RedirectAttributes redirect) {
        User user = userService.findByUsername(principal.getUsername()).orElseThrow();
        housePlanService.findById(id).ifPresent(plan -> {
            if (plan.getUser().getUserId().equals(user.getUserId())) {
                housePlanService.delete(plan);
            }
        });
        redirect.addFlashAttribute("message", "Plan deleted.");
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails principal) {
        User user = userService.findByUsername(principal.getUsername()).orElseThrow();
        HousePlan plan = housePlanService.findById(id)
                .filter(p -> p.getUser().getUserId().equals(user.getUserId()))
                .orElseThrow();

        byte[] pdf = pdfExportService.exportPlan(plan);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=house-plan-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private PlanRequestDto toDto(HousePlan plan) {
        PlanRequestDto dto = new PlanRequestDto();
        dto.setPlanId(plan.getPlanId());
        dto.setPlanName(plan.getPlanName());
        dto.setPlotWidth(plan.getPlotWidth());
        dto.setPlotHeight(plan.getPlotHeight());
        dto.setFloors(plan.getFloors());
        dto.setBedrooms(plan.getBedrooms());
        dto.setBathrooms(plan.getBathrooms());
        dto.setKitchen(plan.isKitchen());
        dto.setHallRequired(plan.isHallRequired());
        dto.setParking(plan.isParking());
        dto.setStaircase(plan.isStaircase());
        dto.setBalcony(plan.isBalcony());
        dto.setPreferredRoomDim(plan.getPreferredRoomDim());
        return dto;
    }
}
