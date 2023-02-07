package com.aelous.model.content.instance;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

/**
 * @author Patrick van Elderen | June, 28, 2021, 15:02
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
@FunctionalInterface
public interface OnTele {
    void accept(Player player, Tile target);
}
