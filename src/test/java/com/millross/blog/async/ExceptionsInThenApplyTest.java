package com.millross.blog.async;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class ExceptionsInThenApplyTest extends CompletableFutureTestBase{

    /**
     * Note that while we throw an IntentionalException, get() causes it to be wrapped in an ExecutionException, in
     * effect, get acts as a subsequent completion stage from the point of view of exception propagation.
     * @throws Exception
     */
    @Test(expected = CompletionException.class)
    public void exceptionCallingThenApply() throws Exception {
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(delayedValueSupplier(1), executor)
                .thenApply(i -> {
                    throw new IntentionalException();
                });
        assertThat(future.join(), is(4));

    }

    @Test(expected = CompletionException.class)
    public void natureOfExceptionCallingThenApply() throws Exception {
        final AtomicReference<Throwable> thrownException = new AtomicReference<>(null);
        final CompletableFuture<Void> future = CompletableFuture.supplyAsync(delayedValueSupplier(1), executor)
                .thenApply(i -> {
                    throw new IntentionalException();
                })
                // whenComplete allows us to retain information about exceptional completion
                .whenComplete((v, t) -> Optional.ofNullable(t)
                        // Let's extract the exception from the CompletionStage and record it
                        .ifPresent(thrownException::set))
                // thenAccept effectively loses information about exceptional completion
                .thenAccept(o -> assertThat((thrownException.get() instanceof IntentionalException), is(true)));
        future.join();

    }

}
