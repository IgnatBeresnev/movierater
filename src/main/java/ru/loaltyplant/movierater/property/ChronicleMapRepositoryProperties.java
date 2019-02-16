package ru.loaltyplant.movierater.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Getter
@Component
@Profile(ApplicationProperties.PROFILE_REPOSITORY_CHRONICLEMAP)
public class ChronicleMapRepositoryProperties {

    //region MOVIES STORAGE SETTINGS
    @Value("${repository.chroniclemap.movies.path}")
    private String moviesFilePath;

    @Value("${repository.chroniclemap.movies.entries}")
    private int moviesNumberOfEntries;

    @Value("${repository.chroniclemap.movies.avgEntryBytes}")
    private int moviesAvgEntryBytes;
    //endregion

    //region GENRES STORAGE SETTINGS
    @Value("${repository.chroniclemap.genres.path}")
    private String genresFilePath;

    @Value("${repository.chroniclemap.genres.entries}")
    private int genresNumberOfEntries;

    @Value("${repository.chroniclemap.genres.avgEntryBytes}")
    private int genresAvgEntryBytes;
    //endregion
}
