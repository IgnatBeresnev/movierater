package ru.loaltyplant.movierater.repository;

import ru.loaltyplant.movierater.model.HasId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MapCrudRepository<V extends HasId> implements CrudRepository<Long, V> {

    protected abstract Map<Long, V> getMapStorage();

    @Override
    public List<V> getAll() {
        return new ArrayList<>(getMapStorage().values());
    }

    @Override
    public void save(V entity) {
        getMapStorage().put(entity.getId(), entity);
    }

    @Override
    public void update(V entity) {
        save(entity);
    }

    @Override
    public boolean delete(Long id) {
        return getMapStorage().remove(id) != null;
    }
}
