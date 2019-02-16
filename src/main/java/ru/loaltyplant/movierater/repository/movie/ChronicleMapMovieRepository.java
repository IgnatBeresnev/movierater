package ru.loaltyplant.movierater.repository.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.loaltyplant.movierater.concurrent.DelegateProgressableFuture;
import ru.loaltyplant.movierater.concurrent.ProgressCounter;
import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.util.MutableInteger;
import ru.loaltyplant.movierater.util.math.RunningAverage;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Repository
@Profile(ApplicationProperties.PROFILE_REPOSITORY_CHRONICLEMAP)
public class ChronicleMapMovieRepository implements MovieRepository {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Map<Long, Movie> storage;

    @Autowired
    public ChronicleMapMovieRepository(Map<Long, Movie> storage) {
        this.storage = storage;
    }

    @Override
    public ProgressableFuture<Double> getAverageRatingForGenre(Genre genre) {
        // we don't care if a couple of new entries are gonna be added
        // to the map while we're iterating, progress is an estimated value
        int approximateMoviesAmount = storage.size();
        ProgressCounter progressCounter = new ProgressCounter(approximateMoviesAmount);

        Future<Double> future = executor.submit(calculateAverageRatingForGenre(genre, progressCounter));
        return new DelegateProgressableFuture<>(future, progressCounter);
    }

    private Callable<Double> calculateAverageRatingForGenre(Genre genre, ProgressCounter progressCounter) {
        return () -> {
            RunningAverage runningAverage = new RunningAverage();
            MutableInteger processedEntriesCounter = new MutableInteger(0);

            long genreId = genre.getId();
            storage.forEach((id, movie) -> {
                if (movie.getGenreIds().contains(genreId)) {
                    runningAverage.add(movie.getAverageRating());

                    int processedEntries = processedEntriesCounter.incrementAndGet();
                    progressCounter.updateProcessed(processedEntries);
                }
            });
            return runningAverage.getAverage();
        };
    }
}
