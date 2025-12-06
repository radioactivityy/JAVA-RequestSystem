package com.absence.domain.service;

import com.absence.domain.dto.AbsenceRequestDTO;
import com.absence.domain.dto.EmployeeDTO;
import com.absence.domain.model.AbsenceRequest;
import com.absence.domain.model.Employee;
import com.absence.domain.model.Manager;
import com.absence.domain.model.RequestStatus;
import com.absence.domain.repository.EmployeeRepository;
import com.absence.domain.repository.ManagerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the AbsenceService interface.
 *
 * Architecture: Domain Layer - Contains all business logic for absence
 * management. This class depends only on repository interfaces defined
 * in the domain layer, not on concrete implementations. This satisfies
 * the Dependency Inversion Principle (DIP).
 *
 * Design Pattern: Service Layer - Orchestrates business operations
 * while remaining completely agnostic of storage mechanisms (XML/JSON).
 *
 * The service coordinates between:
 * - EmployeeRepository (implemented with XML storage in Data layer)
 * - ManagerRepository (implemented with JSON storage in Data layer)
 *
 * But it knows nothing about these implementation details.
 */
public class AbsenceServiceImpl implements AbsenceService {

    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;

    /**
     * Constructor injection of repository dependencies.
     * Repositories are injected as interfaces, not concrete implementations.
     */
    public AbsenceServiceImpl(EmployeeRepository employeeRepository,
                               ManagerRepository managerRepository) {
        this.employeeRepository = employeeRepository;
        this.managerRepository = managerRepository;
    }

    @Override
    public Optional<EmployeeDTO> getEmployee(String employeeId) {
        return employeeRepository.findById(employeeId)
                .map(EmployeeDTO::fromEntity);
    }

    @Override
    public int getRemainingLeaveBalance(String employeeId) {
        return employeeRepository.findById(employeeId)
                .map(Employee::calculateRemainingLeave)
                .orElse(-1);
    }

    @Override
    public AbsenceRequestDTO submitRequest(String employeeId, LocalDate startDate,
                                            LocalDate endDate, String reason) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalStateException(
                        "Employee not found: " + employeeId));

        AbsenceRequest request = new AbsenceRequest(employeeId, startDate, endDate, reason);

        if (!request.isValid()) {
            throw new IllegalArgumentException(
                    "Invalid request dates. End date must not be before start date, " +
                    "and start date must not be in the past.");
        }

        if (!employee.hasSufficientBalance(request)) {
            throw new IllegalStateException(
                    "Insufficient leave balance. Requested: " + request.calculateDuration() +
                    " days, Available: " + employee.calculateRemainingLeave() + " days");
        }

        employee.addAbsenceRequest(request);
        employeeRepository.save(employee);

        return AbsenceRequestDTO.fromEntity(request, employee.getName());
    }

    @Override
    public List<AbsenceRequestDTO> getEmployeeRequests(String employeeId) {
        return employeeRepository.findById(employeeId)
                .map(employee -> employee.getAbsenceRequests().stream()
                        .map(r -> AbsenceRequestDTO.fromEntity(r, employee.getName()))
                        .toList())
                .orElse(List.of());
    }

    @Override
    public boolean cancelRequest(String employeeId, String requestId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalStateException(
                        "Employee not found: " + employeeId));

        AbsenceRequest request = employee.getAbsenceRequests().stream()
                .filter(r -> r.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Request not found or does not belong to employee: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot cancel request with status: " + request.getStatus());
        }

        request.setStatus(RequestStatus.REJECTED);
        return employeeRepository.updateAbsenceRequest(request);
    }

    @Override
    public AbsenceRequestDTO processRequest(String managerId, String requestId, boolean approved) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new IllegalStateException(
                        "Manager not found: " + managerId));

        if (!manager.isActive()) {
            throw new IllegalStateException("Manager account is inactive");
        }

        AbsenceRequest request = employeeRepository.findAbsenceRequestById(requestId)
                .orElseThrow(() -> new IllegalStateException(
                        "Absence request not found: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Request has already been processed. Current status: " + request.getStatus());
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalStateException(
                        "Employee not found for request: " + request.getEmployeeId()));

        if (!manager.canManageDepartment(employee.getDepartment())) {
            throw new IllegalStateException(
                    "Manager is not authorized to process requests for department: " +
                    employee.getDepartment());
        }

        request.setStatus(approved ? RequestStatus.APPROVED : RequestStatus.REJECTED);
        request.setProcessedByManagerId(managerId);
        employeeRepository.updateAbsenceRequest(request);

        return AbsenceRequestDTO.fromEntity(request, employee.getName());
    }

    @Override
    public List<AbsenceRequestDTO> getPendingRequests() {
        return getRequestsByStatus(RequestStatus.PENDING);
    }

    @Override
    public List<AbsenceRequestDTO> getPendingRequestsByDepartment(String department) {
        return employeeRepository.findByDepartment(department).stream()
                .flatMap(emp -> emp.getAbsenceRequests().stream()
                        .filter(r -> r.getStatus() == RequestStatus.PENDING)
                        .map(r -> AbsenceRequestDTO.fromEntity(r, emp.getName())))
                .toList();
    }

    @Override
    public List<AbsenceRequestDTO> getRequestsByStatus(RequestStatus status) {
        List<AbsenceRequest> requests = employeeRepository.findAbsenceRequestsByStatus(status);

        return requests.stream()
                .map(request -> {
                    String employeeName = employeeRepository.findById(request.getEmployeeId())
                            .map(Employee::getName)
                            .orElse("Unknown");
                    return AbsenceRequestDTO.fromEntity(request, employeeName);
                })
                .toList();
    }

    @Override
    public boolean validateManager(String managerId) {
        return managerRepository.validateCredentials(managerId);
    }

    @Override
    public Optional<AbsenceRequestDTO> getRequestDetails(String requestId) {
        return employeeRepository.findAbsenceRequestById(requestId)
                .map(request -> {
                    String employeeName = employeeRepository.findById(request.getEmployeeId())
                            .map(Employee::getName)
                            .orElse("Unknown");
                    return AbsenceRequestDTO.fromEntity(request, employeeName);
                });
    }
}
