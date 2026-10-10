package com.veloverse.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Spring MVC types: web annotations, the Model passed to templates and HTTP error handling
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.Component;
import com.veloverse.model.ComponentSystem;
import com.veloverse.service.ComponentService;

/**
 * Handles the Components overview page (all systems and the bicycle anatomy diagram)
 * and the detail page for one component.
 */
// @Controller: Spring sends matching web requests here; each method returns a template name.
@Controller
public class ComponentController {

    // Set once by the constructor below (constructor injection) and never changed.
    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    // Shows templates/components/overview.html
    @GetMapping("/components")
    public String overview(Model model) {
        // Each system already comes with its components loaded (see ComponentSystemRepository)
        List<ComponentSystem> systems = componentService.findAllSystems();

        // Component name -> id, so the labels on the anatomy diagram can link to detail pages
        Map<String, Integer> componentIds = new HashMap<>();
        for (ComponentSystem system : systems) {
            for (Component component : system.getComponents()) {
                componentIds.put(component.getName(), component.getId());
            }
        }

        model.addAttribute("systems", systems);
        model.addAttribute("componentIds", componentIds);
        return "components/overview";
    }

    // The regex only matches whole numbers, so /components/abc becomes a normal 404 page
    @GetMapping("/components/{id:\\d{1,9}}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        // If no component has this ID, the empty Optional becomes a 404 Not Found page
        Component component = componentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Component not found"));

        // All parts of the same system (including this one) for the "System components" panel
        List<Component> systemComponents = componentService.findSystemComponents(component);

        model.addAttribute("component", component);
        model.addAttribute("systemComponents", systemComponents);
        // The same list without the current component, for the "Related Components" section
        model.addAttribute("relatedComponents", systemComponents.stream()
                .filter(other -> !other.getId().equals(component.getId()))
                .toList());
        return "components/detail";
    }
}
