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
        final CompletableFuture<Integer> future = blockingSuccessFuture();
        assertThat(future.get(), is(1));
    }

    @Test
    public void demoThenApply() throws Exception {
        final CompletableFuture<Integer> future = blockingSuccessFuture()
                .thenApply(i -> i + 3);
        assertThat(future.get(), is(4));
    }

    public CompletableFuture<Integer> blockingSuccessFuture() {
        final CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        final Thread futureThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                future.complete(1);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }

        });
        // Kick off processing
        futureThread.run();
        return future;
    }

}
