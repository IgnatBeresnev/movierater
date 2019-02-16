package ru.loaltyplant.movierater.service.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.loaltyplant.movierater.dto.MovieDto;
import ru.loaltyplant.movierater.model.Movie;
import ru.loaltyplant.movierater.property.ApplicationProperties;
import ru.loaltyplant.movierater.property.LoaltyplantApiProperties;
import ru.loaltyplant.movierater.repository.CrudRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
@Service
@Profile(ApplicationProperties.PROFILE_LOALTYPLANT_API)
public class LoaltyplantApiMovieRepositorySyncService implements RepositorySyncService<Long, Movie> {

    private final LoaltyplantApiProperties apiProperties;

    private final HttpClient httpClient;
    private final RateLimiter rateLimiter;

    private final ObjectMapper objectMapper;

    @Autowired
    public LoaltyplantApiMovieRepositorySyncService(LoaltyplantApiProperties apiProperties, HttpClient httpClient,
                                                    RateLimiter rateLimiter, ObjectMapper objectMapper) {
        this.apiProperties = apiProperties;
        this.httpClient = httpClient;
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;

    }

    @Override
    public void sync(CrudRepository<Long, Movie> repository) {
        try {
            log.debug("Syncing repository...");
            updateMoviesFromAllPages(repository);
            log.debug("Sync complete");
        } catch (Exception e) {
            log.error("Unable to sync genres", e);
        }
    }

    private void updateMoviesFromAllPages(CrudRepository<Long, Movie> repository) throws Exception {
        MoviesPageInfo pagesInfo = getPagesInfo();
        if (pagesInfo == null) {
            log.warn("Page info returned blank response, not able to sync movies");
            return;
        }

        for (int i = 1; i < pagesInfo.totalPages; i++) {
            updateMoviesForPage(i, repository);
            log.debug("Updated movies for page {}", i);
        }
    }

    @Nullable
    private MoviesPageInfo getPagesInfo() throws Exception {
        rateLimiter.acquire();

        HttpRequest request = createSimpleGetRequest(apiProperties.getMoviesUrlWithApiKeyAndPage(1));

        log.debug("Sending movies page info request");
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (isBlank(response)) {
            return null;
        }

        return objectMapper.readValue(response.body(), MoviesPageInfo.class);
    }

    /**
     * Flushes DTOs to repository straight away because we might get OOM
     * or poor GC performance if we store all results from all pages at once
     */
    private void updateMoviesForPage(int pageNumber, CrudRepository<Long, Movie> repository) throws Exception {
        rateLimiter.acquire();

        HttpRequest request = createSimpleGetRequest(apiProperties.getMoviesUrlWithApiKeyAndPage(pageNumber));

        log.debug("Sending movies page {} request", pageNumber);
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (isBlank(response)) {
            log.warn("Movies request for page {} returned blank body", pageNumber);
            return;
        }

        MoviesWrapper moviesWrapper = objectMapper.readValue(response.body(), MoviesWrapper.class);

        List<MovieDto> pageMovies = moviesWrapper.results;
        if (pageMovies.isEmpty()) {
            log.debug("Empty movies result for page {}", pageNumber);
            return;
        }
        updateMovies(repository, pageMovies);
    }

    private void updateMovies(CrudRepository<Long, Movie> repository, List<MovieDto> movieDtos) {
        List<Movie> movies = movieDtos.stream()
                .map(Movie::fromDto)
                .collect(Collectors.toList());

        repository.updateAll(movies);
        log.debug("Updated {} movies", movies.size());
    }

    private HttpRequest createSimpleGetRequest(String url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .GET()
                .uri(new URI(url))
                .build();
    }

    private boolean isBlank(HttpResponse<byte[]> response) {
        return response.body() == null || response.body().length == 0;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MoviesPageInfo {

        @JsonProperty("total_pages")
        private Long totalPages;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MoviesWrapper {
        private List<MovieDto> results;
    }
}
