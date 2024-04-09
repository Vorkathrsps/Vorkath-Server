package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

/**
 * @author PVE
 * @Since augustus 06, 2020
 */
public class DustDevilCombat extends CommonCombatMethod {

    private static final int[] DRAIN = {Skills.ATTACK, Skills.STRENGTH, Skills.RANGED, Skills.MAGIC};

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        Player player = (Player) target;
        if ((!player.getEquipment().contains(ItemIdentifiers.FACEMASK) || !FormulaUtils.hasSlayerHelmet(player) || !FormulaUtils.hasSlayerHelmetImbued(player))) {
            new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
            player.message("<col=ff0000>The devil's dust blinds and damages you!");
            player.message("<col=ff0000>A facemask can protect you from this attack.");
            for (int skill : DRAIN) player.getSkills().alterSkill(skill, -5);
        } else {
            basicAttack(entity, target);
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
