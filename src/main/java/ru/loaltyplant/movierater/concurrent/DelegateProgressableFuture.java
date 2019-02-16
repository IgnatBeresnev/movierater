package ru.loaltyplant.movierater.concurrent;

import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ThreadSafe
public class DelegateProgressableFuture<V> implements ProgressableFuture<V> {

    private final Future<V> future;
    private final ProgressCounter progressCounter;

    public DelegateProgressableFuture(Future<V> future, ProgressCounter progressCounter) {
        this.future = future;
        this.progressCounter = progressCounter;
    }

    @Override
    public double getProgress() {
        return progressCounter.getProgress();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public V get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public String toString() {
        return future.toString();
    }
}
