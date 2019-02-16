package ru.loaltyplant.movierater.repository.movie;

import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.model.Genre;

public interface MovieRepository {
    ProgressableFuture<Double> getAverageRatingForGenre(Genre genre);
}
