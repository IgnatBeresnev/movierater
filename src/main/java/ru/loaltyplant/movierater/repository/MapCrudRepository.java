package ru.loaltyplant.movierater.repository;

import ru.loaltyplant.movierater.model.HasId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MapCrudRepository<V extends HasId> implements CrudRepository<Long, V> {

    protected abstract Map<Long, V> getMapStorage();

    @Override
    public V getById(Long id) {
        return getMapStorage().get(id);
    }

    @Override
    public List<V> getAll() {
        return new ArrayList<>(getMapStorage().values());
    }

    @Override
    public Long save(V entity) {
        getMapStorage().put(entity.getId(), entity);
        return entity.getId();
    }

    @Override
    public List<Long> saveAll(List<V> entities) {
        Map<Long, V> asMap = entities.stream()
                .collect(Collectors.toMap(V::getId, V -> V));
        getMapStorage().putAll(asMap);

        return new ArrayList<>(asMap.keySet());
    }

    @Override
    public void update(V entity) {
        save(entity);
    }

    @Override
    public void updateAll(List<V> entities) {
        saveAll(entities);
    }

    @Override
    public boolean delete(Long id) {
        return getMapStorage().remove(id) != null;
    }
}
