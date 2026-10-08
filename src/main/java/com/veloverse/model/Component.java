package com.veloverse.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * An individual bicycle part, such as the Chain or Cassette.
 * Maps to the components table.
 */
@Entity
@Table(name = "components")
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "component_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "system_id")
    private ComponentSystem system;

    private String name;

    // What the component is
    private String description;

    // What the component does
    private String purpose;

    // How the component works
    private String operation;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany
    @JoinTable(name = "component_materials",
            joinColumns = @JoinColumn(name = "component_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id"))
    @OrderBy("name")
    private List<Material> materials = new ArrayList<>();

    protected Component() {
        // Required by JPA
    }

    public Component(ComponentSystem system, String name, String description,
                     String purpose, String operation) {
        this.system = system;
        this.name = name;
        this.description = description;
        this.purpose = purpose;
        this.operation = operation;
    }

    public Integer getId() {
        return id;
    }

    public ComponentSystem getSystem() {
        return system;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getOperation() {
        return operation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Material> getMaterials() {
        return materials;
    }
}
