package com.aelous.model.entity.combat.method.impl.npcs.bosses.superiorbosses;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness.ScorpiaGuardian;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

public class Skorpios extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        //If Scorpia is below 50% HP & hasn't summoned offspring that heal we..
        var summoned_guardians = entity.<Boolean>getAttribOr(AttributeKey.SCORPIA_GUARDIANS_SPAWNED, false);
        if (entity.hp() < 100 && !summoned_guardians) {
            summon_guardian((NPC) entity);
            summon_guardian((NPC) entity);
            entity.putAttrib(AttributeKey.SCORPIA_GUARDIANS_SPAWNED, true);
            return false;
        }

        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            if (World.getWorld().rollDie(4, 1)) {
                target.poison(20);
            }

            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
            entity.animate(entity.attackAnimation());
        }
        return true;
    }

    private void summon_guardian(NPC scorpia) {
        var guardian = new NPC(NpcIdentifiers.SCORPIAS_GUARDIAN, new Tile(scorpia.tile().x + World.getWorld().random(2), scorpia.tile().y + World.getWorld().random(2)));
        guardian.respawns(false);
        guardian.noRetaliation(true);
        World.getWorld().registerNpc(guardian);
        guardian.setEntityInteraction(scorpia);

        // Execute script
        ScorpiaGuardian.heal(scorpia, guardian);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}
