package ru.loaltyplant.movierater.concurrent;

import java.util.concurrent.Future;

public interface ProgressableFuture<V> extends Future<V> {

    /**
     * @return from 0.0 to 100.0
     */
    double getProgress();
}
