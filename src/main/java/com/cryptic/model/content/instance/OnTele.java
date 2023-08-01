package com.cryptic.model.content.instance;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

/**
 * @author Patrick van Elderen | June, 28, 2021, 15:02
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
@FunctionalInterface
public interface OnTele {
    void accept(Player player, Tile target);
}
