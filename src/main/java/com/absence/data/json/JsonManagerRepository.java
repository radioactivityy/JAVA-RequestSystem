package com.absence.data.json;

import com.absence.domain.model.Manager;
import com.absence.domain.repository.ManagerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * JSON-based implementation of the ManagerRepository interface.
 *
 * Architecture: Data Layer - Concrete implementation using Jackson for
 * JSON persistence. This class depends on the Domain layer's repository
 * interface, satisfying the Dependency Inversion Principle.
 *
 * Design Pattern: Repository Pattern - Abstracts JSON-specific data access,
 * allowing the Service layer to work with managers without knowledge of
 * the underlying storage format.
 *
 * Storage Strategy: Managers are stored in JSON format, separate from
 * employees (XML). This demonstrates how the Repository pattern enables
 * heterogeneous storage strategies while maintaining a uniform interface.
 *
 * Thread Safety: Uses ReadWriteLock for concurrent access support.
 */
public class JsonManagerRepository implements ManagerRepository {

    private final String filePath;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private List<Manager> cachedManagers;

    public JsonManagerRepository(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadManagers();
    }

    private void loadManagers() {
        lock.writeLock().lock();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                cachedManagers = new ArrayList<>();
                return;
            }

            ManagerJsonWrapper wrapper = objectMapper.readValue(file, ManagerJsonWrapper.class);
            cachedManagers = wrapper.getManagers().stream()
                    .map(ManagerJsonModel::toDomain)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load managers from JSON", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveManagers() {
        lock.writeLock().lock();
        try {
            List<ManagerJsonModel> jsonModels = cachedManagers.stream()
                    .map(ManagerJsonModel::fromDomain)
                    .toList();

            ManagerJsonWrapper wrapper = new ManagerJsonWrapper(new ArrayList<>(jsonModels));

            File file = new File(filePath);
            file.getParentFile().mkdirs();

            objectMapper.writeValue(file, wrapper);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save managers to JSON", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Manager> findById(String id) {
        lock.readLock().lock();
        try {
            return cachedManagers.stream()
                    .filter(m -> m.getId().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Manager> findByEmail(String email) {
        lock.readLock().lock();
        try {
            return cachedManagers.stream()
                    .filter(m -> m.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Manager> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(cachedManagers);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Manager> findAllActive() {
        lock.readLock().lock();
        try {
            return cachedManagers.stream()
                    .filter(Manager::isActive)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Manager> findByDepartment(String department) {
        lock.readLock().lock();
        try {
            return cachedManagers.stream()
                    .filter(m -> m.getDepartment() == null ||
                                 m.getDepartment().equalsIgnoreCase(department))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Manager save(Manager manager) {
        lock.writeLock().lock();
        try {
            Optional<Manager> existing = cachedManagers.stream()
                    .filter(m -> m.getId().equals(manager.getId()))
                    .findFirst();

            if (existing.isPresent()) {
                int index = cachedManagers.indexOf(existing.get());
                cachedManagers.set(index, manager);
            } else {
                cachedManagers.add(manager);
            }

            saveManagers();
            return manager;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean deleteById(String id) {
        lock.writeLock().lock();
        try {
            boolean removed = cachedManagers.removeIf(m -> m.getId().equals(id));
            if (removed) {
                saveManagers();
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean validateCredentials(String managerId) {
        lock.readLock().lock();
        try {
            return cachedManagers.stream()
                    .anyMatch(m -> m.getId().equals(managerId) && m.isActive());
        } finally {
            lock.readLock().unlock();
        }
    }
}
