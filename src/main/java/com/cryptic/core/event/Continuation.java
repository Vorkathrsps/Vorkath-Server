package com.cryptic.core.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Continuation {
    final Object impl;

    static final Class<?> IMPL_CLASS;
    static final MethodHandle NEW;
    static final MethodHandle YIELD;

    static final MethodHandle RUN;
    static final MethodHandle IS_DONE;

    static {
        try {
            IMPL_CLASS = Class.forName("jdk.internal.vm.Continuation");

            var lookup = MethodHandles.privateLookupIn(
                    IMPL_CLASS,
                    MethodHandles.lookup()
            );

            NEW = lookup.findConstructor(
                    IMPL_CLASS,
                    MethodType.methodType(void.class, ContinuationScope.IMPL_CLASS, Runnable.class)
            );

            YIELD = lookup.findStatic(
                    IMPL_CLASS,
                    "yield",
                    MethodType.methodType(boolean.class, ContinuationScope.IMPL_CLASS)
            );

            RUN = lookup.findVirtual(
                    IMPL_CLASS,
                    "run",
                    MethodType.methodType(void.class)
            );

            IS_DONE = lookup.findVirtual(
                    IMPL_CLASS,
                    "isDone",
                    MethodType.methodType(boolean.class)
            );
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    public Continuation(ContinuationScope scope, Runnable runnable) {
        try {
            this.impl = NEW.invoke(scope.impl, runnable);
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void yield(ContinuationScope scope) {
        try {
            YIELD.invoke(scope.impl);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void run() {
        try {
            RUN.invoke(impl);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public boolean isDone() {
        try {
            return (boolean) IS_DONE.invoke(impl);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
