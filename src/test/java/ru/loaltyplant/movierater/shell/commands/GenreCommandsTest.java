package ru.loaltyplant.movierater.shell.commands;

import org.junit.jupiter.api.Test;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.service.GenreService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenreCommandsTest {

    @Test
    public void shouldReturnAllGenresIfFlagSet() {
        List<Genre> expectedGenres = new ArrayList<>() {{
            add(new Genre(1L, "1-name"));
            add(new Genre(2L, "2-name"));
            add(new Genre(3L, "3-name"));
        }};

        GenreService genreServiceMock = mock(GenreService.class);
        when(genreServiceMock.getAll()).thenReturn(expectedGenres);

        GenreCommands genreCommands = new GenreCommands(genreServiceMock);

        String commandReturnValue = genreCommands.genres(true);
        String expectedReturnValue = "1. \"1-name\"" + System.lineSeparator() +
                "2. \"2-name\"" + System.lineSeparator() +
                "3. \"3-name\"";

        assertEquals(expectedReturnValue, commandReturnValue);
    }

    @Test
    public void shouldThrowIllegalStateExceptionIfFlagEnabledByDefaultIsSetToFalse() {
        GenreService genreServiceMock = mock(GenreService.class);
        GenreCommands genreCommands = new GenreCommands(genreServiceMock);

        assertThrows(IllegalStateException.class, () -> {
            genreCommands.genres(false);
        });
    }
}