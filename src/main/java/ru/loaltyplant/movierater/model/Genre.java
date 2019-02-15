package ru.loaltyplant.movierater.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Genre implements Serializable {
    private final long id;
    private final String name;
}
