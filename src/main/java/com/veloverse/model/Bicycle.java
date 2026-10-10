package com.veloverse.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A bicycle model, such as the Giant TCR Advanced Pro 2.
 * Maps to the bicycles table. Each bicycle has one brand, one type and one frame material.
 * Specification fields may be null when no reliable value is available.
 */
@Entity
@Table(name = "bicycles")
public class Bicycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bicycle_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id")
    private BicycleType type;

    // Optional: NULL when the exact frame material of the model could not be confirmed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frame_material_id")
    private Material frameMaterial;

    // Model name without the brand, e.g. "TCR Advanced Pro 2"
    private String name;

    // Model year the specifications belong to
    private Integer year;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "wheel_size")
    private String wheelSize;

    @Column(name = "brake_system")
    private String brakeSystem;

    private String drivetrain;

    // Total number of gears, e.g. 2 x 9 = 18
    private Integer gears;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    // What the bicycle is designed for
    @Column(name = "intended_use", columnDefinition = "TEXT")
    private String intendedUse;

    // Main features, advantages and disadvantages, one item per line
    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(columnDefinition = "TEXT")
    private String advantages;

    @Column(columnDefinition = "TEXT")
    private String disadvantages;

    // Page the specifications were checked against
    @Column(name = "source_url")
    private String sourceUrl;

    protected Bicycle() {
        // Required by JPA
    }

    public Bicycle(Brand brand, BicycleType type, Material frameMaterial, String name, Integer year) {
        this.brand = brand;
        this.type = type;
        this.frameMaterial = frameMaterial;
        this.name = name;
        this.year = year;
    }

    public Integer getId() {
        return id;
    }

    public Brand getBrand() {
        return brand;
    }

    public BicycleType getType() {
        return type;
    }

    public Material getFrameMaterial() {
        return frameMaterial;
    }

    public String getName() {
        return name;
    }

    public Integer getYear() {
        return year;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public String getWheelSize() {
        return wheelSize;
    }

    public String getBrakeSystem() {
        return brakeSystem;
    }

    public String getDrivetrain() {
        return drivetrain;
    }

    public Integer getGears() {
        return gears;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIntendedUse() {
        return intendedUse;
    }

    public String getFeatures() {
        return features;
    }

    public String getAdvantages() {
        return advantages;
    }

    public String getDisadvantages() {
        return disadvantages;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public List<String> getFeatureList() {
        return TextLines.split(features);
    }

    public List<String> getAdvantageList() {
        return TextLines.split(advantages);
    }

    public List<String> getDisadvantageList() {
        return TextLines.split(disadvantages);
    }
}
