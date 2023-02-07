package com.aelous.model.entity.player;

import com.aelous.network.packet.PacketListener;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Kaleem on 20/07/2017.
 */
public final class Triggers {

    private final Multimap<Class<? extends PacketListener>, Runnable> triggers  = HashMultimap.create(); //Might end up invoking context idk

    public Triggers(Player player) {
    }

    public void add(Class<? extends PacketListener> clazz, Runnable runnable) {
        triggers.put(clazz, runnable);
    }

    public void remove(Class<? extends PacketListener> clazz, Runnable runnable) {
        triggers.remove(clazz, runnable);
    }

    public Collection<Runnable> get(Class<? extends PacketListener> clazz) {
        return Optional.ofNullable(triggers.get(clazz)).orElseGet(() -> create(clazz));
    }

    private Collection<Runnable> create(Class<? extends PacketListener> clazz) {
        triggers.put(clazz, () -> {} );
        return triggers.get(clazz);
    }

}
