package com.cryptic.model.entity.combat.method.impl.npcs.hydra;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Area;

/**
 * @author Origin | March, 20, 2021, 18:41
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class AlchemicalHydraAttackScript extends CommonCombatMethod {
    public static final Area hydraArea = new Area(1356, 10257, 1377, 10278);

    @Override
    public void init(NPC npc) {
        npc.ignoreOccupiedTiles = true;
        npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 50);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC mobAsNpc = (NPC) entity;
        if (entity.getInstancedArea() != null) {
            if (!hydraArea.transformArea(0, 0, 0, 0, entity.getInstancedArea().getzLevel()).contains(target.tile()))
                return false;
        }
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
    public void doFollowLogic() {
        follow(3);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 4;
    }
}
