package ru.loaltyplant.movierater.service.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.loaltyplant.movierater.dto.GenreDto;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.property.LoaltyplantApiProperties;
import ru.loaltyplant.movierater.repository.CrudRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
@Service
@Profile(ApplicationProperties.PROFILE_LOALTYPLANT_API)
public class LoaltyplantApiGenreRepositorySyncService implements RepositorySyncService<Long, Genre> {

    private final LoaltyplantApiProperties apiProperties;

    private final HttpClient httpClient;
    private final RateLimiter rateLimiter;

    private final ObjectMapper objectMapper;

    @Autowired
    public LoaltyplantApiGenreRepositorySyncService(LoaltyplantApiProperties apiProperties, HttpClient httpClient,
                                                    RateLimiter rateLimiter, ObjectMapper objectMapper) {
        this.apiProperties = apiProperties;
        this.rateLimiter = rateLimiter;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sync(CrudRepository<Long, Genre> repository) {
        try {
            log.debug("Syncing repository...");
            List<Genre> genres = makeListGenresRequest().stream()
                    .map(Genre::fromDto)
                    .collect(Collectors.toList());

            repository.updateAll(genres);
            log.debug("Sync complete");
        } catch (Exception e) {
            log.error("Unable to sync genres", e);
        }
    }

    private List<GenreDto> makeListGenresRequest() throws Exception {
        rateLimiter.acquire();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(apiProperties.getGenresUrlWithApiKey()))
                .build();

        log.debug("Sending request");
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.body() == null || response.body().length == 0) {
            log.warn("Genres request returned empty body");
            return Collections.emptyList();
        }

        GenresWrapper genresWrapper = objectMapper.readValue(response.body(), GenresWrapper.class);
        return genresWrapper.genres;
    }

    private static class GenresWrapper {
        public List<GenreDto> genres;
    }
}
