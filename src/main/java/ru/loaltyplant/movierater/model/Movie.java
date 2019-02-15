package ru.loaltyplant.movierater.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Movie implements Serializable {
    private long id;
    private List<Long> genreIds;
}
