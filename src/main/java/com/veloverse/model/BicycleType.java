package com.veloverse.model;

import java.util.Arrays;
import java.util.List;

// JPA annotations that describe how this class maps to a database table
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
// @Entity: a JPA entity, so each object of this class is one row of the table named in @Table.
@Entity
@Table(name = "bicycle_types")
public class BicycleType {

    // Primary key, numbered automatically by PostgreSQL (IDENTITY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer id;

    private String name;

    private String description;

    // What this type of bicycle is best used for
    @Column(name = "best_use")
    private String bestUse;

    // Stored as one point per line
    private String advantages;

    // Stored as one point per line
    private String disadvantages;

    // protected no-argument constructor: needed by JPA to create objects from rows
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

    // The stored text split into one list item per line, so the template can show bullet points
    // (used as ${type.advantageList} and ${type.disadvantageList} in bicycle-types/detail.html)
    public List<String> getAdvantageList() {
        return toLines(advantages);
    }

    public List<String> getDisadvantageList() {
        return toLines(disadvantages);
    }

    // Splits on any line break (\R), trims spaces from each line and drops empty lines.
    // It does the same as TextLines.split.
    private static List<String> toLines(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split("\\R"))
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    // Getters used by the templates, e.g. ${type.name} calls getName()
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
