package com.cryptic.model.entity.combat.method.impl.npcs.bosses.kraken;

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
import lombok.Getter;
import lombok.Setter;

public class Tentacles extends CommonCombatMethod {
    @Getter
    @Setter
    boolean awakened = false;

    @Override
    public void preDefend(Hit hit) {
        var player = (Player) hit.getAttacker();
        var tentacle = (NPC) entity;
        if (hit.getAttacker() == player && hit.getCombatType() != CombatType.MAGIC) hit.setDamage(0);
        if (player.getKrakenInstance() == null) return;
        if (player.getKrakenInstance().getNonAwakenedTentacles().isEmpty()) return;
        if (!player.getKrakenInstance().getNonAwakenedTentacles().contains(tentacle)) return;
        if (hit.getAttacker() == player && hit.getDamage() > 0) hit.setDamage(0);
        hit.postDamage(d -> {
            player.getKrakenInstance().getNonAwakenedTentacles().remove(tentacle);
            tentacle.transmog(5535, true);
            tentacle.animate(3729);
            tentacle.setCombatMethod(this);
            tentacle.setInstance(player.getKrakenInstance());
            player.getKrakenInstance().getAwakenedTentacles().add(tentacle);
            Chain.noCtx().runFn(4, () -> {
                tentacle.animate(-1);
                tentacle.getCombat().setTarget(player);
                this.setAwakened(true);
            });
        });
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!this.isAwakened()) return false;
        if (!withinDistance(8)) return false;
        hit(entity, target);
        return true;
    }

    private void hit(Entity entity, Entity target) {
        entity.getCombat().setTarget(target);
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 162, 51, duration, 43, 31, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        if (hit.isAccurate()) {
            target.graphic(163, GraphicHeight.MIDDLE, p.getSpeed());
        } else {
            hit.setDamage(0);
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
}
