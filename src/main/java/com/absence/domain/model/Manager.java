package com.absence.domain.model;

import java.util.Objects;

/**
 * Domain entity representing a manager who can approve or reject absence requests.
 *
 * Architecture: Domain Layer - Pure POJO with no external dependencies.
 * Managers are stored separately from employees (in JSON format at the Data layer),
 * but this class knows nothing about storage mechanisms.
 */
public class Manager {

    private String id;
    private String name;
    private String email;
    private String department;
    private boolean active;

    public Manager() {
        this.active = true;
    }

    public Manager(String id, String name, String email, String department) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    /**
     * Checks if this manager can process requests for a given department.
     */
    public boolean canManageDepartment(String targetDepartment) {
        return active && (department == null || department.equalsIgnoreCase(targetDepartment));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return Objects.equals(id, manager.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", active=" + active +
                '}';
    }
}
