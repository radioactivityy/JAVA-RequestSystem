package com.absence.domain.repository;

import com.absence.domain.model.Manager;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Manager persistence operations.
 *
 * Architecture: Domain Layer - Defines the contract for manager data
 * access. The concrete implementation in the Data layer uses JSON storage,
 * but this interface knows nothing about that detail.
 *
 * Design Pattern: Repository Pattern - Enables the Service layer to work
 * with managers without coupling to specific storage technologies.
 *
 * Dependency Rule: Pure domain interface with no external dependencies.
 */
public interface ManagerRepository {

    /**
     * Finds a manager by their unique identifier.
     *
     * @param id the manager's unique identifier
     * @return an Optional containing the manager if found
     */
    Optional<Manager> findById(String id);

    /**
     * Finds a manager by their email address.
     *
     * @param email the manager's email
     * @return an Optional containing the manager if found
     */
    Optional<Manager> findByEmail(String email);

    /**
     * Retrieves all managers.
     *
     * @return list of all managers
     */
    List<Manager> findAll();

    /**
     * Retrieves all active managers.
     *
     * @return list of active managers
     */
    List<Manager> findAllActive();

    /**
     * Retrieves all managers responsible for a specific department.
     *
     * @param department the department name
     * @return list of managers for the department
     */
    List<Manager> findByDepartment(String department);

    /**
     * Saves a manager (creates new or updates existing).
     *
     * @param manager the manager to save
     * @return the saved manager
     */
    Manager save(Manager manager);

    /**
     * Deletes a manager by their identifier.
     *
     * @param id the manager's unique identifier
     * @return true if deletion was successful
     */
    boolean deleteById(String id);

    /**
     * Validates manager credentials for authentication.
     *
     * @param managerId the manager's ID
     * @return true if the manager exists and is active
     */
    boolean validateCredentials(String managerId);
}
