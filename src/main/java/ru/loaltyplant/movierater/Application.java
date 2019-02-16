package ru.loaltyplant.movierater;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.loaltyplant.movierater.configuration.ApplicationConfiguration;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.repository.movie.MovieRepository;

import java.util.concurrent.ExecutionException;

public class Application {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        MovieRepository movieRepository = context.getBean(MovieRepository.class);

        Movie byId;
        do {
            byId = movieRepository.getById(2L);
        } while (byId == null);
        System.out.println(byId);
    }
}
