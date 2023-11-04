package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.ItemIdentifiers;

/**
 * @author PVE
 * @Since augustus 06, 2020
 */
public class Banshee extends CommonCombatMethod {

    private static final int[] DRAIN = { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC, Skills.PRAYER, Skills.AGILITY};

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());

        Player player = (Player) target;

        if(!player.getEquipment().contains(ItemIdentifiers.EARMUFFS) && !player.getEquipment().wearingSlayerHelm()) {
            player.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE) + 6, CombatType.MELEE).submit();
            for (int skill : DRAIN) {
                player.getSkills().alterSkill(skill, -5);
            }
            player.message("The banshee's deafening scream drains your stats!");
        } else {
            player.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
        }
        return true;
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
