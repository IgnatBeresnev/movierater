package ru.loaltyplant.movierater;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.configuration.ApplicationConfiguration;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.repository.movie.MovieRepository;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        MovieRepository movieRepository = context.getBean(MovieRepository.class);

        Movie byId;
        do {
            byId = movieRepository.getById(2L);
            Thread.sleep(5000L);
        } while (byId == null);
        System.out.println(new Date() + "; Fully synced");

        long start = System.nanoTime();
        ProgressableFuture<Double> name = movieRepository.getAverageRatingForGenre(new Genre(99L, "Name"));
        Double avrRating = name.get();
        long finish = System.nanoTime();

        System.out.println("DONNNE: " + avrRating + "; time taken: " +
                TimeUnit.NANOSECONDS.toSeconds(finish - start)
        );
    }
}
