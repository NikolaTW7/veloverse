package com.veloverse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.Material;
import com.veloverse.service.MaterialService;

@Controller
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping("/materials")
    public String list(Model model) {
        model.addAttribute("materials", materialService.findAll());
        return "materials";
    }

    // The regex only matches whole numbers, so /materials/abc becomes a normal 404 page
    @GetMapping("/materials/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Material material = materialService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material not found"));

        model.addAttribute("material", material);
        return "material-detail";
    }
}
