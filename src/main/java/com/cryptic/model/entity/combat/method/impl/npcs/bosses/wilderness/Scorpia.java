package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

public class Scorpia extends CommonCombatMethod {

    @Override
    public void init(NPC npc) {
        npc.ignoreOccupiedTiles = true;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        //If Scorpia is below 50% HP & hasn't summoned offspring that heal we..
        var summoned_guardians = entity.<Boolean>getAttribOr(AttributeKey.SCORPIA_GUARDIANS_SPAWNED, false);
        if (entity.hp() < 100 && !summoned_guardians) {
            summon_guardian((NPC) entity);
            summon_guardian((NPC) entity);
            entity.putAttrib(AttributeKey.SCORPIA_GUARDIANS_SPAWNED, true);
        }

        if (withinDistance(1)) {
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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
