package com.veloverse.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A bicycle manufacturer, such as Cervélo or Giant.
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

    // Null when the brand does not state where it is from
    private String country;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Official website address
    private String website;

    // The kinds of bicycles and products the brand is known for, one per line
    @Column(columnDefinition = "TEXT")
    private String specialities;

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

    public String getWebsite() {
        return website;
    }

    public String getSpecialities() {
        return specialities;
    }

    public List<String> getSpecialityList() {
        return TextLines.split(specialities);
    }
}
