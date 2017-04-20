package com.millross.blog.async;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class ExceptionsInThenApplyTest extends CompletableFutureTestBase{

    /**
     * Note that while we throw an IntentionalException, get() causes it to be wrapped in an ExecutionException, in
     * effect, get acts as a subsequent completion stage from the point of view of exception propagation.
     * @throws Exception
     */
    @Test(expected = IntentionalException.class)
    public void exceptionCallingThenApply() throws Throwable {
        final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(delayedValueSupplier(1), executor)
                .thenApply(i -> {
                    throw new IntentionalException();
                });

        try {
            future.join();
        } catch (CompletionException ex) {
            throw ex.getCause();
        }

    }

    @Test(expected = IntentionalException.class)
    public void exceptionCallingThenApplyAsObservedFromNextStage() throws Throwable {
        final AtomicReference<Throwable> thrownException = new AtomicReference<>(null);
        final CompletableFuture<Object> future = CompletableFuture.supplyAsync(delayedValueSupplier(1), executor)
                .thenApply(i -> {
                    throw new IntentionalException();
                })
                .whenComplete((v, t) -> {
                    if (t != null) {
                        thrownException.set(t);
                    }
                });
        try {
            future.join();
        } catch (CompletionException ex) {
            throw (Optional.ofNullable(thrownException.get().getCause()).orElse(new RuntimeException("No thrown exception")));
        }
    }

}
