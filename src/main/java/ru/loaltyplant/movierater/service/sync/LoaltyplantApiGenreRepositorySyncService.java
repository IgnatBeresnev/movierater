package ru.loaltyplant.movierater.service.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.property.LoaltyplantApiProperties;
import ru.loaltyplant.movierater.repository.CrudRepository;

@Service
@Profile(ApplicationProperties.PROFILE_LOALTYPLANT_API)
public class LoaltyplantApiGenreRepositorySyncService implements RepositorySyncService<Long, Genre> {

    private final LoaltyplantApiProperties apiProperties;

    @Autowired
    public LoaltyplantApiGenreRepositorySyncService(LoaltyplantApiProperties apiProperties) {
        this.apiProperties = apiProperties;
    }

    @Override
    public void sync(CrudRepository<Long, Genre> repository) {
        // TODO [beresnev] implement
        System.out.println("Syncing genres...");
    }
}
