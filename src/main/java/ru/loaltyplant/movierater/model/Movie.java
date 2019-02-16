package ru.loaltyplant.movierater.model;

import lombok.Data;
import net.jcip.annotations.ThreadSafe;

import java.io.Serializable;
import java.util.Set;

@Data
@ThreadSafe
public class Movie implements HasId, Serializable {
    private final long id;
    private final Set<Long> genreIds;
    private final double averageRating;
}
