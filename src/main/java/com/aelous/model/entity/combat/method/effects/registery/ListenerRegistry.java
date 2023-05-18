package com.aelous.model.entity.combat.method.effects.registery;

import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ListenerRegistry {
    private static final Map<DamageEffectListener, Boolean> listeners = new ConcurrentHashMap<>();

    public static void registerListener(DamageEffectListener listener) {
        listeners.put(listener, true);
    }

    public static void unregisterListener(DamageEffectListener listener) {
        listeners.remove(listener);
    }

    public static Set<DamageEffectListener> getListeners() {
        return listeners.keySet();
    }
}
