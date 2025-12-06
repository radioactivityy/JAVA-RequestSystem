package com.absence.data.json;

import com.absence.domain.model.Manager;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON model for Manager serialization using Jackson.
 *
 * Architecture: Data Layer - Jackson-annotated model that provides JSON
 * serialization for Manager entities. Similar to XML models, this class
 * serves as an Anti-Corruption Layer between the domain and persistence.
 */
public class ManagerJsonModel {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("department")
    private String department;

    @JsonProperty("active")
    private boolean active;

    public ManagerJsonModel() {
    }

    public static ManagerJsonModel fromDomain(Manager manager) {
        ManagerJsonModel model = new ManagerJsonModel();
        model.id = manager.getId();
        model.name = manager.getName();
        model.email = manager.getEmail();
        model.department = manager.getDepartment();
        model.active = manager.isActive();
        return model;
    }

    public Manager toDomain() {
        Manager manager = new Manager();
        manager.setId(id);
        manager.setName(name);
        manager.setEmail(email);
        manager.setDepartment(department);
        manager.setActive(active);
        return manager;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
