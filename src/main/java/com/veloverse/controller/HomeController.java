package com.veloverse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.veloverse.service.BicycleService;
import com.veloverse.service.BicycleTypeService;

/**
 * Serves the VeloVerse Home page.
 */
@Controller
public class HomeController {

    private static final int HOME_TYPE_COUNT = 4;
    private static final int HOME_BICYCLE_COUNT = 3;

    private final BicycleTypeService bicycleTypeService;
    private final BicycleService bicycleService;

    public HomeController(BicycleTypeService bicycleTypeService, BicycleService bicycleService) {
        this.bicycleTypeService = bicycleTypeService;
        this.bicycleService = bicycleService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredTypes",
                bicycleTypeService.findAll().stream().limit(HOME_TYPE_COUNT).toList());
        // The first bicycles by ID fill the Featured Models section
        model.addAttribute("featuredBicycles",
                bicycleService.findAll().stream().limit(HOME_BICYCLE_COUNT).toList());
        return "home";
    }
}
