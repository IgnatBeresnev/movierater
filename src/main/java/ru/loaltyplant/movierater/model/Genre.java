package ru.loaltyplant.movierater.model;

import lombok.Data;
import net.jcip.annotations.ThreadSafe;

import java.io.Serializable;

@Data
@ThreadSafe
public class Genre implements HasId, Serializable {
    private final long id;
    private final String name;
}
