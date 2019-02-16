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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
@Service
@Profile(ApplicationProperties.PROFILE_LOALTYPLANT_API)
public class LoaltyplantApiMovieRepositorySyncService implements RepositorySyncService<Long, Movie> {

    private final LoaltyplantApiProperties apiProperties;

    private final HttpClient httpClient;
    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    private final ForkJoinPool forkJoinPool;

    @Autowired
    public LoaltyplantApiMovieRepositorySyncService(LoaltyplantApiProperties apiProperties, HttpClient httpClient,
                                                    RateLimiter rateLimiter, ObjectMapper objectMapper) {
        this.apiProperties = apiProperties;
        this.httpClient = httpClient;
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;

        this.forkJoinPool = new ForkJoinPool(apiProperties.getFjpWorkers());
    }

    @Override
    public void sync(CrudRepository<Long, Movie> repository) {
        try {
            long syncStartTime = System.nanoTime();
            log.debug("Syncing repository...");

            updateMoviesFromAllPages(repository);

            long millisTaken = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - syncStartTime);
            log.debug("Sync complete, millis taken={}", millisTaken);
        } catch (Exception e) {
            log.error("Unable to sync genres", e);
        }
    }

    private void updateMoviesFromAllPages(CrudRepository<Long, Movie> repository) throws Exception {
        MoviesPageInfo pagesInfo = getPagesInfo();
        if (pagesInfo == null || pagesInfo.totalPages == null) {
            log.warn("Page info returned blank response, not able to sync movies");
            return;
        }

        log.debug("Creating task to update {} pages", pagesInfo.totalPages);
        ForkJoinTask<Void> updateAllPagesTask = forkJoinPool.submit(
                updateAllPagesTask(repository, pagesInfo)
        );
        updateAllPagesTask.join();
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

    private ForkJoinTask<Void> updateAllPagesTask(CrudRepository<Long, Movie> repository, MoviesPageInfo pageInfo) {
        return new RecursiveAction() {
            @Override
            protected void compute() {
                try {
                    List<ForkJoinTask<Void>> updateIndividualPagesSubTasks = IntStream.range(1, pageInfo.totalPages.intValue())
                            .mapToObj(pageNum -> updateMoviesForPageSubTask(pageNum, repository))
                            .collect(Collectors.toList());

                    ForkJoinTask.invokeAll(updateIndividualPagesSubTasks);
                } catch (Throwable e) {
                    log.error("Unable to finish update all pages task", e);
                }
            }
        };
    }

    /**
     * Flushes DTOs to repository straight away because we might get OOM
     * or poor GC performance if we store all results from all pages at once
     */
    private ForkJoinTask<Void> updateMoviesForPageSubTask(int pageNumber, CrudRepository<Long, Movie> repository) {
        return new RecursiveAction() {
            @Override
            protected void compute() {
                try {
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

                    ForkJoinTask<Void> updateRepositorySubTask = updateRepositorySubTask(repository, pageMovies);
                    updateRepositorySubTask.fork();
                } catch (Throwable e) {
                    log.error("Unable to update page " + pageNumber, e);
                }
            }
        };
    }

    private ForkJoinTask<Void> updateRepositorySubTask(CrudRepository<Long, Movie> repository, List<MovieDto> movieDtos) {
        return new RecursiveAction() {
            @Override
            protected void compute() {
                try {
                    List<Movie> movies = movieDtos.stream()
                            .map(Movie::fromDto)
                            .collect(Collectors.toList());

                    repository.updateAll(movies);
                    log.debug("Updated {} movies", movies.size());
                } catch (Throwable e) {
                    log.debug("Error on updating repository", e);
                }
            }
        };
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
