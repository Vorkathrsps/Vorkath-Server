package com.cryptic.model.entity.combat.method.impl.arceuus;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

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
