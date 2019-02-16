package ru.loaltyplant.movierater.model;

import lombok.Data;
import net.jcip.annotations.ThreadSafe;
import ru.loaltyplant.movierater.dto.MovieDto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@ThreadSafe
public class Movie implements HasId, Serializable {
    private final long id;
    private final Set<Long> genreIds;
    private final double averageRating;

    public static Movie fromDto(MovieDto movieDto) {
        return new Movie(movieDto.getId(), new HashSet<>(movieDto.getGenreIds()), movieDto.getVoteAverage());
    }
}
