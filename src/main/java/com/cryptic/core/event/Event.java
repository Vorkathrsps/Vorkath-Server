package com.cryptic.core.event;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Event<T> {
    private static final Logger logger = LoggerFactory.getLogger(Event.class);
    @Getter
    private int ticks;
    protected final ContinuationScope continuationScope;
    protected final Continuation continuation;
    @Nullable
    protected Runnable onContinue;
    @Nullable
    protected final T context;
    @Nullable
    private Supplier<Boolean> cancelCondition;

    public Event(
        ContinuationScope continuationScope,
        @Nullable T context) {
        this.continuationScope = continuationScope;
        this.continuation = new Continuation(continuationScope, () -> {
            Runnable onContinue = this.onContinue;
            if (onContinue != null) {
                try {
                    onContinue.run();
                } catch (Throwable t) {
                    logger.error("", t);
                }
            }
        });
        this.context = context;
    }

    public final boolean tick() {
        if (ticks > 0) {
            if (--ticks > 0) {
                return true;
            }
            if (cancelCondition != null && cancelCondition.get()) {
                return false;
            }
        }
        try {
            if (continuation.isDone()) {
                return false;
            }
            continuation.run();
            return true;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    public final void delay(int ticks) {
        this.ticks = ticks;

        Continuation.yield(continuationScope);
    }

    public void delay() {
        delay(1);
    }

    public final void waitUntil(Supplier<Boolean> condition, int timeout) {
        int time = 0;
        while (!condition.get() && time < timeout) {
            time++;
            delay(1);
        }
    }

    public final void waitUntil(Supplier<Boolean> condition) {
        waitUntil(condition, Integer.MAX_VALUE);
    }


    public void waitFor(BooleanSupplier supplier) {
        waitUntil(supplier::getAsBoolean);
    }

    /**
     * When returning from a pause (delay), the given condition will be checked, and if met, the event will be stopped
     */
    public final void setCancelCondition(@org.jetbrains.annotations.Nullable Supplier<Boolean> cancelCondition) {
        this.cancelCondition = cancelCondition;
    }

    public final void onContinue(@Nullable Runnable onContinue) {
        this.onContinue = onContinue;
    }

    public void stop() {
        cancelCondition = () -> true;
    }

}
