package ru.loaltyplant.movierater.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.model.Movie;

import java.util.Set;

@Getter
@Component
@Profile(ApplicationProperties.PROFILE_REPOSITORY_CHRONICLEMAP)
public class ChronicleMapRepositoryProperties {

    /**
     * ChronicleMap requires you to either set average value (as an object)
     * or an average size of an average object value in bytes. Since counting
     * bytes can be tricky (you can count how many bytes fields take within
     * the object, but you can't be sure how many bytes it'll take when serialized),
     * it's much easier to just give ChronicleMap an average value object
     * and let it do it's thing.
     * <p>
     * If new fields are added to related objects, MAKE SURE you update average
     * value objects (the ones below). Otherwise it can lead to runtime errors
     * when performing actions on chronicle map.
     */
    private final Movie averageMovieValue = new Movie(1L, Set.of(1L, 2L, 3L, 4L), 5d);
    private final Genre averageGenreValue = new Genre(1L, "Long ass genre name ololo");

    //region MOVIES STORAGE SETTINGS
    @Value("${repository.chroniclemap.movies.path}")
    private String moviesFilePath;

    @Value("${repository.chroniclemap.movies.entries}")
    private int moviesNumberOfEntries;
    //endregion

    //region GENRES STORAGE SETTINGS
    @Value("${repository.chroniclemap.genres.path}")
    private String genresFilePath;

    @Value("${repository.chroniclemap.genres.entries}")
    private int genresNumberOfEntries;
    //endregion
}
