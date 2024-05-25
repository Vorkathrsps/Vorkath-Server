package com.cryptic.model.entity.combat.method.impl.arceuus;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

public class RangeThrall extends NPC {
    public RangeThrall(int id, Tile tile, boolean spawn) {
        super(id, tile, spawn);
        this.canAttack(false);
        this.animate(9048);
        this.graphic(1904);
        Chain.bound(this).name("ranging thrall").runFn(40, this::clearList);
    }

    public void sendThrallAttack(Entity entity, Player player) {
        var playerTarget = player.getCombat().getTarget();
        entity.setPositionToFace(playerTarget.tile());
        this.animate(5512);
        int tileDist = entity.tile().distance(playerTarget.tile());
        int duration = (61 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, playerTarget, 1906, 61, duration, 30, 30, 16, playerTarget.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        final Hit hit = Hit.builder(entity, playerTarget, Utils.random(0, 3), delay, CombatType.RANGED).postDamage(d -> {
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
