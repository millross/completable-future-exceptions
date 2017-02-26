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
                .thenCompose(i -> supplyAsync(delayedValueSupplier(2, i * 100), executor));
        assertThat(future.get(), is(2));
    }

    @Test
    public void demoMultiStagePipeline() throws Exception {
        // We'll use one future to specify how long a subsequent future will take
        final CompletableFuture<Integer> future = supplyAsync(delayedValueSupplier(3), executor)
                .thenApply(i -> {
                    System.out.println("First future completed, " + i);
                    return i + 1;
                })
                .thenCompose(i -> supplyAsync(delayedValueSupplier(i + 2, i * 100), executor));
        assertThat(future.get(), is(6));
    }

    public Supplier<Integer> delayedValueSupplier(final int value) {
        return delayedValueSupplier(value, 1000);
    }

    public Supplier<Integer> delayedValueSupplier(final int value, final int delayMs) {
        return () -> {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                throw new RuntimeException("Problem while waiting to return value");
            }
            return value;
        };
    }

}
