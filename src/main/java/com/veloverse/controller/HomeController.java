package com.veloverse.controller;

// Spring MVC types: web annotations and the Model passed to templates
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.veloverse.service.BicycleService;
import com.veloverse.service.BicycleTypeService;

/**
 * Serves the VeloVerse Home page.
 */
// @Controller: Spring sends matching web requests here; the method returns a template name.
@Controller
public class HomeController {

    // How many bicycle types and bicycles to show on the Home page (constants)
    private static final int HOME_TYPE_COUNT = 4;
    private static final int HOME_BICYCLE_COUNT = 3;

    // Both services are private final and set once by the constructor (constructor injection).
    private final BicycleTypeService bicycleTypeService;
    private final BicycleService bicycleService;

    public HomeController(BicycleTypeService bicycleTypeService, BicycleService bicycleService) {
        this.bicycleTypeService = bicycleTypeService;
        this.bicycleService = bicycleService;
    }

    // Shows templates/home.html for the site root "/"
    @GetMapping("/")
    public String home(Model model) {
        // The first types by ID; stream().limit(n).toList() keeps only the first n items
        model.addAttribute("featuredTypes",
                bicycleTypeService.findAll().stream().limit(HOME_TYPE_COUNT).toList());
        // The first bicycles by ID fill the Featured Models section
        model.addAttribute("featuredBicycles",
                bicycleService.findAll().stream().limit(HOME_BICYCLE_COUNT).toList());
        return "home";
    }
}
