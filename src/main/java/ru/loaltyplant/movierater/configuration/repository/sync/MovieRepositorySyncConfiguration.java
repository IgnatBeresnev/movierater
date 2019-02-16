package ru.loaltyplant.movierater.configuration.repository.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.repository.movie.MovieRepository;
import ru.loaltyplant.movierater.service.sync.RepositorySyncService;

@Configuration
@Profile(ApplicationProperties.PROFILE_SYNC_REPOSITORIES)
public class MovieRepositorySyncConfiguration {

    private final MovieRepository movieRepository;
    private final RepositorySyncService<Long, Movie> movieSyncService;

    @Autowired
    public MovieRepositorySyncConfiguration(MovieRepository movieRepository, RepositorySyncService<Long, Movie> movieSyncService) {
        this.movieRepository = movieRepository;
        this.movieSyncService = movieSyncService;
    }

    @Scheduled(initialDelay = 1000L, fixedRate = 60_000L) // TODO [beresnev] get time from config
    public void schedule() {
        movieSyncService.sync(movieRepository);
    }
}
