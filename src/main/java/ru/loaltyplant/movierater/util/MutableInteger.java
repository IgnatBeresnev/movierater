package ru.loaltyplant.movierater.util;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class MutableInteger {
    private int value;

    public MutableInteger(int initialValue) {
        this.value = initialValue;
    }

    public int incrementAndGet() {
        return ++value;
    }
}
