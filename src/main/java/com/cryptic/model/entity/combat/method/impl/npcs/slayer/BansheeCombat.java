package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.ItemIdentifiers;

/**
 * @author PVE
 * @Since augustus 06, 2020
 */
public class BansheeCombat extends CommonCombatMethod {

    private static final int[] DRAIN = { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC, Skills.PRAYER, Skills.AGILITY};

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!isReachable()) return true;
        entity.animate(entity.attackAnimation());

        Player player = (Player) target;

        if(!player.getEquipment().contains(ItemIdentifiers.EARMUFFS) && !FormulaUtils.hasSlayerHelmet(player) && !FormulaUtils.hasSlayerHelmetImbued(player)) {
            new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
            for (int skill : DRAIN) {
                player.getSkills().alterSkill(skill, -5);
            }
            player.message("The banshee's deafening scream drains your stats!");
        } else {
            new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
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
