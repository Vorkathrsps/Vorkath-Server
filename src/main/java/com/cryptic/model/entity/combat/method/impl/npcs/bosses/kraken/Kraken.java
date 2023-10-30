package com.cryptic.model.entity.combat.method.impl.npcs.bosses.kraken;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

public class Kraken extends CommonCombatMethod {
    @Getter
    @Setter
    boolean awakened = false;
    @Override
    public void onRespawn(NPC npc) {
        var player = (Player) target;
        if (player.getKrakenInstance() == null) return;
        player.getKrakenInstance().setKrakenState(KrakenState.ALIVE);
        if (npc.id() == KrakenBoss.KRAKEN_NPCID) {
            npc.transmog(KrakenBoss.KRAKEN_WHIRLPOOL);
            npc.setCombatMethod(new Kraken());
        }
    }

    @Override
    public void preDefend(Hit hit) {
        var player = (Player) hit.getAttacker();
        var kraken = (NPC) entity;
        if (hit.getAttacker() == player && hit.getCombatType() != CombatType.MAGIC) hit.setDamage(0);
        if (player.getKrakenInstance() == null) return;
        if (this.isAwakened()) return;
        if (hit.getAttacker() == player && hit.getDamage() > 0) hit.setDamage(0);
        if (player.getKrakenInstance().getNonAwakenedTentacles().isEmpty()) {
            kraken.transmog(494);
            kraken.animate(7135);
            kraken.setCombatMethod(this);
            kraken.setInstance(player.getKrakenInstance());
            Chain.noCtx().runFn(4, () -> {
                kraken.getCombat().setTarget(player);
                this.setAwakened(true);
            });
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        var kraken = (NPC) entity;
        if (!withinDistance(8)) return false;
        if (!this.isAwakened()) return false;
        if (kraken.id() != 494) return false;
        hit(entity, target);
        return true;
    }

    private static void hit(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 156, 51, duration, 43, 31, 16, 2, 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        if (hit.isAccurate()) {
            target.graphic(157, GraphicHeight.MIDDLE, p.getSpeed());
        } else {
            target.graphic(85, GraphicHeight.LOW, p.getSpeed());
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        var player = (Player) hit.getAttacker();
        var kraken = (NPC) entity;
        for (var n : player.getKrakenInstance().getAwakenedTentacles()) {
            if (n == null) continue;
            n.die();
        }
        player.getKrakenInstance().getAwakenedTentacles().clear();
        player.getKrakenInstance().setKrakenState(KrakenState.DEAD);
        kraken.die();
        return true;
    }
}
