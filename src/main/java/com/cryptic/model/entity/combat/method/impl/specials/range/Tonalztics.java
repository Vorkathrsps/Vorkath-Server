package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;

public class Tonalztics extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(10914);
        entity.graphic(2725);
        final int distance = entity.tile().distance(target.getCentrePosition());
        final int duration = 36 + 16 + (distance * 2);
        Projectile initial = new Projectile(entity, target, 2727, 36, duration, 37, 36, 255, entity.getSize(), 2);
        final int delay = entity.executeProjectile(initial);
        final int ricochetDuration = (initial.getSpeed() + 42) + 16 + (distance * 2);
        Projectile end = new Projectile(target, entity, 2727, initial.getSpeed() + 42, ricochetDuration, 36, 37, 255, entity.getSize(), 2);
        target.executeProjectile(end);
        target.graphic(2731, GraphicHeight.HIGH, initial.getSpeed());
        new Hit(entity, target, delay + 1, CombatType.RANGED)
            .checkAccuracy(true)
            .submit()
            .postDamage(post -> {
                if (post.isAccurate()) {
                    if (target instanceof NPC npc) {
                        int defence = npc.getCombatInfo().stats.defence;
                        int magic = npc.getCombatInfo().stats.magic;
                        int formula = (int) (magic * 0.10D);
                        int output = defence - formula;
                        npc.getCombatInfo().stats.defence = Math.max(0, output);
                    } else if (target instanceof Player player) {
                        int defence = player.skills().level(Skills.DEFENCE);
                        int magic = player.skills().level(Skills.MAGIC);
                        int formula = (int) (magic * 0.10D);
                        int output = defence - formula;
                        player.skills().alterSkill(Skills.DEFENCE, Math.max(0, output));
                    }
                }
            });
        new Hit(entity, target, delay + 1, CombatType.RANGED)
            .checkAccuracy(true)
            .submit()
            .postDamage(post -> {
                if (post.isAccurate()) {
                    if (target instanceof NPC npc) {
                        int defence = npc.getCombatInfo().stats.defence;
                        int magic = npc.getCombatInfo().stats.magic;
                        int formula = (int) (magic * 0.10D);
                        int output = defence - formula;
                        npc.getCombatInfo().stats.defence = Math.max(0, output);
                    } else if (target instanceof Player player) {
                        int defence = player.skills().level(Skills.DEFENCE);
                        int magic = player.skills().level(Skills.MAGIC);
                        int formula = (int) (magic * 0.10D);
                        int output = defence - formula;
                        player.skills().alterSkill(Skills.DEFENCE, Math.max(0, output));
                    }
                }
            });
        CombatSpecial.drain(entity, CombatSpecial.TONALZTICK.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 6;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 5;
    }
}
