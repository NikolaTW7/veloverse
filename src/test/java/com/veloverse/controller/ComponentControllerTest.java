package com.veloverse.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.veloverse.model.Component;
import com.veloverse.model.ComponentSystem;
import com.veloverse.model.Material;
import com.veloverse.service.ComponentService;

/**
 * Web-layer tests for the Components & Anatomy pages (FR-02), with the service mocked.
 */
@WebMvcTest(ComponentController.class)
class ComponentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComponentService componentService;

    private final ComponentSystem drivetrain = withId(
            new ComponentSystem("Drivetrain", "Transfers power to the rear wheel."), 2);
    private final Component chain = withId(
            new Component(drivetrain, "Chain", "Linked plates and rollers.", "Transfers power.", "Pulled by the chainring."), 4);
    private final Component cassette = withId(
            new Component(drivetrain, "Cassette", "A cluster of sprockets.", "Gives a range of gears.", "The derailleur moves the chain."), 5);

    private static <T> T withId(T entity, int id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    @Test
    void overviewShowsSystemsAndLinksToComponents() throws Exception {
        drivetrain.getComponents().addAll(List.of(chain, cassette));
        when(componentService.findAllSystems()).thenReturn(List.of(drivetrain));

        mockMvc.perform(get("/components"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/overview"))
                .andExpect(content().string(containsString("Drivetrain")))
                .andExpect(content().string(containsString("id=\"system-2\"")))
                .andExpect(content().string(containsString("href=\"/components/5\"")));
    }

    @Test
    void overviewShowsEmptyStateWhenThereAreNoSystems() throws Exception {
        when(componentService.findAllSystems()).thenReturn(List.of());

        mockMvc.perform(get("/components"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No components have been added yet.")));
    }

    @Test
    void detailShowsPurposeOperationMaterialsAndSystem() throws Exception {
        cassette.getMaterials().add(new Material("Steel", "Strong and durable."));
        when(componentService.findById(5)).thenReturn(Optional.of(cassette));
        when(componentService.findSystemComponents(any())).thenReturn(List.of(chain, cassette));

        mockMvc.perform(get("/components/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/detail"))
                .andExpect(content().string(containsString("Cassette")))
                .andExpect(content().string(containsString("Gives a range of gears.")))
                .andExpect(content().string(containsString("The derailleur moves the chain.")))
                .andExpect(content().string(containsString("Steel")))
                .andExpect(content().string(containsString("Transfers power to the rear wheel.")))
                // The other component in the same system is linked as related
                .andExpect(content().string(containsString("href=\"/components/4\"")));
    }

    @Test
    void detailShowsEmptyStateWhenNoMaterialsAreRecorded() throws Exception {
        when(componentService.findById(4)).thenReturn(Optional.of(chain));
        when(componentService.findSystemComponents(any())).thenReturn(List.of(chain));

        mockMvc.perform(get("/components/4"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No materials have been recorded for this component yet.")));
    }

    @Test
    void unknownComponentIdReturnsNotFound() throws Exception {
        when(componentService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/components/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void nonNumericComponentIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/components/abc"))
                .andExpect(status().isNotFound());
    }
}
