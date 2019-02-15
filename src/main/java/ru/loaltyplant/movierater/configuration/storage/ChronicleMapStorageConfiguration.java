package ru.loaltyplant.movierater.configuration.storage;

import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.map.ChronicleMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.property.ChronicleMapStorageProperties;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Configuration
@Profile(ApplicationProperties.PROFILE_REPOSITORY_CHRONICLEMAP)
public class ChronicleMapStorageConfiguration {

    @Bean("movies")
    public ConcurrentMap<Long, Movie> movieStorage(ChronicleMapStorageProperties storageProperties) throws IOException {
        ChronicleMap<Long, Movie> moviesMap = ChronicleMap.of(Long.class, Movie.class)
                .entries(storageProperties.getMoviesAvgEntryBytes())
                .averageValueSize(storageProperties.getMoviesAvgEntryBytes())
                .createPersistedTo(getValidMapFile(storageProperties.getMoviesFilePath()));

        addShutdownHookToCloseMap(moviesMap);
        return moviesMap;
    }

    @Bean("genres")
    public ConcurrentMap<Long, Genre> genreStorage(ChronicleMapStorageProperties storageProperties) throws IOException {
        ChronicleMap<Long, Genre> genresMap = ChronicleMap.of(Long.class, Genre.class)
                .entries(storageProperties.getGenresNumberOfEntries())
                .averageValueSize(storageProperties.getGenresAvgEntryBytes())
                .createPersistedTo(getValidMapFile(storageProperties.getGenresFilePath()));

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
        }));
    }
}
