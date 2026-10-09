package com.veloverse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A bicycle manufacturer, such as Trek or Giant.
 * Maps to the brands table.
 */
@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Integer id;

    private String name;

    private String country;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected Brand() {
        // Required by JPA
    }

    public Brand(String name, String country, String description) {
        this.name = name;
        this.country = country;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }
}
