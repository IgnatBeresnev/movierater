package ru.loaltyplant.movierater.shell.commands;

import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import ru.loaltyplant.movierater.concurrent.DelegateProgressableFuture;
import ru.loaltyplant.movierater.concurrent.ProgressCounter;
import ru.loaltyplant.movierater.concurrent.ProgressableFuture;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.service.GenreService;
import ru.loaltyplant.movierater.service.MovieService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MovieCommandsTest {

    /**
     * Tests cancel command currently executing task. The idea is:
     * 1. Run getAverageRatingForGenre() in a separate thread, make sure it's
     * running in the main loop where it's printing progress to console.
     * 2. Run cancelCurrentlyExecutingTask() from another thread.
     * Assert isCancel() and result returned by command execution.
     */
    @Test
    public void shouldCancelCurrentlyRunningTask() throws Exception {
        // we still need to return genre, otherwise it'll fail at first argument check
        Genre requestedGenre = new Genre(1L, "Name");
        GenreService genreServiceMock = mock(GenreService.class);
        when(genreServiceMock.getById(requestedGenre.getId())).thenReturn(requestedGenre);

        // barrier will indicate that the thread is currently
        // in the loop which prints progress to console
        CountDownLatch executingInUpdateProgressLoopLatch = new CountDownLatch(1);
        ProgressCounter progressCounterMock = mock(ProgressCounter.class);
        when(progressCounterMock.getProgress()).then((Answer<Double>) invocation -> {
            executingInUpdateProgressLoopLatch.countDown();
            return 0d;
        });

        // using real completableFuture and delegating cancel() to it, easier than making a mock
        CompletableFuture<Double> delegatedCompletableFuture = new CompletableFuture<>();
        DelegateProgressableFuture<Double> progressableFuture =
                new DelegateProgressableFuture<>(delegatedCompletableFuture, progressCounterMock);

        MovieService movieServiceMock = mock(MovieService.class);
        when(movieServiceMock.getAverageRatingForGenre(requestedGenre)).thenReturn(progressableFuture);
        MovieCommands movieCommands = new MovieCommands(movieServiceMock, genreServiceMock);

        // calling averageVote command in a separate thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> averageRatingResponseFuture = executor.submit(() -> movieCommands.averageVote(1L));
        executor.shutdown(); // graceful shutdown will allow tasks to finish

        // make sure calculation is in progress (printing progress to console)
        executingInUpdateProgressLoopLatch.await();
        assertFalse(averageRatingResponseFuture.isDone());
        assertFalse(delegatedCompletableFuture.isCancelled());

        // cancel it out
        movieCommands.cancelCurrentlyExecutingTask();
        assertTrue(delegatedCompletableFuture.isCancelled());

        String commandResult = averageRatingResponseFuture.get();
        assertEquals("Calculation has been canceled", commandResult);
    }

    @Test
    public void shouldReturnCorrectAverageRatingForGenre() throws Exception {
        Genre requestedGenre = new Genre(1L, "Name");
        double expectedAverageRating = 5d;

        GenreService genreServiceMock = mock(GenreService.class);
        when(genreServiceMock.getById(requestedGenre.getId())).thenReturn(requestedGenre);

        //noinspection unchecked
        ProgressableFuture<Double> returnedProgressableFuture = mock(ProgressableFuture.class);
        when(returnedProgressableFuture.isDone()).thenReturn(true);
        when(returnedProgressableFuture.isCancelled()).thenReturn(false);
        when(returnedProgressableFuture.get()).thenReturn(expectedAverageRating);

        MovieService movieServiceMock = mock(MovieService.class);
        when(movieServiceMock.getAverageRatingForGenre(requestedGenre)).thenReturn(returnedProgressableFuture);

        MovieCommands movieCommands = new MovieCommands(movieServiceMock, genreServiceMock);

        String averageVoteResponse = movieCommands.averageVote(1);
        String expectedResponse = String.format(
                "Average rating for movies of genre \"%s\" is %f",
                requestedGenre.getName(), expectedAverageRating
        );
        assertEquals(expectedResponse, averageVoteResponse);
    }

    @Test
    public void shouldReturnIfGivenGenreIdIsNotFound() throws Exception {
        MovieService movieServiceMock = mock(MovieService.class);

        GenreService genreServiceMock = mock(GenreService.class);
        when(genreServiceMock.getById(2L)).thenReturn(null);

        MovieCommands movieCommands = new MovieCommands(movieServiceMock, genreServiceMock);

        String returnedGenreNotFoundMessage = movieCommands.averageVote(2L);
        assertEquals("Genre with given id not found", returnedGenreNotFoundMessage);
    }
}