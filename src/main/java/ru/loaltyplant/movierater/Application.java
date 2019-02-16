package ru.loaltyplant.movierater;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.configuration.ApplicationConfiguration;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.repository.movie.MovieRepository;

import java.util.concurrent.ExecutionException;

public class Application {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        MovieRepository movieRepository = context.getBean(MovieRepository.class);
        ProgressableFuture<Double> test = movieRepository.getAverageRatingForGenre(new Genre(1L, "test"));
        while (test.isDone()) {
            System.out.println(test.getProgress());
        }
        System.out.println("Waiting");
        System.out.println(test.get());
    }
}
