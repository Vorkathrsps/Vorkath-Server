package com.aelous.model.entity.combat.method.impl.npcs.karuulm;

import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.model.entity.combat.CombatFactory.MELEE_COMBAT;

/**
 * @author Patrick van Elderen | December, 22, 2020, 14:57
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DrakeCombatScript extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC npc = (NPC) entity;

        if (npc instanceof Drake) {
            Drake drake = (Drake) npc;
            drake.recordedAttacks--;

            if (drake.recordedAttacks == 0) {
                volcanicBreath(drake, target);
                drake.recordedAttacks = 7;
            } else {
                regularAttack(drake, target);
            }
        }
        return true;
    }

    /**
     * Sends the drakes's ranged or melee attack.
     */
    private void regularAttack(NPC drake, Entity target) {
        var meleeAttack = CombatFactory.canReach(drake, MELEE_COMBAT, target);

        if (meleeAttack && World.getWorld().rollDie(2,1)) {
            drake.animate(8275);
            target.hit(drake, CombatFactory.calcDamageFromType(drake, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
        } else {
            drake.animate(8276);
            new Projectile(drake, target,1636,40, 68, 25, 31,0,16,96).sendProjectile();
            target.hit(drake, CombatFactory.calcDamageFromType(drake, target, CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy().submit();
        }
    }

    /**
     * Sends the volcanic breath special attack.
     */
    private void volcanicBreath(NPC drake, Entity target) {
        drake.animate(8276);
        final var tile = target.tile().copy();
       // new Projectile(drake.getCentrePosition(), tile, 1,1637,125, 40, 25, 0,0,16,96).sendProjectile();
        Chain.bound(null).runFn(5, () -> {
            World.getWorld().tileGraphic(1638, tile, 0, 0);
            if (target.tile().equals(tile)) {
                for (int hits = 0; hits < 4; hits++) {
                    Chain.bound(null).name("drake_special_attack_task").runFn(hits, () -> {
                        target.hit(drake, World.getWorld().random(6, 8));
                    });
                }
            }
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }
}
