package com.cryptic.model.content.instance;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | June, 28, 2021, 15:02
 * 
 */
@FunctionalInterface
public interface OnTele {
    void accept(Player player, Tile target);
}
