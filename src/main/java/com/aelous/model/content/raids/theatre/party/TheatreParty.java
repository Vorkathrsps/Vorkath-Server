package com.aelous.model.content.raids.theatre.party;

import com.aelous.model.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class TheatreParty {
    @Nonnull
    Player player;

    public TheatreParty(@NotNull Player player) {
        this.player = player;
    }
}
