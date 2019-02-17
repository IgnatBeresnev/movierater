package ru.loaltyplant.movierater.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.service.GenreService;

import java.util.Comparator;

@ShellComponent
public class GenreCommands {

    private final GenreService genreService;

    @Autowired
    public GenreCommands(GenreService genreService) {
        this.genreService = genreService;
    }

    @ShellMethod("Lists all genres")
    public void genres(@ShellOption(value = "--list", defaultValue = "true") boolean list) {
        if (list) {
            genreService.getAll().stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .forEach(genre -> {
                        System.out.println(String.format("%d. \"%s\"", genre.getId(), genre.getName()));
                    });
        }
    }
}
