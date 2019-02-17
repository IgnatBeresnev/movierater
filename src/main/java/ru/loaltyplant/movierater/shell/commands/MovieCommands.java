package ru.loaltyplant.movierater.shell.commands;

import me.tongfei.progressbar.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.service.MovieService;

import java.util.concurrent.ExecutionException;

@ShellComponent
public class MovieCommands {

    private final MovieService movieService;

    @Autowired
    public MovieCommands(MovieService movieService) {
        this.movieService = movieService;
    }

    @ShellMethod("Calculates average rating for all movies of specified genre")
    public String averageVote(@ShellOption("--genre-id") int genreId) throws ExecutionException, InterruptedException {
        Genre requestedGenre = new Genre(genreId, "name");
        ProgressableFuture<Double> futureAverageRating = movieService.getAverageRatingForGenre(
                requestedGenre
        );
        try (ProgressBar progressBar = new ProgressBar("Calculating average", 100)) {
            while (!futureAverageRating.isDone()) {
                progressBar.stepTo((int) futureAverageRating.getProgress());
                progressBar.setExtraMessage("Calculating average rating...");
                Thread.sleep(10);
            }
            progressBar.stepTo(100); // since we're isDone() here, we're definitely at 100%
        }

        double averageRating = -1d;
        try {
            averageRating = futureAverageRating.get();
        } catch (Exception ignored) { // isDone() is true at this point
        }

        return String.format("Average rating for movies of genre \"%s\" is %f", requestedGenre.getName(), averageRating);
    }
}
