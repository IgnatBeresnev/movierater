package ru.loaltyplant.movierater.service.sync;

import ru.loaltyplant.movierater.repository.CrudRepository;

public interface RepositorySyncService<ID, T> {

    void sync(CrudRepository<ID, T> repository);
}
