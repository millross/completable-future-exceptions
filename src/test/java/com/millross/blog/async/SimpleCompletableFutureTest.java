package com.millross.blog.async;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class SimpleCompletableFutureTest {

    // We'll allow a 3-sized thread pool for async execution, rather than using the fork/join pool for asynchronous
    // value generation
    final Executor executor = Executors.newFixedThreadPool(3);

    @Test
    public void testSimpleFutureCompletion() throws Exception {
        final CompletableFuture<Integer> future = supplyAsync(delayedValueSupplier(1), executor);
        assertThat(future.get(), is(1));
    }

    @Test
    public void demoThenApply() throws Exception {
        final CompletableFuture<Integer> future = supplyAsync(delayedValueSupplier(1), executor)
                .thenApply(i -> i + 3);
        assertThat(future.get(), is(4));
    }

    @Test
    public void demoThenCompose() throws Exception {
        // We'll use one future to specify how long a subsequent future will take
        final CompletableFuture<Integer> future = supplyAsync(delayedValueSupplier(3), executor)
                .thenApply(i -> {
                    System.out.println("First future completed, " + i);
                    return i;
                })
                .thenCompose(i -> specifiedDelayFuture(i * 100, 2));
        assertThat(future.get(), is(2));
    }

    public Supplier<Integer> delayedValueSupplier(final int value) {
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Problem while waiting to return value");
            }
            return value;
        };
    }

    public CompletableFuture<Integer> specifiedDelayFuture(final int delay, final int finalValue) {
        final CompletableFuture<Integer> future = new CompletableFuture<>();
        final Thread futureThread = new Thread(() -> {
            try {
                Thread.sleep(delay);
                future.complete(finalValue);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }

        });
        // Kick off processing
        futureThread.run();
        return future;
    }

}
