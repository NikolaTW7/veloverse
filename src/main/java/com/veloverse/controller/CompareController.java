package com.veloverse.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.veloverse.model.Bicycle;
import com.veloverse.service.BicycleService;

/**
 * Compare Bicycles page: pick two bicycles and see their stored specifications side by side.
 * This is a neutral, educational comparison: no scores and no "winner".
 */
@Controller
public class CompareController {

    static final String MESSAGE_CHOOSE_TWO = "Please choose two bicycles to compare.";
    static final String MESSAGE_NOT_FOUND = "One of the selected bicycles could not be found. Please choose both bicycles from the lists.";
    static final String MESSAGE_SAME_BICYCLE = "You chose the same bicycle twice. Please choose two different bicycles.";

    private final BicycleService bicycleService;

    public CompareController(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    // a and b are the IDs chosen in the two dropdowns, e.g. /compare?a=1&b=6
    @GetMapping("/compare")
    public String compare(@RequestParam(name = "a", required = false) String a,
                          @RequestParam(name = "b", required = false) String b,
                          Model model) {
        // Options for both dropdowns, plus the current choices so they stay selected
        model.addAttribute("bicycles", bicycleService.findAll());
        model.addAttribute("selectedA", BicycleController.parseId(a));
        model.addAttribute("selectedB", BicycleController.parseId(b));

        String message = null;

        if (a == null && b == null) {
            // First visit: nothing chosen yet, so no message
        } else if (isBlank(a) || isBlank(b)) {
            message = MESSAGE_CHOOSE_TWO;
        } else {
            Optional<Bicycle> first = find(a);
            Optional<Bicycle> second = find(b);

            if (first.isEmpty() || second.isEmpty()) {
                message = MESSAGE_NOT_FOUND;
            } else if (first.get().getId().equals(second.get().getId())) {
                message = MESSAGE_SAME_BICYCLE;
            } else {
                model.addAttribute("bikeA", first.get());
                model.addAttribute("bikeB", second.get());
                model.addAttribute("comparedBicycles", List.of(first.get(), second.get()));
            }
        }

        model.addAttribute("message", message);
        return "compare";
    }

    /** Looks up a bicycle from a dropdown value; anything that is not a valid, existing ID gives an empty result. */
    private Optional<Bicycle> find(String value) {
        Integer id = BicycleController.parseId(value);
        return id == null ? Optional.empty() : bicycleService.findById(id);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
