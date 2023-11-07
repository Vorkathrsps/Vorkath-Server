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
import com.cryptic.model.map.position.Tile;
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
        if (player.getKrakenInstance() == null || player.getInstancedArea() == null) return;
        NPC[] npcs = new NPC[]{new NPC(5534, new Tile(2275, 10034, player.getKrakenInstance().getzLevel())), new NPC(5534, new Tile(2284, 10034, player.getKrakenInstance().getzLevel())), new NPC(5534, new Tile(2284, 10038, player.getKrakenInstance().getzLevel())), new NPC(5534, new Tile(2275, 10038, player.getKrakenInstance().getzLevel()))};
        for (var n : npcs) {
            n.setInstancedArea(player.getKrakenInstance());
            n.spawn(false);
            n.noRetaliation(true);
            n.setCombatMethod(new Tentacles());
            player.getKrakenInstance().getNonAwakenedTentacles().add(n);
        }
        if (npc.id() == KrakenBoss.KRAKEN_NPCID) {
            npc.transmog(KrakenBoss.KRAKEN_WHIRLPOOL, true);
            npc.setCombatMethod(new Kraken());
            player.getKrakenInstance().setKrakenState(KrakenState.ALIVE);
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
        hit.postDamage(d -> {
            if (player.getKrakenInstance().getNonAwakenedTentacles().isEmpty()) {
                kraken.transmog(494, true);
                kraken.animate(7135);
                kraken.setCombatMethod(this);
                kraken.setInstancedArea(player.getKrakenInstance());
                player.getKrakenInstance().setKrakenState(KrakenState.ALIVE);
                Chain.noCtx().runFn(4, () -> {
                    kraken.getCombat().setTarget(player);
                    this.setAwakened(true);
                });
            }
        });
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
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
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
        for (var n : player.getKrakenInstance().getNonAwakenedTentacles()) {
            if (n  == null) continue;
            n.die();
        }
        player.getKrakenInstance().getAwakenedTentacles().clear();
        player.getKrakenInstance().setKrakenState(KrakenState.DEAD);
        kraken.die();
        return true;
    }
}
