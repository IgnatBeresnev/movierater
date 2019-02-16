package ru.loaltyplant.movierater.repository;

import java.util.List;

public interface CrudRepository<ID, T> {
    T getById(ID id);

    List<T> getAll();

    ID save(T entity);

    List<ID> saveAll(List<T> entities);

    void update(T entity);

    void updateAll(List<T> entities);

    boolean delete(ID id);
}
