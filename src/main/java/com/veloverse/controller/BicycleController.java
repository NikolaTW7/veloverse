package com.veloverse.controller;

// Spring MVC types: web annotations, the Model passed to templates and HTTP error handling
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.Bicycle;
import com.veloverse.service.BicycleService;

/**
 * Handles the Bicycles list page (with search and filters) and the Bicycle Detail page.
 * A controller only deals with the web side; it asks the service layer for the data.
 */
// @Controller marks this class as a web controller: each method returns the name of a
// Thymeleaf template in src/main/resources/templates (for example "bicycles" means bicycles.html).
@Controller
public class BicycleController {

    // private: only this class can use the field. final: it is set once in the constructor and never changed.
    private final BicycleService bicycleService;

    // Constructor injection: Spring creates the BicycleService and passes it in here automatically.
    // This makes the dependency clear and lets tests pass in their own (mock) service.
    public BicycleController(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    // All parameters are optional, e.g. /bicycles?q=trek&type=2&brand=1&material=3
    // q is the search text; type, brand and material are IDs chosen in the filter dropdowns ("All" sends an empty value).
    // @GetMapping links this method to GET requests for the URL. @RequestParam reads a ?name=value
    // part of the URL; required = false means it may be missing (then the value is null).
    // They are read as String, not Integer, so a bad value such as ?type=abc is ignored instead of causing an error.
    @GetMapping("/bicycles")
    public String list(@RequestParam(name = "q", required = false) String q,
                       @RequestParam(name = "type", required = false) String type,
                       @RequestParam(name = "brand", required = false) String brand,
                       @RequestParam(name = "material", required = false) String material,
                       Model model) {
        Integer typeId = parseId(type);
        Integer brandId = parseId(brand);
        Integer materialId = parseId(material);

        // model.addAttribute makes a value available to the template under the given name, e.g. ${bicycles}
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
    // @PathVariable takes the {id} part of the URL, e.g. 5 from /bicycles/5.
    @GetMapping("/bicycles/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        // findById returns an Optional (a box that may be empty). orElseThrow gives the bicycle,
        // or, if the box is empty, throws an exception that Spring turns into a 404 Not Found page.
        Bicycle bicycle = bicycleService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicycle not found"));

        model.addAttribute("bicycle", bicycle);
        return "bicycle-detail";
    }

    /**
     * Turns a dropdown value into an ID. "All" (empty) or anything that is not a whole number means no filter.
     * At most 9 digits are accepted, so the number always fits in an Integer.
     * It has no access modifier (package-private), so CompareController in the same package can reuse it,
     * and it is static, so it can be called without a BicycleController object.
     */
    static Integer parseId(String value) {
        if (value == null || !value.matches("\\d{1,9}")) {
            return null;
        }
        return Integer.valueOf(value);
    }
}
