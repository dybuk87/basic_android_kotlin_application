package pl.dybuk.health.core.common.rx;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleFuture<T> implements Future<T> {

    private T result;

    private Throwable error;

    private boolean isDone = false;
    private boolean isCanceled = false;

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        isCanceled = true;
        return true;
    }

    @Override
    public synchronized boolean isCancelled() {
        return isCanceled;
    }

    @Override
    public synchronized boolean isDone() {
        return isDone;
    }

    @Override
    public synchronized T get() throws ExecutionException, InterruptedException {
        while (!isDone) {
            wait();
        }

        if (error != null) {
            throw new ExecutionException(error);
        }

        return result;
    }

    @Override
    public synchronized T get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        if (!isDone) {
            wait(unit.toMillis(timeout));
        }

        if (!isDone) {
            throw new TimeoutException();
        }

        if (error != null) {
            throw new ExecutionException(error);
        }

        return result;
    }

    public synchronized void setResult(T result) {
        this.isDone = true;
        this.result = result;
        this.error = null;
        notifyAll();
    }

    public synchronized void setError(Throwable error) {
        this.isDone = true;
        this.result = null;
        this.error = error;
        notifyAll();
    }
}
