package com.veloverse.model;

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

    private String description;

    // Stored as one point per line
    private String advantages;

    // Stored as one point per line
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
}
