package com.veloverse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.Brand;
import com.veloverse.service.BicycleService;
import com.veloverse.service.BrandService;

@Controller
public class BrandController {

    private final BrandService brandService;
    private final BicycleService bicycleService;

    public BrandController(BrandService brandService, BicycleService bicycleService) {
        this.brandService = brandService;
        this.bicycleService = bicycleService;
    }

    @GetMapping("/brands")
    public String list(Model model) {
        model.addAttribute("brands", brandService.findAll());
        return "brands";
    }

    // The regex only matches whole numbers, so /brands/abc becomes a normal 404 page
    @GetMapping("/brands/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Brand brand = brandService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));

        model.addAttribute("brand", brand);
        model.addAttribute("bicycles", bicycleService.findByBrand(brand.getId()));
        return "brand-detail";
    }
}
