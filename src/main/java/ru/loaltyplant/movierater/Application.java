package ru.loaltyplant.movierater;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.loaltyplant.movierater.configuration.ApplicationConfiguration;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.repository.movie.MovieRepository;

public class Application {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        MovieRepository movieRepository = context.getBean(MovieRepository.class);
        double avgRating = movieRepository.getAverageRatingForGenre(new Genre(1L, "test"));
        System.out.println(avgRating);
    }
}
