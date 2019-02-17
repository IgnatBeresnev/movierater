package ru.loaltyplant.movierater.shell.commands;

import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.service.GenreService;
import ru.loaltyplant.movierater.service.MovieService;

import java.util.concurrent.Future;

@Slf4j
@ShellComponent
public class MovieCommands {

    private final MovieService movieService;
    private final GenreService genreService;

    private volatile Future<?> currentlyExecutingTask;

    @Autowired
    public MovieCommands(MovieService movieService, GenreService genreService) {
        this.movieService = movieService;
        this.genreService = genreService;
    }

    public void cancelCurrentlyExecutingTask() {
        Future<?> currentlyExecutingTask = this.currentlyExecutingTask; // avoid races
        if (currentlyExecutingTask != null) {
            boolean cancel = currentlyExecutingTask.cancel(true);
            if (!cancel) {
                log.error("Could not cancel currently running task");
            }
        }
    }

    @ShellMethod("Calculates average rating for all movies of specified genre")
    public String averageVote(@ShellOption("--genre-id") long genreId) throws Exception {
        Genre requestedGenre = genreService.getById(genreId);
        if (requestedGenre == null) {
            return "Genre with given id not found";
        }

        ProgressableFuture<Double> futureAverageRating = movieService.getAverageRatingForGenre(
                requestedGenre
        );
        currentlyExecutingTask = futureAverageRating;
        try (ProgressBar progressBar = new ProgressBar("Calculating average", 100)) {
            do {
                progressBar.stepTo((int) futureAverageRating.getProgress());
                progressBar.setExtraMessage("Calculating average rating...");
            } while (!futureAverageRating.isDone() && !futureAverageRating.isCancelled());
        }
        currentlyExecutingTask = null;

        if (futureAverageRating.isCancelled()) {
            return "Calculation has been canceled";
        }

        double averageRating;
        try {
            averageRating = futureAverageRating.get();
        } catch (Exception e) {
            throw new IllegalStateException("isDone() should be true by now", e);
        }

        return String.format("Average rating for movies of genre \"%s\" is %f", requestedGenre.getName(), averageRating);
    }
}
