package com.veloverse.model;

// BigDecimal stores exact decimal numbers (such as a weight of 8.40), without rounding errors
import java.math.BigDecimal;
import java.util.List;

// JPA (Jakarta Persistence) annotations that describe how this class maps to a database table
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
 * Maps to the bicycles table. Each bicycle has one brand, one type and at most one frame material
 * (the frame material is empty when it could not be confirmed).
 * Specification fields may be null when no reliable value is available.
 */
// @Entity makes this a JPA entity: a Java class whose objects are rows of a database table.
// Hibernate (the JPA library) reads the rows and fills in the fields for us.
// @Table gives the table name. Fields without @Column use a column with the same name (e.g. name, year).
@Entity
@Table(name = "bicycles")
public class Bicycle {

    // @Id marks the primary key. IDENTITY means PostgreSQL creates the number when a row is inserted.
    // @Column(name = ...) is needed because the column name differs from the field name.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bicycle_id")
    private Integer id;

    // @ManyToOne: many bicycles can belong to one brand. @JoinColumn names the foreign key column.
    // FetchType.LAZY means the brand is not read from the database until it is needed,
    // so the repository uses @EntityGraph to load it in the same query when a page needs it.
    // optional = false: every bicycle must have a brand.
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

    // Weight in kilograms, e.g. 8.40
    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "wheel_size")
    private String wheelSize;

    @Column(name = "brake_system")
    private String brakeSystem;

    private String drivetrain;

    // Total number of gears, e.g. 2 x 9 = 18
    private Integer gears;

    // columnDefinition = "TEXT" tells JPA the column is a TEXT column (long text with no fixed limit)
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

    // protected: JPA needs a constructor with no parameters to create the object before filling in
    // the fields, but other code should use the public constructor below.
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

    // Getters: the fields are private, so templates read them through these methods.
    // For example ${bicycle.name} in Thymeleaf calls getName().
    // There are no setters, because the website only reads data.
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

    // The list getters split the stored "one item per line" text into a list (see TextLines),
    // so the template can show each item as a bullet point, e.g. ${bicycle.featureList}.
    // They are not database columns: they are worked out from the text fields each time.
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
