package com.veloverse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.BicycleType;
import com.veloverse.service.BicycleTypeService;

@Controller
public class BicycleTypeController {

    private static final int RELATED_TYPE_COUNT = 3;

    private final BicycleTypeService bicycleTypeService;

    public BicycleTypeController(BicycleTypeService bicycleTypeService) {
        this.bicycleTypeService = bicycleTypeService;
    }

    @GetMapping("/bicycle-types")
    public String list(Model model) {
        model.addAttribute("types", bicycleTypeService.findAll());
        return "bicycle-types/list";
    }

    // The regex only matches whole numbers, so /bicycle-types/abc becomes a normal 404 page
    @GetMapping("/bicycle-types/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        BicycleType type = bicycleTypeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicycle type not found"));

        model.addAttribute("type", type);
        model.addAttribute("relatedTypes", bicycleTypeService.findOthers(type, RELATED_TYPE_COUNT));
        return "bicycle-types/detail";
    }
}
