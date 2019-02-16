package ru.loaltyplant.movierater.configuration.repository;

import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.map.ChronicleMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.property.ChronicleMapRepositoryProperties;

import java.io.File;
import java.io.IOException;

@Slf4j
@Configuration
@Profile(ApplicationProperties.PROFILE_REPOSITORY_CHRONICLEMAP)
public class ChronicleMapRepositoryConfiguration {

    @Bean("movies")
    public ChronicleMap<Long, Movie> movieStorage(ChronicleMapRepositoryProperties repositoryProperties) throws IOException {
        ChronicleMap<Long, Movie> moviesMap = ChronicleMap.of(Long.class, Movie.class)
                .entries(repositoryProperties.getMoviesNumberOfEntries())
                .averageValueSize(repositoryProperties.getMoviesAvgEntryBytes())
                .createPersistedTo(getValidMapFile(repositoryProperties.getMoviesFilePath()));

        addShutdownHookToCloseMap(moviesMap);
        return moviesMap;
    }

    @Bean("genres")
    public ChronicleMap<Long, Genre> genreStorage(ChronicleMapRepositoryProperties repositoryProperties) throws IOException {
        ChronicleMap<Long, Genre> genresMap = ChronicleMap.of(Long.class, Genre.class)
                .entries(repositoryProperties.getGenresNumberOfEntries())
                .averageValueSize(repositoryProperties.getGenresAvgEntryBytes())
                .createPersistedTo(getValidMapFile(repositoryProperties.getGenresFilePath()));

        addShutdownHookToCloseMap(genresMap);
        return genresMap;
    }

    private File getValidMapFile(String path) throws IOException {
        File file = new File(path);
        if (file.exists() && (file.isDirectory() || !file.canRead() || !file.canWrite())) {
            throw new IllegalArgumentException(
                    "Bad map path: " + path + "; It's either a directory, or I can't read or write"
            );
        }

        if (!file.exists()) {
            boolean created = file.createNewFile();
            if (!created) {
                throw new IllegalArgumentException("Unable to create new file under path: " + path);
            }
        }
        return file;
    }

    /**
     * @see <a href="https://github.com/OpenHFT/Chronicle-Map/blob/master/docs/CM_Tutorial.adoc#close-chroniclemap"></a>
     */
    private void addShutdownHookToCloseMap(ChronicleMap<?, ?> map) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Closing map with value class: {}", map.valueClass());
            map.close();
        }, "ChronicleMap-shutdownHook"));
    }
}
