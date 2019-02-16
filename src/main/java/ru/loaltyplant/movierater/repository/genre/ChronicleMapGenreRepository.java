package ru.loaltyplant.movierater.repository.genre;

import net.openhft.chronicle.map.ChronicleMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.repository.MapCrudRepository;

import java.util.Map;

@Repository
@Profile(ApplicationProperties.PROFILE_REPOSITORY_CHRONICLEMAP)
public class ChronicleMapGenreRepository extends MapCrudRepository<Genre> implements GenreRepository {

    private final ChronicleMap<Long, Genre> storage;

    @Autowired
    public ChronicleMapGenreRepository(ChronicleMap<Long, Genre> storage) {
        this.storage = storage;
    }

    @Override
    public Map<Long, Genre> getMapStorage() {
        return storage;
    }
}
