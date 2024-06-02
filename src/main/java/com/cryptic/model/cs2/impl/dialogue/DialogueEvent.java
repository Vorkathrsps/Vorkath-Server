package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.core.event.Event;
import com.cryptic.core.event.EventWorker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

public abstract class DialogueEvent<T> extends Event<T> {

    private final AtomicInteger phase = new AtomicInteger(0);
    private final AtomicInteger option = new AtomicInteger(0);

    public DialogueEvent(@Nullable T context) {
        super(EventWorker.CONTINUATION_SCOPE, context);
    }

    public void select(int option) {
        waitFor(isOption(option));
        this.option.getAndIncrement();
        next();
        phase.incrementAndGet();
    }

    public void next() {
        waitFor(isNext());
        phase.incrementAndGet();
    }

    private @NotNull BooleanSupplier isOption(final int next) {
        return () -> option.get() != next;
    }

    private @NotNull BooleanSupplier isNext() {
        int current = phase.get();
        return () -> current != phase.get();
    }

    public void interrupt() {
        stop();
    }
}

