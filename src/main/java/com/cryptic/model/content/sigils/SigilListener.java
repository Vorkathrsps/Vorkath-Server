package com.cryptic.model.content.sigils;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;

public interface SigilListener {
    boolean prepare(Player player, Entity target);
}
