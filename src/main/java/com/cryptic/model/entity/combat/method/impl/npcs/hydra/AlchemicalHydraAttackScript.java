package com.cryptic.model.entity.combat.method.impl.npcs.hydra;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;

/**
 * @author Patrick van Elderen | March, 20, 2021, 18:41
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class AlchemicalHydraAttackScript extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC mobAsNpc = (NPC) entity;

        if (mobAsNpc instanceof AlchemicalHydra npc) {

            npc.recordedAttacks--;
            var nextAttackType = npc.getNextAttack();

            if (nextAttackType == HydraAttacks.RANGED || nextAttackType == HydraAttacks.MAGIC) {
                npc.currentAttack = nextAttackType;
            }

            nextAttackType.executeAttack(npc, target.getAsPlayer());
            npc.getCombat().delayAttack(5);
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 5;
    }
}
