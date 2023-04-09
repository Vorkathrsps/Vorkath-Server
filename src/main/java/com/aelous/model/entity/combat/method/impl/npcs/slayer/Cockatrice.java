package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.ItemIdentifiers;

/**
 * @author PVE
 * @Since augustus 06, 2020
 */
public class Cockatrice extends CommonCombatMethod {

    private static final int[] DRAIN = {Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC};

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());

        Player player = (Player) target;
        basicAttack(entity, target);
        if (!player.getEquipment().contains(ItemIdentifiers.MIRROR_SHIELD)) {
            for (int skill : DRAIN) {
                player.getSkills().alterSkill(skill, -8);
            }
            player.message("<col=ff0000>The cockatrice's piercing gaze drains your stats!");
            player.message("<col=ff0000>A mirror shield can protect you from this attack.");
        }
        return true;
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
