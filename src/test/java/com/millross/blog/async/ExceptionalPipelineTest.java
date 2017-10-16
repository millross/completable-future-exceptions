package com.millross.blog.async;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Explorations of failures at various stages in CompletionStage pipelines
 */
public class ExceptionalPipelineTest extends CompletableFutureTestBase {
    @Test(expected = IntentionalException.class)
    public void exceptionCompletionFollowedByPipeline() throws Throwable {
        // This is where we'll store the exception seen in the next stage
        final AtomicReference<Throwable> thrownException = new AtomicReference<>(null);

        final CompletableFuture<Integer> future = delayedExceptionalCompletion(new IntentionalException())
                .thenApply(i -> i + 1)
                .thenCompose(i -> CompletableFuture.supplyAsync(delayedValueSupplier(1), executor))
                .whenComplete((v, t) -> {
                    if (t != null) {
                        thrownException.set(t);
                    }
                });

        try {
            future.join();
        } catch (CompletionException ex) {
            throw (Optional.ofNullable(thrownException.get().getCause()).orElse(ex));
        }
    }

}
