package ru.loaltyplant.movierater.repository.movie;

import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Long, Movie> {
    ProgressableFuture<Double> getAverageRatingForGenre(Genre genre);
}
