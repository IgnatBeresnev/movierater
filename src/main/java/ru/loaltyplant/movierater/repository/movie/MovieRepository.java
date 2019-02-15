package ru.loaltyplant.movierater.repository.movie;

import ru.loaltyplant.movierater.model.Genre;

public interface MovieRepository {
    double getAverageRatingForGenre(Genre genre);
}
