package com.cryptic.model.content.sigils;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;

public abstract class AbstractSigilHandler {
    protected abstract void process(Player player, Entity target);
    protected abstract boolean attuned(Player player);
    protected abstract boolean activated(Player player);
}
