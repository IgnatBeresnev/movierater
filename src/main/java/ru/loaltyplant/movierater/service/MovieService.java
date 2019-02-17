package ru.loaltyplant.movierater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.repository.movie.MovieRepository;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public ProgressableFuture<Double> getAverageRatingForGenre(Genre genre) {
        return movieRepository.getAverageRatingForGenre(genre);
    }
}
