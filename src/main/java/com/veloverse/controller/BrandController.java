package com.veloverse.controller;

// Spring MVC types: web annotations, the Model passed to templates and HTTP error handling
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.Brand;
import com.veloverse.service.BicycleService;
import com.veloverse.service.BrandService;

/**
 * Handles the Brands list page and the Brand Detail page, which also lists that brand's bicycles.
 */
// @Controller: Spring sends matching web requests here; each method returns a template name.
@Controller
public class BrandController {

    // Two services are needed: one for brand data and one for the brand's bicycles.
    // Both are private final and set by the constructor (constructor injection).
    private final BrandService brandService;
    private final BicycleService bicycleService;

    public BrandController(BrandService brandService, BicycleService bicycleService) {
        this.brandService = brandService;
        this.bicycleService = bicycleService;
    }

    // Shows templates/brands.html
    @GetMapping("/brands")
    public String list(Model model) {
        model.addAttribute("brands", brandService.findAll());
        return "brands";
    }

    // The regex only matches whole numbers, so /brands/abc becomes a normal 404 page
    @GetMapping("/brands/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        // If no brand has this ID, the empty Optional becomes a 404 Not Found page
        Brand brand = brandService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));

        model.addAttribute("brand", brand);
        model.addAttribute("bicycles", bicycleService.findByBrand(brand.getId()));
        return "brand-detail";
    }
}
