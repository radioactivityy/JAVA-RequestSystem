package com.absence.data.registry;

import com.absence.data.json.JsonManagerRepository;
import com.absence.data.xml.XmlEmployeeRepository;
import com.absence.domain.repository.EmployeeRepository;
import com.absence.domain.repository.ManagerRepository;
import com.absence.domain.service.AbsenceService;
import com.absence.domain.service.AbsenceServiceImpl;

/**
 * Singleton registry for repository and service instances.
 *
 * Architecture: Data Layer - Acts as a simple Dependency Injection container,
 * providing centralized access to repository implementations and services.
 * In a production system, this would typically be replaced by a DI framework
 * like Spring or Guice.
 *
 * Design Pattern: Singleton - Ensures a single instance of the registry exists,
 * providing global access to repositories while controlling instantiation.
 *
 * Design Pattern: Registry - Centralizes object creation and lookup, decoupling
 * clients from concrete implementations.
 *
 * Note: This class knows about concrete implementations (XmlEmployeeRepository,
 * JsonManagerRepository) but clients only interact with interfaces. This is
 * acceptable as the registry serves as a composition root.
 */
public final class RepositoryRegistry {

    private static volatile RepositoryRegistry instance;

    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;
    private final AbsenceService absenceService;

    private static final String DEFAULT_EMPLOYEES_FILE = "data/employees.xml";
    private static final String DEFAULT_MANAGERS_FILE = "data/managers.json";

    private RepositoryRegistry(String employeesFile, String managersFile) {
        this.employeeRepository = new XmlEmployeeRepository(employeesFile);
        this.managerRepository = new JsonManagerRepository(managersFile);
        this.absenceService = new AbsenceServiceImpl(employeeRepository, managerRepository);
    }

    /**
     * Returns the singleton instance with default file paths.
     * Thread-safe using double-checked locking.
     */
    public static RepositoryRegistry getInstance() {
        return getInstance(DEFAULT_EMPLOYEES_FILE, DEFAULT_MANAGERS_FILE);
    }

    /**
     * Returns the singleton instance with custom file paths.
     * Thread-safe using double-checked locking.
     */
    public static RepositoryRegistry getInstance(String employeesFile, String managersFile) {
        if (instance == null) {
            synchronized (RepositoryRegistry.class) {
                if (instance == null) {
                    instance = new RepositoryRegistry(employeesFile, managersFile);
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton instance. Primarily for testing purposes.
     */
    public static synchronized void reset() {
        instance = null;
    }

    /**
     * Returns the EmployeeRepository implementation.
     * Clients receive the interface type, not the concrete XML implementation.
     */
    public EmployeeRepository getEmployeeRepository() {
        return employeeRepository;
    }

    /**
     * Returns the ManagerRepository implementation.
     * Clients receive the interface type, not the concrete JSON implementation.
     */
    public ManagerRepository getManagerRepository() {
        return managerRepository;
    }

    /**
     * Returns the AbsenceService implementation.
     */
    public AbsenceService getAbsenceService() {
        return absenceService;
    }
}
