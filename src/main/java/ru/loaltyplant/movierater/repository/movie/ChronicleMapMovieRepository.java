package ru.loaltyplant.movierater.repository.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.property.ApplicationProperties;

import java.util.Map;

@Repository
@Profile(ApplicationProperties.PROFILE_REPOSITORY_CHRONICLEMAP)
public class ChronicleMapMovieRepository implements MovieRepository {

    private final Map<Long, Movie> storage;

    @Autowired
    public ChronicleMapMovieRepository(Map<Long, Movie> storage) {
        this.storage = storage;
    }

    @Override
    public double getAverageRatingForGenre(Genre genre) {
        return 0;
    }
}
