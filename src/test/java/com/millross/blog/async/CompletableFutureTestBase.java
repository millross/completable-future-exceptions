package com.millross.blog.async;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Common base class for testing completable futures for the blog series around completable futures and exceptions
 */
public class CompletableFutureTestBase {

    // We'll allow a 2-sized thread pool for async execution, rather than using the fork/join pool for asynchronous
    // value generation
    protected final Executor executor = Executors.newFixedThreadPool(2);

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
