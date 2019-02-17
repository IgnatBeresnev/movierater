package ru.loaltyplant.movierater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.loaltyplant.movierater.model.Genre;
import ru.loaltyplant.movierater.repository.genre.GenreRepository;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAll() {
        return genreRepository.getAll();
    }
}
