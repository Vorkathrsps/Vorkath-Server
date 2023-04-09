package com.aelous.model.entity.combat.method.impl.npcs.hydra;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;

/**
 * @author Patrick van Elderen | March, 20, 2021, 18:41
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class AlchemicalHydraAttackScript extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC mobAsNpc = (NPC) entity;

        if (mobAsNpc instanceof AlchemicalHydra) {
            AlchemicalHydra npc = (AlchemicalHydra) mobAsNpc;

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
    public int getAttackDistance(Entity entity) {
        return 5;
    }
}
