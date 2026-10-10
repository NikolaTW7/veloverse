package com.veloverse.controller;

// Spring MVC types: web annotations, the Model passed to templates and HTTP error handling
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.BicycleType;
import com.veloverse.service.BicycleTypeService;

/**
 * Handles the Bicycle Types list page and the detail page for one type.
 */
// @Controller: Spring sends matching web requests here; each method returns a template name.
@Controller
public class BicycleTypeController {

    // How many other types to suggest at the bottom of a detail page.
    // static final makes this a constant shared by the whole class.
    private static final int RELATED_TYPE_COUNT = 3;

    // Set once by the constructor below (constructor injection) and never changed.
    private final BicycleTypeService bicycleTypeService;

    public BicycleTypeController(BicycleTypeService bicycleTypeService) {
        this.bicycleTypeService = bicycleTypeService;
    }

    // Shows templates/bicycle-types/list.html
    @GetMapping("/bicycle-types")
    public String list(Model model) {
        model.addAttribute("types", bicycleTypeService.findAll());
        return "bicycle-types/list";
    }

    // The regex only matches whole numbers, so /bicycle-types/abc becomes a normal 404 page
    @GetMapping("/bicycle-types/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        // If no type has this ID, the empty Optional becomes a 404 Not Found page
        BicycleType type = bicycleTypeService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicycle type not found"));

        model.addAttribute("type", type);
        model.addAttribute("relatedTypes", bicycleTypeService.findOthers(type, RELATED_TYPE_COUNT));
        return "bicycle-types/detail";
    }
}
