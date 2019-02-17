package ru.loaltyplant.movierater.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.service.GenreService;

import java.util.Comparator;
import java.util.stream.Collectors;

@ShellComponent
public class GenreCommands {

    private final GenreService genreService;

    @Autowired
    public GenreCommands(GenreService genreService) {
        this.genreService = genreService;
    }

    @ShellMethod("Lists all genres")
    public String genres(@ShellOption(value = "--list", defaultValue = "true") boolean list) {
        if (list) {
            return genreService.getAll().stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .map(genre -> String.format("%d. \"%s\"", genre.getId(), genre.getName()))
                    .collect(Collectors.joining(System.lineSeparator()));
        }
        throw new IllegalStateException("Task execution error, should not get to here");
    }
}
