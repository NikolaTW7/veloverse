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
 * A category of bicycle, such as Road Bike or Mountain Bike.
 * Maps to the bicycle_types table.
 */
@Entity
@Table(name = "bicycle_types")
public class BicycleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer id;

    private String name;

    private String description;

    @Column(name = "best_use")
    private String bestUse;

    // Stored as one point per line
    private String advantages;

    // Stored as one point per line
    private String disadvantages;

    protected BicycleType() {
        // Required by JPA
    }

    public BicycleType(String name, String description, String bestUse,
                       String advantages, String disadvantages) {
        this.name = name;
        this.description = description;
        this.bestUse = bestUse;
        this.advantages = advantages;
        this.disadvantages = disadvantages;
    }

    public List<String> getAdvantageList() {
        return toLines(advantages);
    }

    public List<String> getDisadvantageList() {
        return toLines(disadvantages);
    }

    private static List<String> toLines(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split("\\R"))
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .toList();
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

    public String getBestUse() {
        return bestUse;
    }

    public String getAdvantages() {
        return advantages;
    }

    public String getDisadvantages() {
        return disadvantages;
    }
}
