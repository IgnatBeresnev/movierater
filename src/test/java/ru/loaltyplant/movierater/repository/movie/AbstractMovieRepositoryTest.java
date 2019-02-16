package ru.loaltyplant.movierater.repository.movie;

import org.junit.jupiter.api.Test;
import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.model.Movie;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractMovieRepositoryTest {
    @Test
    public void shouldCountAverage() throws ExecutionException, InterruptedException, TimeoutException {
        double expectedValue = 3.85d;
        ConcurrentMap<Long, Movie> testData = new ConcurrentHashMap<>() {{
            put(1L, new Movie(1L, Set.of(1L), 5.2f));
            put(2L, new Movie(2L, Set.of(1L), 4.3f));
            put(3L, new Movie(3L, Set.of(1L), 3.4f));
            put(4L, new Movie(4L, Set.of(1L), 2.5f));
        }};

        MovieRepository repository = createRepository(testData);
        ProgressableFuture<Double> averageRatingFuture =
                repository.getAverageRatingForGenre(new Genre(1L, "GenreName"));

        double averageResult = averageRatingFuture.get(50, TimeUnit.MILLISECONDS);

        assertEquals(expectedValue, averageResult, 0.01d);
    }

    @Test
    public void shouldFilterBasedOnGenre() throws InterruptedException, ExecutionException, TimeoutException {
        double expectedValue = 666L;
        ConcurrentMap<Long, Movie> testData = new ConcurrentHashMap<>() {{
            put(1L, new Movie(1L, Set.of(1L), 1L));
            put(2L, new Movie(2L, Set.of(2L), 2L));
            put(3L, new Movie(3L, Set.of(3L), expectedValue));
            put(4L, new Movie(4L, Set.of(4L), 4L));
        }};

        MovieRepository repository = createRepository(testData);

        Genre genre = new Genre(3L, "Expected Genre");
        ProgressableFuture<Double> averageRatingFuture =
                repository.getAverageRatingForGenre(genre);

        double averageResult = averageRatingFuture.get(50, TimeUnit.MILLISECONDS);
        assertEquals(expectedValue, averageResult);
    }

    @Test
    public void shouldReturnZeroRatingForEmptyStorage() throws InterruptedException, ExecutionException, TimeoutException {
        double expectedValue = 0d;
        ConcurrentHashMap<Long, Movie> emptyMap = new ConcurrentHashMap<>();

        MovieRepository emptyRepository = createRepository(emptyMap);
        ProgressableFuture<Double> averageRatingFuture =
                emptyRepository.getAverageRatingForGenre(new Genre(1L, "Name"));

        double averageResult = averageRatingFuture.get(50, TimeUnit.MILLISECONDS);
        assertEquals(expectedValue, averageResult);
    }

    @Test
    public void shouldReturnHundredPercentProgressAfterDoneIteratingEvenForUnmatchingGenre()
            throws InterruptedException, ExecutionException, TimeoutException {
        ConcurrentMap<Long, Movie> testData = new ConcurrentHashMap<>() {{
            put(0L, new Movie(0L, Set.of(0L), 0f));
        }};

        MovieRepository repository = createRepository(testData);
        ProgressableFuture<Double> averageRatingFuture =
                repository.getAverageRatingForGenre(new Genre(1L, "GenreName"));

        averageRatingFuture.get(50, TimeUnit.MILLISECONDS);

        double expectedProgress = 100d;
        double actualProgress = averageRatingFuture.getProgress();
        assertEquals(expectedProgress, actualProgress);
    }

    public abstract MovieRepository createRepository(ConcurrentMap<Long, Movie> testData);
}
