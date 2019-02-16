package ru.loaltyplant.movierater.repository;

import java.util.List;

public interface CrudRepository<ID, T> {
    List<T> getAll();

    void save(T entity);

    void update(T entity);

    boolean delete(ID id);
}
