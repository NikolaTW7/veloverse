package com.veloverse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.veloverse.service.BicycleTypeService;

/**
 * Serves the VeloVerse Home page.
 */
@Controller
public class HomeController {

    private static final int HOME_TYPE_COUNT = 4;

    private final BicycleTypeService bicycleTypeService;

    public HomeController(BicycleTypeService bicycleTypeService) {
        this.bicycleTypeService = bicycleTypeService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredTypes",
                bicycleTypeService.findAll().stream().limit(HOME_TYPE_COUNT).toList());
        return "home";
    }
}
