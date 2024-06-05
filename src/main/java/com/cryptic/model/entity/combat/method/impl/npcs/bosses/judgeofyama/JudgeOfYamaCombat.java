package com.cryptic.model.entity.combat.method.impl.npcs.bosses.judgeofyama;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.Prayer;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.masks.impl.tinting.Tinting;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.chainedwork.Chain;

public class JudgeOfYamaCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        target.getAsPlayer().getPacketSender().sendEntityHint(entity);

        var roll = World.getWorld().random(10);

        if (entity.dead()) {
            target.getAsPlayer().getPacketSender().sendEntityHint(entity);
        }

        if (withinDistance(1) && World.getWorld().rollDie(2, 1)) {
            Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MAGIC).checkAccuracy(true);
            hit.submit();
        }

        switch (roll) {
            case 0, 1 -> magicAttack(entity, target);
            case 2, 3 -> corruptedVoid(entity, target);
            case 4, 5 -> rangeAttack(entity, target);
        }

        return true;
    }

    public void rangeAttack(Entity entity, Entity target) {
        entity.animate(69);
        double dist = entity.tile().distance(target.getCentrePosition());
        double delay = (int) (1 + Math.floor(3 + dist) / 6D);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), (int) delay, CombatType.MAGIC).checkAccuracy(true);
        hit.submit();
    }

    public void magicAttack(Entity entity, Entity target) {
        entity.animate(69);
        entity.forceChat("FOR THE GODS OF ZARYTE!");
        double dist = entity.tile().distance(target.getCentrePosition());
        double delay = entity.getCombat().magicSpellDelay(target);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), (int) delay, CombatType.MAGIC).checkAccuracy(true);
        target.performGraphic(new Graphic(156, GraphicHeight.HIGH, (int) (delay + 1)));
        hit.submit();
    }

    public void corruptedVoid(Entity entity, Entity target) {
        short delay = 0;
        short duration = 240;
        byte hue = 50;
        byte sat = 80;
        byte lum = 20;
        byte opac = 112;
        entity.stopActions(true);
        entity.animate(new Animation(6950, Priority.HIGH));
        entity.forceChat("I WILL CONSUME YOUR SOUL!");
        entity.setTinting(new Tinting(delay, duration, (byte) 0, (byte) 0, (byte) 0, (byte) 100));
        World.getWorld().getPlayers().forEach(p -> Chain.bound(null).runFn(0, () -> {
            if (entity.isRegistered() && !entity.dead() && target.tile().inSqRadius(entity.tile(), 8)) {
                target.setTinting(new Tinting(delay, duration, hue, sat, lum, opac));
                Hit hit = target.hit(entity, 40, delay, CombatType.MAGIC).setAccurate(true);
                if (target.getAsPlayer().getPrayer().isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) {
                    hit.setDamage(20);
                }
                target.performGraphic(new Graphic(1430, GraphicHeight.LOW, delay));
                hit.submit();
                entity.heal((int) (target.getSkills().level(Skills.HITPOINTS) * .10));
            }
        }).then(1, entity::unlock));
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        return super.customOnDeath(hit);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return false;
    }
}
