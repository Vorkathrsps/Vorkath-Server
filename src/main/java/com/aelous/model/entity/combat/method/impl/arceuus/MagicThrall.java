package com.aelous.model.entity.combat.method.impl.arceuus;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

public class MagicThrall extends NPC {

    public MagicThrall(int id, Tile tile, boolean spawn) {
        super(id, tile, spawn);
        this.canAttack(false);
        this.animate(9047);
        this.graphic(1903, GraphicHeight.LOW, 0);
        Chain.bound(this).name("magic thrall").runFn(40, this::clearList);
    }

    public void sendThrallAttack(Entity entity, Player player) {
        var playerTarget = player.getCombat().getTarget();
        int tileDist = entity.tile().distance(playerTarget.tile());
        int duration = (51 + -5 + (10 * tileDist));
        entity.setPositionToFace(playerTarget.tile());
        entity.animate(5540);
        Projectile p = new Projectile(entity, playerTarget, 1907, 51, duration, 43, 31, 16, playerTarget.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        final Hit hit = Hit.builder(entity, playerTarget, Utils.random(0, 3), delay, CombatType.MAGIC).postDamage(d -> {
            if (d.getDamage() == 0) {
                d.block();
                playerTarget.graphic(85, GraphicHeight.HIGH, p.getSpeed());
            }
        });
        hit.setAccurate(true);
        hit.submit();
        if (hit.isAccurate()) {
            playerTarget.graphic(1908, GraphicHeight.HIGH, p.getSpeed());
        }
    }

    public void clearList() {
        this.getActiveThrall().remove(this);
        this.remove();
    }
}
