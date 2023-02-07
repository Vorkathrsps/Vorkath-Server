package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.util.ArrayList;
import java.util.List;

public class Vetion extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        if (entity.hp() <= 125 && !entity.hasAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED)) {
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
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 6;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }

    private void doMagic() {
        entity.animate(entity.attackAnimation());
        Tile lightning_one = target.tile();
        Tile lightning_two = lightning_one.transform(1, 0);
        Tile lightning_three = lightning_one.transform(1, 1);
        int tileDist = entity.tile().distance(target.tile());
        int delay = Math.max(1, (30 + tileDist * 12) / 30);

        Chain.bound(null).runFn(2, () -> {
            new Projectile(new Tile(entity.tile().x + -1, entity.tile().y + 1), lightning_one, 0, 280, 10 * tileDist, delay, 70, 45, 0).sendProjectile();
            new Projectile(new Tile(entity.tile().x + -1, entity.tile().y + 1), lightning_two, 0, 280, 10 * tileDist, delay, 70, 45, 0).sendProjectile();
            new Projectile(new Tile(entity.tile().x + -1, entity.tile().y + 1), lightning_three, 0, 280, 10 * tileDist, delay, 70, 45, 0).sendProjectile();

            World.getWorld().tileGraphic(281, lightning_one,0, 10 * tileDist);
            World.getWorld().tileGraphic(281, lightning_one, 0, 10 * tileDist);
            World.getWorld().tileGraphic(281, lightning_two, 0, 10 * tileDist);
            World.getWorld().tileGraphic(281, lightning_three, 0, 10 * tileDist);
        }).then(3, () -> {
            if (target.tile() == (lightning_one) || target.tile() == (lightning_one.transform(1, 0)) || target.tile() == (lightning_one.transform(1, 1))) {
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), CombatType.MAGIC).checkAccuracy().submit();
            }
        });
        entity.getCombat().delayAttack(6);
    }

    private void spawnHellhounds(NPC vetion, Entity target) {
        vetion.forceChat("Kill my pets!");
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
