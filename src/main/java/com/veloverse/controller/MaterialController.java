package com.veloverse.controller;

// Spring MVC types: web annotations, the Model passed to templates and HTTP error handling
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.Material;
import com.veloverse.service.MaterialService;

/**
 * Handles the Materials list page and the Material Detail page.
 */
// @Controller: Spring sends matching web requests here; each method returns a template name.
@Controller
public class MaterialController {

    // Set once by the constructor below (constructor injection) and never changed.
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    // Shows templates/materials.html
    @GetMapping("/materials")
    public String list(Model model) {
        model.addAttribute("materials", materialService.findAll());
        return "materials";
    }

    // The regex only matches whole numbers, so /materials/abc becomes a normal 404 page
    @GetMapping("/materials/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        // If no material has this ID, the empty Optional becomes a 404 Not Found page
        Material material = materialService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        model.addAttribute("material", material);
        return "material-detail";
    }
}
