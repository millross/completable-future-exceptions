package com.millross.blog.async;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class ExceptionalThenComposeTest extends CompletableFutureTestBase {


    @Test(expected = IntentionalException.class)
    public void exceptionCallingThenApplyAsSeenFromNextStage() throws Throwable {
        final AtomicReference<Throwable> thrownException = new AtomicReference<>(null);
        final CompletableFuture<Object> future = CompletableFuture.supplyAsync(delayedValueSupplier(1), executor)
                .thenCompose(i -> {
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
