package ru.loaltyplant.movierater.repository.movie;

import net.openhft.chronicle.map.ChronicleMap;
import ru.loaltyplant.movierater.model.Movie;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

class ChronicleMapMovieRepositoryTest extends AbstractMovieRepositoryTest {

    @Override
    public MovieRepository createRepository(ConcurrentMap<Long, Movie> testData) {
        ChronicleMap<Long, Movie> chronicleMap = ChronicleMap.of(Long.class, Movie.class)
                .averageValue(new Movie(1L, Set.of(1L, 2L, 3L), 3.0))
                .entries(testData.size())
                .create();
        chronicleMap.putAll(testData);

        return new ChronicleMapMovieRepository(chronicleMap);
    }
}