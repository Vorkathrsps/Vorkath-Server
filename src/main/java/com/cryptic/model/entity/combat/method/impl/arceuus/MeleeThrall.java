package com.cryptic.model.entity.combat.method.impl.arceuus;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

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
