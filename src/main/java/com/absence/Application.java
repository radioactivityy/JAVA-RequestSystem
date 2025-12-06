package com.absence;

import com.absence.data.registry.RepositoryRegistry;
import com.absence.domain.service.AbsenceService;
import com.absence.presentation.cli.ManagerCommandLineInterface;

/**
 * Main application entry point.
 *
 * Architecture: This class serves as the Composition Root, where all
 * dependencies are wired together. It demonstrates how the three-tier
 * architecture comes together:
 *
 * 1. Data Layer: RepositoryRegistry initializes XML and JSON repositories
 * 2. Domain Layer: AbsenceService receives repositories via constructor injection
 * 3. Presentation Layer: CLI receives the AbsenceService
 *
 * The flow of dependencies:
 * Presentation -> Domain <- Data
 *
 * Both Presentation and Data depend on Domain, but never on each other.
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("Initializing Employee Absence Request System...");
        System.out.println();

        try {
            RepositoryRegistry registry = RepositoryRegistry.getInstance();
            AbsenceService absenceService = registry.getAbsenceService();

            if (args.length > 0 && "--demo".equals(args[0])) {
                runDemo(absenceService);
            } else {
                runManagerCli(absenceService);
            }

        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void runManagerCli(AbsenceService absenceService) {
        ManagerCommandLineInterface cli = new ManagerCommandLineInterface(absenceService);
        cli.start();
    }

    private static void runDemo(AbsenceService absenceService) {
        System.out.println("=== Running Demo Mode ===");
        System.out.println();

        System.out.println("Fetching pending requests...");
        var pendingRequests = absenceService.getPendingRequests();
        System.out.println("Found " + pendingRequests.size() + " pending request(s):");
        System.out.println();

        for (var request : pendingRequests) {
            System.out.println(request.toDisplayString());
            System.out.println("-".repeat(50));
        }

        System.out.println();
        System.out.println("Fetching employee EMP001...");
        absenceService.getEmployee("EMP001").ifPresent(emp -> {
            System.out.println(emp.toDisplayString());
        });

        System.out.println();
        System.out.println("=== Demo Complete ===");
    }
}
