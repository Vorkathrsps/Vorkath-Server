package com.cryptic.core.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ContinuationScope {
    final Object impl;

    static final Class<?> IMPL_CLASS;
    static final MethodHandle NEW;

    static {
        try {
            IMPL_CLASS = Class.forName("jdk.internal.vm.ContinuationScope");

            var lookup = MethodHandles.privateLookupIn(
                    IMPL_CLASS,
                    MethodHandles.lookup()
            );

            NEW = lookup.findConstructor(
                    IMPL_CLASS,
                    MethodType.methodType(void.class, String.class)
            );

        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    public ContinuationScope(String name) {
        try {
            this.impl = NEW.invoke(name);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return impl.toString();
    }
}
