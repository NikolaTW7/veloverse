package com.veloverse.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.veloverse.model.BicycleType;
import com.veloverse.service.BicycleTypeService;

/**
 * Web-layer tests for the Bicycle Types pages (FR-01), with the service mocked.
 */
@WebMvcTest(BicycleTypeController.class)
class BicycleTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BicycleTypeService bicycleTypeService;

    private static BicycleType type(int id, String name) {
        BicycleType type = new BicycleType(name, name + " description.", name + " use",
                "First advantage\nSecond advantage", "Only disadvantage");
        ReflectionTestUtils.setField(type, "id", id);
        return type;
    }

    @Test
    void listPageShowsAllTypesWithLinks() throws Exception {
        when(bicycleTypeService.findAll()).thenReturn(List.of(type(1, "Road Bike"), type(2, "Mountain Bike")));

        mockMvc.perform(get("/bicycle-types"))
                .andExpect(status().isOk())
                .andExpect(view().name("bicycle-types/list"))
                .andExpect(content().string(containsString("Road Bike")))
                .andExpect(content().string(containsString("Mountain Bike")))
                .andExpect(content().string(containsString("href=\"/bicycle-types/2\"")));
    }

    @Test
    void listPageShowsEmptyStateWhenThereAreNoTypes() throws Exception {
        when(bicycleTypeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/bicycle-types"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No bicycle types have been added yet.")));
    }

    @Test
    void detailPageShowsTypeAdvantagesAndRelatedTypes() throws Exception {
        BicycleType road = type(1, "Road Bike");
        when(bicycleTypeService.findById(1)).thenReturn(Optional.of(road));
        when(bicycleTypeService.findOthers(any(), anyInt())).thenReturn(List.of(type(3, "Gravel Bike")));

        mockMvc.perform(get("/bicycle-types/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bicycle-types/detail"))
                .andExpect(content().string(containsString("Road Bike")))
                .andExpect(content().string(containsString("Second advantage")))
                .andExpect(content().string(containsString("Only disadvantage")))
                .andExpect(content().string(containsString("href=\"/bicycle-types/3\"")));
    }

    @Test
    void unknownTypeIdReturnsNotFound() throws Exception {
        when(bicycleTypeService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/bicycle-types/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void nonNumericTypeIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/bicycle-types/abc"))
                .andExpect(status().isNotFound());
    }
}
