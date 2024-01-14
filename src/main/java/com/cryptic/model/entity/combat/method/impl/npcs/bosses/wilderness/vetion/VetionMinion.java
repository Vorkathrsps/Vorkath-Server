package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Origin | Zerikoth | PVE
 * @date maart 19, 2020 16:52
 */
public class VetionMinion extends NPC {
    public VetionMinion(NPC vetion, Entity target, Tile tile) {
        super(6613, vetion.tile());
        this.putAttrib(AttributeKey.BOSS_OWNER, vetion);
        this.setTile(vetion.tile().transform(Utils.random(3), -1));
        this.walkRadius(8);
        this.respawns(false);
        this.getCombat().attack(target);
    }
}
