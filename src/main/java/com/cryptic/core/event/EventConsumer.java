package com.cryptic.core.event;

@FunctionalInterface
public interface EventConsumer<T, E extends Event<T>> {

    void accept(E event);

}
