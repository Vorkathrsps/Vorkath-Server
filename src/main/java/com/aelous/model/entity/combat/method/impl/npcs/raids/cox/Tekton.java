package com.aelous.model.entity.combat.method.impl.npcs.raids.cox;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.TEKTON_7542;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.TEKTON_ENRAGED_7544;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 11, 2020
 */
public class Tekton extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if(entity.isNpc()) {
            NPC npc = entity.getAsNpc();

            //World boss
            if (npc.id() == TEKTON_7542) {
                npc.animate(npc.attackAnimation());
                //10% chance that the wold boss skulls you!
                if (World.getWorld().rollDie(10, 1)) {
                    Skulling.assignSkullState(((Player) target), SkullType.WHITE_SKULL);
                    target.message("The " + entity.getMobName() + " has skulled you, be careful!");
                }

                //entity.setPositionToFace(null); // Stop facing the target
                World.getWorld().getPlayers().forEach(p -> {
                    if (p != null && target.tile().inSqRadius(p.tile(), 12)) {
                        p.hit(entity, CombatFactory.calcDamageFromType(entity, p, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
                    }
                });

                entity.setPositionToFace(target.tile()); // Go back to facing the target.
                entity.getTimers().register(TimerKey.COMBAT_ATTACK, 5);
            } else if(npc.id() == TEKTON_ENRAGED_7544) {
                npc.getMovement().setBlockMovement(true); // Lock movement when we found a target
                doMeleePhaseInner(npc, target);
            }
        }
        return true;
    }

    private static boolean instanceFinished(Entity entity) {
        if (entity instanceof NPC) {
            NPC npc = (NPC) entity;
            if (npc.dead() || !npc.isRegistered()) {
                return true;
            }
        }
        return false;
    }

    private static void doMeleePhaseInner(NPC npc, Entity target) {
        npc.setEntityInteraction(null);
        npc.animate(7493);
        Tile p1 = target.tile().copy();
        npc.setPositionToFace(p1);
        Chain.bound(null).cancelWhen(() -> instanceFinished(npc)).runFn(1, () -> {
            npc.setPositionToFace(p1);
        }).then(4, () -> {
            if (p1.area(1).contains(target)) {
                target.hit(npc, Utils.random(41), 0, CombatType.MELEE).checkAccuracy().submit();
            }
        }).then(2, () -> {
            npc.setEntityInteraction(target);
        }).then(2, () -> {
            npc.setEntityInteraction(null);
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return entity.getAsNpc().id() == TEKTON_ENRAGED_7544 ? 1 : 7;
    }
}
