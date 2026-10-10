package com.veloverse.controller;

// Static imports let the tests call these methods without their class name:
// get builds a fake GET request, status / view / content check the response,
// containsString (Hamcrest) checks the page text, and when (Mockito) sets what a mock returns.
// not(...) turns a check around, e.g. the page must NOT contain the placeholder image.
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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

import com.veloverse.model.Material;
import com.veloverse.service.MaterialService;

/**
 * Web-layer tests for the Materials list and detail pages, including their photos and credits,
 * with the service mocked.
 * @WebMvcTest starts only the web layer (this controller plus Thymeleaf), not the services or the database.
 * Each test follows arrange (tell the mock service what to return), act (send a fake request with
 * MockMvc) and assert (check the status, the template name and the rendered HTML).
 */
@WebMvcTest(MaterialController.class)
class MaterialControllerTest {

    // MockMvc sends fake HTTP requests to the controller without starting a real web server.
    // @Autowired asks Spring to put its MockMvc object into this field.
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean: Spring gives the controller a Mockito mock in place of the real service.
    // A mock returns only what a test sets with when(...), otherwise an empty list, an empty Optional or null.
    @MockitoBean
    private MaterialService materialService;

    /**
     * Builds a test material with this ID and name.
     * Material has no setters (the database normally sets the ID), so ReflectionTestUtils.setField
     * writes the private "id" field directly.
     */
    private static Material material(int id, String name) {
        Material material = new Material(name, name + " description.");
        ReflectionTestUtils.setField(material, "id", id);
        return material;
    }

    // The photo is chosen from the material name in templates/fragments/photos.html, so the names matter here
    @Test
    void listShowsAPhotoForEachOfTheFiveMaterials() throws Exception {
        when(materialService.findAll()).thenReturn(List.of(material(1, "Aluminium"), material(2, "Carbon Fibre"),
                material(3, "Steel"), material(4, "Titanium"), material(5, "Rubber")));

        mockMvc.perform(get("/materials"))
                .andExpect(status().isOk())
                .andExpect(view().name("materials"))
                .andExpect(content().string(containsString("/images/photos/material-aluminium-640.webp")))
                .andExpect(content().string(containsString("/images/photos/material-carbon-fibre-640.webp")))
                .andExpect(content().string(containsString("/images/photos/material-steel-640.webp")))
                .andExpect(content().string(containsString("/images/photos/material-titanium-640.webp")))
                .andExpect(content().string(containsString("/images/photos/material-rubber-640.webp")))
                .andExpect(content().string(not(containsString("placeholder-bike.svg"))));
    }

    // The Steel and Rubber detail pages show the large photo and credit its photographer with a link to the source
    @Test
    void replacementSteelAndRubberPhotosAreCredited() throws Exception {
        when(materialService.findById(3)).thenReturn(Optional.of(material(3, "Steel")));
        when(materialService.findById(5)).thenReturn(Optional.of(material(5, "Rubber")));

        mockMvc.perform(get("/materials/3"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/material-steel-1280.webp")))
                .andExpect(content().string(containsString("engin akyurt")))
                .andExpect(content().string(containsString(
                        "href=\"https://unsplash.com/photos/a-large-stack-of-pipes-stacked-on-top-of-each-other-F4nEetWGt0A\"")));

        mockMvc.perform(get("/materials/5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/material-rubber-1280.webp")))
                .andExpect(content().string(containsString("Tokuo Nobuhiro")))
                .andExpect(content().string(containsString(
                        "href=\"https://www.pexels.com/photo/close-up-of-a-stack-of-used-tires-16945365/\"")));
    }

    // Wikimedia Commons photos need the author, a link to the licence and a note about the changes made
    @Test
    void wikimediaPhotosShowAuthorLicenceAndChanges() throws Exception {
        when(materialService.findById(4)).thenReturn(Optional.of(material(4, "Titanium")));
        when(materialService.findById(1)).thenReturn(Optional.of(material(1, "Aluminium")));

        mockMvc.perform(get("/materials/4"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Alchemist-hp (pse-mendelejew.de)")))
                .andExpect(content().string(containsString("href=\"https://creativecommons.org/licenses/by-sa/3.0/\"")))
                .andExpect(content().string(containsString("This adapted version is licensed under CC BY-SA 3.0.")))
                // The original was smaller than 1280 px, so the large file is not enlarged
                .andExpect(content().string(containsString("1206w")));

        mockMvc.perform(get("/materials/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"https://commons.wikimedia.org/wiki/File:Aluminium-4.jpg\"")))
                .andExpect(content().string(containsString("href=\"https://creativecommons.org/licenses/by/3.0/\"")));
    }

    // "Bamboo" (ID 9) is a made-up test material with no photo, so the placeholder image is shown
    @Test
    void materialWithoutAPhotoKeepsThePlaceholder() throws Exception {
        when(materialService.findById(9)).thenReturn(Optional.of(material(9, "Bamboo")));

        mockMvc.perform(get("/materials/9"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("placeholder-bike.svg")));
    }
}
