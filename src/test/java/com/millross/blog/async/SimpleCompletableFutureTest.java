package com.millross.blog.async;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class SimpleCompletableFutureTest extends CompletableFutureTestBase {

    @Test
    public void testSimpleFutureCompletion() throws Exception {
        final CompletableFuture<Integer> future = supplyAsync(delayedValueSupplier(1), executor);
        assertThat(future.get(), is(1));
    }

    @Test(expected = IntentionalException.class)
    public void testExceptionalCompletion() throws Throwable {
        final CompletableFuture<Integer> future = delayedExceptionalCompletion(new IntentionalException());
        try {
            future.join();
        } catch (CompletionException ex) {
            throw (ex.getCause());
        }
    }

    @Test(expected = IntentionalException.class)
    public void exceptionCompletionAsSeenFromNextStage() throws Throwable {

        // This is where we'll store the exception seen in the next stage
        final AtomicReference<Throwable> thrownException = new AtomicReference<>(null);

        final CompletableFuture<Integer> future = delayedExceptionalCompletion(new IntentionalException())
                .whenComplete((v, t) -> {
                    if (t != null) {
                        thrownException.set(t);
                    }
                });

        try {
            future.join();
        } catch (CompletionException ex) {
            throw (Optional.ofNullable(thrownException.get()).orElse(ex));
        }

    }

    @Test
    public void demoThenApply() throws Exception {
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(delayedValueSupplier(1), executor)
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
                    System.out.println("First fut`ure completed, " + i);
                    return i + 1;
                })
                .thenCompose(i -> supplyAsync(delayedValueSupplier(i + 2, i * 100), executor));
        assertThat(future.get(), is(6));
    }


}
