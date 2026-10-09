package com.veloverse.model;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A material used for bicycles and components, such as Aluminium or Carbon Fibre.
 * Maps to the materials table.
 */
@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Integer id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Stored as one point per line
    @Column(columnDefinition = "TEXT")
    private String advantages;

    // Stored as one point per line
    @Column(columnDefinition = "TEXT")
    private String disadvantages;

    protected Material() {
        // Required by JPA
    }

    public Material(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAdvantages() {
        return advantages;
    }

    public String getDisadvantages() {
        return disadvantages;
    }

    public List<String> getAdvantageList() {
        return toLines(advantages);
    }

    public List<String> getDisadvantageList() {
        return toLines(disadvantages);
    }

    // Same approach as BicycleType: split the stored text into one item per line
    private static List<String> toLines(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split("\\R"))
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .toList();
    }
}
