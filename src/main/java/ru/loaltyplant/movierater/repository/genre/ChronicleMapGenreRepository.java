package ru.loaltyplant.movierater.repository.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.property.ApplicationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Repository
@Profile(ApplicationProperties.PROFILE_REPOSITORY_CHRONICLEMAP)
public class ChronicleMapGenreRepository implements GenreRepository {
    private final ConcurrentMap<Long, Genre> storage;

    @Autowired
    public ChronicleMapGenreRepository(ConcurrentMap<Long, Genre> storage) {
        this.storage = storage;
    }

    @Override
    public List<Genre> getGenres() {
        return new ArrayList<>(storage.values());
    }
}
