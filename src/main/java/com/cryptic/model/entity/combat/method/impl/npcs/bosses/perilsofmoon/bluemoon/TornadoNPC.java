package com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.PerilOfMoonInstance;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;

public class TornadoNPC extends NPC {

    PerilOfMoonInstance instance;

    public TornadoNPC(int id, Tile tile, PerilOfMoonInstance instance) {
        super(id, tile);
        this.instance = instance;
    }

    @Override
    public boolean beforeAttack() {
        for (var player : instance.getPlayers()) {
            if (player.tile().equals(this.tile())) {
                var damage = Utils.random(1, 11);
                player.putAttrib(AttributeKey.TORNADO_DAMAGE, damage);
                this.submitHit(player, 0, Utils.random(1, 11), HitMark.HIT);
                return true;
            }
        }
        return false;
    }
}
