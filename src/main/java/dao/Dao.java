package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object (DAO) interface providing basic CRUD operations.
 *
 * @param <T> the type of the entity
 * @param <K> the type of the primary key
 */
public interface Dao<T, K> {

    /**
     * Saves a new entity to the database.
     *
     * @param item the entity to save
     */
    void save(T item) throws SQLException;

    /**
     * Updates an existing entity in the database.
     *
     * @param item the entity to update
     * @param id the ID of the entity to update
     */
    void update(T item, K id) throws SQLException;

    /**
     * Retrieves an entity from the database by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return an Optional containing the found entity, or empty if not found
     */
    Optional<T> get(K id) throws SQLException;

    /**
     * Retrieves all entities from the database.
     *
     * @return a list of all entities
     */
    List<T> getAll() throws SQLException;

    /**
     * Deletes an entity from the database by its ID.
     *
     * @param id the ID of the entity to delete
     */
    void delete(K id) throws SQLException;
}

