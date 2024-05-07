package com.cryptic.model.entity.combat.method.impl.npcs.slayer.kraken;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.chainedwork.Chain;

public class CaveKrakenCombat extends CommonCombatMethod {

    @Override
    public void preDefend(Hit hit) {
        var player = (Player) hit.getAttacker();
        var kraken = (NPC) entity;
        if (hit.getCombatType() != CombatType.MAGIC) hit.block();
        if (kraken.id() == NpcIdentifiers.CAVE_KRAKEN) return;
        if (hit.getAttacker() == player && hit.getDamage() > 0) hit.block();
        hit.postDamage(h -> {
           kraken.transmog(NpcIdentifiers.CAVE_KRAKEN, true);
           kraken.animate(7135);
           kraken.setCombatMethod(this);
           Chain.noCtx().runFn(5, () -> kraken.getCombat().setTarget(player));
        });
    }

    @Override
    public void onRespawn(NPC npc) {
        if (npc.id() == NpcIdentifiers.CAVE_KRAKEN) {
            npc.transmog(493, true);
            npc.setCombatMethod(this);
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 156, 51, duration, 43, 31, 16, 2, 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
        hit.submit();
        if (hit.isAccurate()) {
            target.graphic(157, GraphicHeight.MIDDLE, p.getSpeed());
        } else {
            target.graphic(85, GraphicHeight.LOW, p.getSpeed());
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}
