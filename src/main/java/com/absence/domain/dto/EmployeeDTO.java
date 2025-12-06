package com.absence.domain.dto;

import com.absence.domain.model.Employee;

import java.util.List;

/**
 * Data Transfer Object for Employee entities.
 *
 * Architecture: Domain Layer - Provides a simplified view of employee
 * data for the Presentation layer. Includes computed fields like
 * remaining leave balance.
 *
 * Design Pattern: DTO Pattern - Decouples presentation concerns from
 * domain model internals. Changes to the Employee entity don't
 * necessarily affect API consumers.
 */
public record EmployeeDTO(
        String id,
        String name,
        String email,
        String department,
        int annualLeaveBalance,
        int remainingLeaveBalance,
        int pendingRequestsCount,
        List<AbsenceRequestDTO> recentRequests
) {

    /**
     * Factory method to create a DTO from a domain entity.
     */
    public static EmployeeDTO fromEntity(Employee employee) {
        List<AbsenceRequestDTO> requestDTOs = employee.getAbsenceRequests().stream()
                .map(r -> AbsenceRequestDTO.fromEntity(r, employee.getName()))
                .toList();

        long pendingCount = employee.getAbsenceRequests().stream()
                .filter(r -> r.getStatus() == com.absence.domain.model.RequestStatus.PENDING)
                .count();

        return new EmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getAnnualLeaveBalance(),
                employee.calculateRemainingLeave(),
                (int) pendingCount,
                requestDTOs
        );
    }

    /**
     * Creates a simplified DTO without request history.
     */
    public static EmployeeDTO summary(Employee employee) {
        long pendingCount = employee.getAbsenceRequests().stream()
                .filter(r -> r.getStatus() == com.absence.domain.model.RequestStatus.PENDING)
                .count();

        return new EmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getAnnualLeaveBalance(),
                employee.calculateRemainingLeave(),
                (int) pendingCount,
                List.of()
        );
    }

    /**
     * Returns a formatted string representation for display.
     */
    public String toDisplayString() {
        return String.format(
                "Employee: %s (ID: %s)%n" +
                "Email: %s%n" +
                "Department: %s%n" +
                "Leave Balance: %d/%d days remaining%n" +
                "Pending Requests: %d",
                name, id, email, department,
                remainingLeaveBalance, annualLeaveBalance, pendingRequestsCount
        );
    }
}
