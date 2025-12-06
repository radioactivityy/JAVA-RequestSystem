package com.absence.domain.dto;

import com.absence.domain.model.AbsenceRequest;
import com.absence.domain.model.RequestStatus;

import java.time.LocalDate;

/**
 * Data Transfer Object for AbsenceRequest entities.
 *
 * Architecture: Domain Layer - DTOs facilitate data transfer between
 * the Presentation and Domain layers without exposing internal entity
 * structures. This decouples the presentation from domain model changes.
 *
 * Design Pattern: DTO Pattern - Provides a flat, serialization-friendly
 * representation of domain data for use in web responses and CLI output.
 *
 * Note: Using Java 17 record for immutability and conciseness.
 */
public record AbsenceRequestDTO(
        String id,
        String employeeId,
        String employeeName,
        LocalDate startDate,
        LocalDate endDate,
        long durationDays,
        String reason,
        String status,
        String processedByManagerId,
        LocalDate submissionDate
) {

    /**
     * Factory method to create a DTO from a domain entity.
     */
    public static AbsenceRequestDTO fromEntity(AbsenceRequest request, String employeeName) {
        return new AbsenceRequestDTO(
                request.getId(),
                request.getEmployeeId(),
                employeeName,
                request.getStartDate(),
                request.getEndDate(),
                request.calculateDuration(),
                request.getReason(),
                request.getStatus().getDisplayName(),
                request.getProcessedByManagerId(),
                request.getSubmissionDate()
        );
    }

    /**
     * Converts this DTO to a domain entity for submission.
     */
    public AbsenceRequest toEntity() {
        AbsenceRequest request = new AbsenceRequest();
        if (id != null) {
            request.setId(id);
        }
        request.setEmployeeId(employeeId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setReason(reason);
        if (status != null) {
            request.setStatus(RequestStatus.valueOf(status.toUpperCase().replace(" ", "_")));
        }
        request.setProcessedByManagerId(processedByManagerId);
        if (submissionDate != null) {
            request.setSubmissionDate(submissionDate);
        }
        return request;
    }

    /**
     * Returns a formatted string representation for CLI display.
     */
    public String toDisplayString() {
        return String.format(
                "Request ID: %s%n" +
                "Employee: %s (ID: %s)%n" +
                "Period: %s to %s (%d days)%n" +
                "Reason: %s%n" +
                "Status: %s%n" +
                "Submitted: %s",
                id, employeeName, employeeId,
                startDate, endDate, durationDays,
                reason, status, submissionDate
        );
    }
}
