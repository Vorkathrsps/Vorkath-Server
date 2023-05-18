package com.aelous.model.entity.combat.method;

import com.aelous.model.entity.combat.method.effects.AbilityListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ListenerRegistry {
    private static final Map<AbilityListener, Boolean> listeners = new ConcurrentHashMap<>();

    public static void registerListener(AbilityListener listener) {
        listeners.put(listener, true);
    }

    public static void unregisterListener(AbilityListener listener) {
        listeners.remove(listener);
    }

    public static Set<AbilityListener> getListeners() {
        return listeners.keySet();
    }
}
