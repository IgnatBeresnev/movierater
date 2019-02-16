package ru.loaltyplant.movierater.repository.movie;

import ru.loaltyplant.movierater.model.Movie;

import java.util.concurrent.ConcurrentMap;

class ChronicleMapMovieRepositoryTest extends AbstractMovieRepositoryTest {

    @Override
    public MovieRepository createRepository(ConcurrentMap<Long, Movie> testData) {
        return new ChronicleMapMovieRepository(testData);
    }
}