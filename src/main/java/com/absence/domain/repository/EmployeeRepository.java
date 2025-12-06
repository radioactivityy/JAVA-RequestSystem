package com.absence.domain.repository;

import com.absence.domain.model.AbsenceRequest;
import com.absence.domain.model.Employee;
import com.absence.domain.model.RequestStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee persistence operations.
 *
 * Architecture: Domain Layer - This interface defines the contract for
 * data access without specifying implementation details. The Data layer
 * provides concrete implementations (e.g., XML-based storage).
 *
 * Design Pattern: Repository Pattern - Abstracts the data persistence
 * mechanism, allowing the Service layer to remain agnostic of whether
 * data is stored in XML, JSON, databases, or any other format.
 *
 * Dependency Rule: This interface is in the Domain layer and has no
 * dependencies on external frameworks or the Data layer.
 */
public interface EmployeeRepository {

    /**
     * Finds an employee by their unique identifier.
     *
     * @param id the employee's unique identifier
     * @return an Optional containing the employee if found
     */
    Optional<Employee> findById(String id);

    /**
     * Finds an employee by their email address.
     *
     * @param email the employee's email
     * @return an Optional containing the employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Retrieves all employees.
     *
     * @return list of all employees
     */
    List<Employee> findAll();

    /**
     * Retrieves all employees in a specific department.
     *
     * @param department the department name
     * @return list of employees in the department
     */
    List<Employee> findByDepartment(String department);

    /**
     * Saves an employee (creates new or updates existing).
     *
     * @param employee the employee to save
     * @return the saved employee
     */
    Employee save(Employee employee);

    /**
     * Deletes an employee by their identifier.
     *
     * @param id the employee's unique identifier
     * @return true if deletion was successful
     */
    boolean deleteById(String id);

    /**
     * Finds a specific absence request by its ID across all employees.
     *
     * @param requestId the absence request's unique identifier
     * @return an Optional containing the request if found
     */
    Optional<AbsenceRequest> findAbsenceRequestById(String requestId);

    /**
     * Retrieves all absence requests with a specific status.
     *
     * @param status the request status to filter by
     * @return list of absence requests with the given status
     */
    List<AbsenceRequest> findAbsenceRequestsByStatus(RequestStatus status);

    /**
     * Updates an existing absence request.
     *
     * @param request the absence request to update
     * @return true if update was successful
     */
    boolean updateAbsenceRequest(AbsenceRequest request);
}
