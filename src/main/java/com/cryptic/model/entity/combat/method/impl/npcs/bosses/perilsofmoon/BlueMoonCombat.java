package com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

import java.util.function.BooleanSupplier;

public class BlueMoonCombat extends CommonCombatMethod {
    boolean initiated = false;
   //attack 11014
    @Override
    public void init(NPC npc) {
    }

    @Override
    public void doFollowLogic() {
        this.entity.setEntityInteraction(null);
        this.entity.face(null);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        return true;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        NPC moon = (NPC) this.entity;
        moon.hidden(true);
        World.getWorld().tileGraphic(2790, moon.tile().transform(2, 1), 0, 0);
        moon.die();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }

}
