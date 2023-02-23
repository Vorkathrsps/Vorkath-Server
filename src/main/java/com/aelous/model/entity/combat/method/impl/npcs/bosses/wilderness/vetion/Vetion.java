package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.util.ArrayList;
import java.util.List;

public class Vetion extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        /*if (entity.hp() <= 125 && !entity.hasAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED)) {
            spawnHellhounds((NPC) entity, target);
        }

        //If the 5 minute timer has expired we revert vetion back to his original form.
        if (!entity.getTimers().has(TimerKey.VETION_REBORN_TIMER) && ((NPC) entity).transmog() == 6612) {
            ((NPC) entity).transmog(6611);
        }

        if (Utils.rollDie(20, 1)) { // 5% chance the target sends his bitch ass lightning
            doMagic();
        } else if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            entity.animate(5499);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        } else {
            doMagic();
        }*/

        var roll = World.getWorld().random(8);

       // switch (roll) {
        //    case 0, 1 -> doMagicSwordRaise();
        //    case 2, 3 -> doMagicSwordSlash();
       // }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 6;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }

    private void doMagicSwordRaise() {
        int startSpeed = 51, stepMultiplier = 10, tileDist = entity.tile().distance(target.tile());
        Tile lightning_one = target.tile().transform(0, 1);
        Tile lightning_two = lightning_one.transform(1, 0);
        Tile lightning_three = lightning_one.transform(1, 1);
        entity.animate(9969, 0);
        Chain.bound(entity).runFn(1, () -> {
            entity.performGraphic(new Graphic(2344, GraphicHeight.MIDDLE, 0));
        }).then(2, () -> {
            if (target != null && target.isPlayer() && !target.dead() && entity.isRegistered() && !entity.dead()) {
                int duration = (startSpeed + -5 + (stepMultiplier * tileDist));
                World.getWorld().tileGraphic(2346, lightning_one, 0, duration);
                World.getWorld().tileGraphic(2346, lightning_one, 0, duration);
                World.getWorld().tileGraphic(2346, lightning_two, 0, duration);
                World.getWorld().tileGraphic(2346, lightning_three, 0, duration);
                if (target.tile().equals(lightning_one) || target.tile().equals(lightning_two) || target.tile().equals(lightning_three)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), duration, CombatType.MAGIC).checkAccuracy();
                    hit.submit();
                } else if (target.tile().isWithinDistance(lightning_one, 1)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), duration, CombatType.MAGIC).checkAccuracy();
                    hit.submit();
                    hit.setDamage(hit.getDamage() / 2);
                }
            }
        });
    }

    private void doMagicSwordSlash() {
        int startSpeed = 51, stepMultiplier = 10, tileDist = entity.tile().distance(target.tile());
        Tile lightning_one = target.tile().transform(0, 1);
        Tile lightning_two = lightning_one.transform(1, 0);
        Tile lightning_three = lightning_one.transform(1, 1);
        entity.animate(9972);
        Chain.bound(entity).runFn(1, () -> {
            entity.performGraphic(new Graphic(2344, GraphicHeight.MIDDLE, 0));
        }).then(0, () -> {
            if (target != null && target.isPlayer() && !target.dead() && entity.isRegistered() && !entity.dead()) {
                int duration = (startSpeed + -10 + (stepMultiplier * tileDist));
                World.getWorld().tileGraphic(2346, lightning_one, 0, duration);
                World.getWorld().tileGraphic(2346, lightning_one, 0, duration);
                World.getWorld().tileGraphic(2346, lightning_two, 0, duration);
                World.getWorld().tileGraphic(2346, lightning_three, 0, duration);
                if (target.tile().equals(lightning_one) || target.tile().equals(lightning_two) || target.tile().equals(lightning_three)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), duration, CombatType.MAGIC).checkAccuracy();
                    hit.submit();
                } else if (target.tile().isWithinDistance(lightning_one, 1)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), duration, CombatType.MAGIC).checkAccuracy();
                    hit.submit();
                    hit.setDamage(hit.getDamage() / 2);
                }
            }
        });
    }

    private void spawnHellhounds(NPC vetion, Entity target) {
        List<NPC> minions = new ArrayList<>();
        for (int index = 0; index < 2; index++) {
            VetionMinion minion = new VetionMinion(vetion, target);
            minions.add(minion);
            World.getWorld().registerNpc(minion);
        }

        vetion.putAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED, true);
        vetion.putAttrib(AttributeKey.MINION_LIST, minions);
    }

    public boolean customOnDeath(Entity entity) {
        if (entity.isNpc()) {
            NPC purpleVetion = (NPC) entity;
            if ((purpleVetion.hp() == 0 || purpleVetion.dead()) && !purpleVetion.<Boolean>getAttribOr(AttributeKey.VETION_REBORN_ACTIVE, false)) {
                purpleVetion.heal(255); // Heal vetion
                purpleVetion.transmog(6612); //Transform into orange vetion
                purpleVetion.setTile(purpleVetion.tile());//Update tile
                purpleVetion.forceChat("Do it again!!");
                purpleVetion.getTimers().register(TimerKey.VETION_REBORN_TIMER, 500);
                purpleVetion.putAttrib(AttributeKey.VETION_REBORN_ACTIVE, true);
                return purpleVetion.transmog() != 6612;
            }
        }
        return false;
    }
}
