package com.absence.presentation.cli;

import com.absence.domain.dto.AbsenceRequestDTO;
import com.absence.domain.model.RequestStatus;
import com.absence.domain.service.AbsenceService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

/**
 * Command-line interface for manager operations.
 *
 * Architecture: Presentation Layer - Handles user interaction for the
 * manager context. This layer depends on the Domain layer (AbsenceService)
 * but has no direct dependency on the Data layer.
 *
 * Dependency Rule: The Presentation layer communicates with the Domain
 * layer through DTOs (AbsenceRequestDTO), never directly with entities
 * or repositories. This maintains clean separation of concerns.
 *
 * Note: This is a simplified CLI implementation. In a production system,
 * consider using a CLI framework like Picocli or Spring Shell.
 */
public class ManagerCommandLineInterface {

    private final AbsenceService absenceService;
    private final BufferedReader reader;
    private final PrintStream output;

    private String currentManagerId;
    private boolean running;

    public ManagerCommandLineInterface(AbsenceService absenceService) {
        this(absenceService, new BufferedReader(new InputStreamReader(System.in)), System.out);
    }

    public ManagerCommandLineInterface(AbsenceService absenceService,
                                        BufferedReader reader, PrintStream output) {
        this.absenceService = absenceService;
        this.reader = reader;
        this.output = output;
        this.running = true;
    }

    /**
     * Starts the CLI application loop.
     */
    public void start() {
        printWelcome();

        if (!authenticate()) {
            output.println("Authentication failed. Exiting.");
            return;
        }

        while (running) {
            printMenu();
            try {
                String choice = reader.readLine();
                processChoice(choice);
            } catch (IOException e) {
                output.println("Error reading input: " + e.getMessage());
            }
        }
    }

    private void printWelcome() {
        output.println("================================================");
        output.println("    Employee Absence Management System");
        output.println("         Manager Command Interface");
        output.println("================================================");
        output.println();
    }

    private boolean authenticate() {
        output.println("Please enter your Manager ID to login:");
        try {
            String managerId = reader.readLine();
            if (managerId == null || managerId.trim().isEmpty()) {
                return false;
            }

            if (absenceService.validateManager(managerId.trim())) {
                this.currentManagerId = managerId.trim();
                output.println("Welcome, Manager " + currentManagerId + "!");
                output.println();
                return true;
            } else {
                output.println("Invalid or inactive manager ID.");
                return false;
            }
        } catch (IOException e) {
            output.println("Error during authentication: " + e.getMessage());
            return false;
        }
    }

    private void printMenu() {
        output.println();
        output.println("--- Manager Menu ---");
        output.println("1. View Pending Requests");
        output.println("2. View All Requests by Status");
        output.println("3. Approve Request");
        output.println("4. Reject Request");
        output.println("5. View Request Details");
        output.println("0. Exit");
        output.println();
        output.print("Enter your choice: ");
    }

    private void processChoice(String choice) {
        if (choice == null) {
            running = false;
            return;
        }

        switch (choice.trim()) {
            case "1" -> viewPendingRequests();
            case "2" -> viewRequestsByStatus();
            case "3" -> approveRequest();
            case "4" -> rejectRequest();
            case "5" -> viewRequestDetails();
            case "0" -> exit();
            default -> output.println("Invalid choice. Please try again.");
        }
    }

    private void viewPendingRequests() {
        output.println();
        output.println("=== Pending Absence Requests ===");

        List<AbsenceRequestDTO> requests = absenceService.getPendingRequests();

        if (requests.isEmpty()) {
            output.println("No pending requests found.");
            return;
        }

        displayRequestList(requests);
    }

    private void viewRequestsByStatus() {
        output.println();
        output.println("Select status to view:");
        output.println("1. Pending");
        output.println("2. Approved");
        output.println("3. Rejected");
        output.print("Choice: ");

        try {
            String statusChoice = reader.readLine();
            RequestStatus status = switch (statusChoice.trim()) {
                case "1" -> RequestStatus.PENDING;
                case "2" -> RequestStatus.APPROVED;
                case "3" -> RequestStatus.REJECTED;
                default -> null;
            };

            if (status == null) {
                output.println("Invalid status choice.");
                return;
            }

            output.println();
            output.println("=== " + status.getDisplayName() + " Requests ===");

            List<AbsenceRequestDTO> requests = absenceService.getRequestsByStatus(status);
            if (requests.isEmpty()) {
                output.println("No requests found with status: " + status.getDisplayName());
                return;
            }

            displayRequestList(requests);

        } catch (IOException e) {
            output.println("Error reading input: " + e.getMessage());
        }
    }

    private void displayRequestList(List<AbsenceRequestDTO> requests) {
        output.println(String.format("%-36s %-20s %-12s %-12s %-10s",
                "Request ID", "Employee", "Start", "End", "Days"));
        output.println("-".repeat(95));

        for (AbsenceRequestDTO request : requests) {
            output.println(String.format("%-36s %-20s %-12s %-12s %-10d",
                    request.id(),
                    truncate(request.employeeName(), 20),
                    request.startDate(),
                    request.endDate(),
                    request.durationDays()));
        }
    }

    private void approveRequest() {
        processRequestDecision(true);
    }

    private void rejectRequest() {
        processRequestDecision(false);
    }

    private void processRequestDecision(boolean approve) {
        String action = approve ? "approve" : "reject";
        output.println();
        output.print("Enter Request ID to " + action + ": ");

        try {
            String requestId = reader.readLine();
            if (requestId == null || requestId.trim().isEmpty()) {
                output.println("Invalid request ID.");
                return;
            }

            AbsenceRequestDTO result = absenceService.processRequest(
                    currentManagerId, requestId.trim(), approve);

            output.println();
            output.println("Request " + (approve ? "APPROVED" : "REJECTED") + " successfully!");
            output.println();
            output.println(result.toDisplayString());

        } catch (IllegalStateException e) {
            output.println("Error: " + e.getMessage());
        } catch (IOException e) {
            output.println("Error reading input: " + e.getMessage());
        }
    }

    private void viewRequestDetails() {
        output.println();
        output.print("Enter Request ID: ");

        try {
            String requestId = reader.readLine();
            if (requestId == null || requestId.trim().isEmpty()) {
                output.println("Invalid request ID.");
                return;
            }

            absenceService.getRequestDetails(requestId.trim())
                    .ifPresentOrElse(
                            request -> {
                                output.println();
                                output.println("=== Request Details ===");
                                output.println(request.toDisplayString());
                            },
                            () -> output.println("Request not found: " + requestId)
                    );

        } catch (IOException e) {
            output.println("Error reading input: " + e.getMessage());
        }
    }

    private void exit() {
        output.println();
        output.println("Thank you for using the Absence Management System.");
        output.println("Goodbye!");
        running = false;
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
