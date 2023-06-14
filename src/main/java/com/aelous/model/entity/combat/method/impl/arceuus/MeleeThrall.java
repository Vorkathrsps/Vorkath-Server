package com.aelous.model.entity.combat.method.impl.arceuus;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

public class MeleeThrall extends NPC {
    public MeleeThrall(int id, Tile tile, boolean spawn) {
        super(id, tile, spawn); //10886 , opening gfx 1905
        this.canAttack(false);
        this.animate(9007);
        this.graphic(1905, GraphicHeight.LOW, 0);
        Chain.bound(this).name("melee thrall").runFn(40, this::clearList);
    }

    public void sendThrallAttack(Entity entity, Player player) {
        var playerTarget = player.getCombat().getTarget();
        entity.setPositionToFace(playerTarget.tile());
        this.animate(5568);
        final Hit hit = Hit.builder(entity, playerTarget, Utils.random(0, 3), 0, CombatType.MELEE).postDamage(d -> {
            if (d.getDamage() == 0) {
                d.block();
            }
        });
        hit.setAccurate(true);
        hit.submit();
    }

    public void clearList() {
        this.getActiveThrall().remove(this);
        this.remove();
    }
}
