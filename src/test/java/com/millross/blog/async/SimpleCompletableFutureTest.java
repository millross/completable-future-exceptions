package com.millross.blog.async;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class SimpleCompletableFutureTest {

    @Test
    public void testSimpleFutureCompletion() throws Exception {
        final CompletableFuture<Integer> future = blockingSuccessFuture(1);
        assertThat(future.get(), is(1));
    }

    @Test
    public void demoThenApply() throws Exception {
        final CompletableFuture<Integer> future = blockingSuccessFuture(1)
                .thenApply(i -> i + 3);
        assertThat(future.get(), is(4));
    }

    @Test
    public void demoThenCompose() throws Exception {
        // We'll use one future to specify how long a subsequent future will take
        final CompletableFuture<Integer> future = blockingSuccessFuture(3)
                .thenApply(i -> {
                    System.out.println("First future completed, " + i);
                    return i;
                })
                .thenCompose(i ->  specifiedDelayFuture(i * 100, 2));
        assertThat(future.get(), is(2));
    }

    public CompletableFuture<Integer> blockingSuccessFuture(final int value) {
        final CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        final Thread futureThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                future.complete(value);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }

        });
        // Kick off processing
        futureThread.run();
        return future;
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
