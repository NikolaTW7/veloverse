package com.veloverse.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.veloverse.model.Component;
import com.veloverse.model.ComponentSystem;
import com.veloverse.service.ComponentService;

@Controller
public class ComponentController {

    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/components")
    public String overview(Model model) {
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
        Component component = componentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Component not found"));

        List<Component> systemComponents = componentService.findSystemComponents(component);

        model.addAttribute("component", component);
        model.addAttribute("systemComponents", systemComponents);
        model.addAttribute("relatedComponents", systemComponents.stream()
                .filter(other -> !other.getId().equals(component.getId()))
                .toList());
        return "components/detail";
    }
}
