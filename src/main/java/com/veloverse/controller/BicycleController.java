package com.veloverse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.Bicycle;
import com.veloverse.service.BicycleService;

@Controller
public class BicycleController {

    private final BicycleService bicycleService;

    public BicycleController(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    // All parameters are optional, e.g. /bicycles?q=trek&type=2&brand=1&material=3
    // q is the search text; type, brand and material are IDs chosen in the filter dropdowns ("All" sends an empty value).
    @GetMapping("/bicycles")
    public String list(@RequestParam(name = "q", required = false) String q,
                       @RequestParam(name = "type", required = false) String type,
                       @RequestParam(name = "brand", required = false) String brand,
                       @RequestParam(name = "material", required = false) String material,
                       Model model) {
        Integer typeId = parseId(type);
        Integer brandId = parseId(brand);
        Integer materialId = parseId(material);

        model.addAttribute("bicycles", bicycleService.search(q, typeId, brandId, materialId));
        model.addAttribute("query", q == null ? "" : q.trim());

        // Dropdown options come from the database: only values that at least one bicycle uses
        model.addAttribute("types", bicycleService.findTypesInUse());
        model.addAttribute("brands", bicycleService.findBrandsInUse());
        model.addAttribute("materials", bicycleService.findFrameMaterialsInUse());

        // Remember the current choices so the dropdowns stay selected after the page reloads
        model.addAttribute("selectedType", typeId);
        model.addAttribute("selectedBrand", brandId);
        model.addAttribute("selectedMaterial", materialId);
        model.addAttribute("filtersActive", typeId != null || brandId != null || materialId != null);
        return "bicycles";
    }

    // The regex only matches whole numbers, so /bicycles/abc becomes a normal 404 page
    @GetMapping("/bicycles/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Bicycle bicycle = bicycleService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicycle not found"));

        model.addAttribute("bicycle", bicycle);
        return "bicycle-detail";
    }

    /** Turns a dropdown value into an ID. "All" (empty) or anything that is not a whole number means no filter. */
    static Integer parseId(String value) {
        if (value == null || !value.matches("\\d{1,9}")) {
            return null;
        }
        return Integer.valueOf(value);
    }
}
