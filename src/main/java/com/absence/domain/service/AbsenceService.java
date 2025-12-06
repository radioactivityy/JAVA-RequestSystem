package com.absence.domain.service;

import com.absence.domain.dto.AbsenceRequestDTO;
import com.absence.domain.dto.EmployeeDTO;
import com.absence.domain.model.RequestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface defining business operations for the absence management system.
 *
 * Architecture: Domain Layer - The Service Layer pattern encapsulates business
 * logic and orchestrates operations across multiple repositories. This interface
 * is storage-agnostic; it neither knows nor cares whether data is stored in
 * XML, JSON, or any other format.
 *
 * Design Pattern: Service Layer - Provides a unified API for business operations,
 * coordinating between repositories and enforcing business rules.
 */
public interface AbsenceService {

    // Employee Operations

    /**
     * Retrieves an employee by their ID.
     *
     * @param employeeId the employee's unique identifier
     * @return Optional containing the employee DTO if found
     */
    Optional<EmployeeDTO> getEmployee(String employeeId);

    /**
     * Retrieves an employee's remaining leave balance.
     *
     * @param employeeId the employee's unique identifier
     * @return remaining leave days, or -1 if employee not found
     */
    int getRemainingLeaveBalance(String employeeId);

    // Absence Request Submission (Employee Context)

    /**
     * Submits a new absence request for an employee.
     *
     * @param employeeId the requesting employee's ID
     * @param startDate the first day of absence
     * @param endDate the last day of absence
     * @param reason the reason for absence
     * @return the created request DTO
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if employee not found or insufficient balance
     */
    AbsenceRequestDTO submitRequest(String employeeId, LocalDate startDate,
                                     LocalDate endDate, String reason);

    /**
     * Retrieves all absence requests for an employee.
     *
     * @param employeeId the employee's unique identifier
     * @return list of absence request DTOs
     */
    List<AbsenceRequestDTO> getEmployeeRequests(String employeeId);

    /**
     * Cancels a pending absence request.
     *
     * @param employeeId the employee's ID (for authorization)
     * @param requestId the request to cancel
     * @return true if cancellation was successful
     * @throws IllegalStateException if request is not pending or not owned by employee
     */
    boolean cancelRequest(String employeeId, String requestId);

    // Manager Operations (Request Processing)

    /**
     * Processes an absence request (approve or reject).
     *
     * @param managerId the processing manager's ID
     * @param requestId the request to process
     * @param approved true to approve, false to reject
     * @return the updated request DTO
     * @throws IllegalStateException if manager not found or request not pending
     */
    AbsenceRequestDTO processRequest(String managerId, String requestId, boolean approved);

    /**
     * Retrieves all pending absence requests.
     *
     * @return list of pending request DTOs
     */
    List<AbsenceRequestDTO> getPendingRequests();

    /**
     * Retrieves all pending requests for a specific department.
     *
     * @param department the department name
     * @return list of pending request DTOs for the department
     */
    List<AbsenceRequestDTO> getPendingRequestsByDepartment(String department);

    /**
     * Retrieves all requests with a specific status.
     *
     * @param status the status to filter by
     * @return list of request DTOs with the given status
     */
    List<AbsenceRequestDTO> getRequestsByStatus(RequestStatus status);

    /**
     * Validates manager credentials.
     *
     * @param managerId the manager's ID
     * @return true if manager exists and is active
     */
    boolean validateManager(String managerId);

    /**
     * Retrieves details of a specific absence request.
     *
     * @param requestId the request's unique identifier
     * @return Optional containing the request DTO if found
     */
    Optional<AbsenceRequestDTO> getRequestDetails(String requestId);
}
