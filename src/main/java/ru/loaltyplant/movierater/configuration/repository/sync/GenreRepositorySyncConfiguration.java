package ru.loaltyplant.movierater.configuration.repository.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.repository.genre.GenreRepository;
import ru.loaltyplant.movierater.service.sync.RepositorySyncService;

@Configuration
@Profile(ApplicationProperties.PROFILE_SYNC_REPOSITORIES)
public class GenreRepositorySyncConfiguration {

    private final GenreRepository genreRepository;
    private final RepositorySyncService<Long, Genre> genreSyncService;

    @Autowired
    public GenreRepositorySyncConfiguration(GenreRepository genreRepository, RepositorySyncService<Long, Genre> genreSyncService) {
        this.genreRepository = genreRepository;
        this.genreSyncService = genreSyncService;
    }

    @Scheduled(initialDelay = 1000L, fixedRate = 300_000L) // TODO [beresnev] get time from config
    public void schedule() {
        genreSyncService.sync(genreRepository);
    }
}
