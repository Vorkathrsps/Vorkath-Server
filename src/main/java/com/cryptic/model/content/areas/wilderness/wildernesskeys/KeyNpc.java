package com.cryptic.model.content.areas.wilderness.wildernesskeys;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Tuple;
import lombok.Getter;

import static com.cryptic.model.entity.attributes.AttributeKey.PLAYER_UID;

public class KeyNpc extends NPC {
    @Getter
    public final Player player;

    /**
     * Constructor for @KeyNpc
     * @param id
     * @param tile
     * @param player
     */
    public KeyNpc(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        Long uid = this.<Long>getAttribOr(PLAYER_UID, 0L);
        this.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(uid, player));
        this.getCombatInfo().setAggressive(true);
        this.respawns(false);
        this.getCombat().setTarget(player);
        this.setPositionToFace(player.tile());
    }

    /**
     * @KeyNpc Death Sequence
     */
    @Override
    public void die() {
        player.getWildernessKeys().onDeath(player, this);
    }
}
