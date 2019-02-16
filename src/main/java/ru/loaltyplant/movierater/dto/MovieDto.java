package ru.loaltyplant.movierater.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("genre_ids")
    private List<Long> genreIds;

    @JsonProperty("vote_average")
    private Double voteAverage;
}
