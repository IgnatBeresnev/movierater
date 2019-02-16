package ru.loaltyplant.movierater.repository.genre;

import ru.loaltyplant.movierater.model.Genre;

import java.util.List;

public interface GenreRepository {
    List<Genre> getAll();
}
