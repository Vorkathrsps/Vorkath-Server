package com.aelous.model.content.seasonal_events.halloween.boss;

import com.aelous.core.task.Task;
import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.FEAR_REAPER;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 15, 2021
 */
public class GrimCombatScript extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if(World.getWorld().rollDie(10,1)) {
            spawnMinions((NPC) entity);
        }

        if(World.getWorld().rollDie(5,1)) {
            fallingCandy((NPC) entity, target);
        }

        meleeAttack((NPC) entity, target);
        return true;
    }

    private void spawnMinions(NPC mob) {
        NPC fearRepearOne = new NPC(FEAR_REAPER, mob.tile().transform(-2,0));
        NPC fearRepearTwo = new NPC(FEAR_REAPER, mob.tile().transform(+2,0));
        Task.runOnceTask(2, t -> {
            Chain.runGlobal(1, () -> {
                World.getWorld().registerNpc(fearRepearOne);
                World.getWorld().registerNpc(fearRepearTwo);

                World.getWorld().getPlayers().forEach(p -> {
                    if (p != null && mob.tile().inSqRadius(p.tile(), 12)) {
                        fearRepearOne.setPositionToFace(p.tile());
                        fearRepearTwo.setPositionToFace(p.tile());
                        new Projectile(fearRepearOne, p, 1403, 45, 140, 50, 33, 0).sendProjectile();
                        new Projectile(fearRepearTwo, p, 1404, 45, 120, 50, 33, 0).sendProjectile();
                        target.hit(fearRepearOne, CombatFactory.calcDamageFromType(fearRepearOne, p, CombatType.MAGIC), 3, CombatType.MAGIC).checkAccuracy().submit();
                        target.hit(fearRepearTwo, CombatFactory.calcDamageFromType(fearRepearTwo, p, CombatType.RANGED), 4, CombatType.RANGED).checkAccuracy().submit();
                    }
                });
            }).then(5, () -> {
                World.getWorld().unregisterNpc(fearRepearOne);
                World.getWorld().unregisterNpc(fearRepearTwo);
            });
        });
    }

    private void fallingCandy(Entity entity, Entity target) {
        World.getWorld().getPlayers().forEach(p -> {
            if (p != null && target.tile().inSqRadius(p.tile(), 12)) {
                final var tile = p.tile().copy();

                new Projectile(entity.tile().transform(1, 1,0), tile, 1, 1671, 165, 30, 200, 6, 0).sendProjectile();

                Chain.bound(null).runFn(6, () -> {
                    World.getWorld().tileGraphic(1765, tile, 5, 0);
                    if (p.tile().equals(tile)) {
                        p.hit(entity, World.getWorld().random(1, 10));
                    }
                });
            }
        });
    }

    private void meleeAttack(NPC npc, Entity entity) {
        npc.animate(8056);
        npc.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> {
            if (p != null && target.tile().inSqRadius(p.tile(), 12)) {
                p.hit(npc, CombatFactory.calcDamageFromType(npc, p, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
            }
        });
        npc.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
