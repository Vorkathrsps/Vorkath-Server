package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

/**
 * @author PVE
 * @Since augustus 06, 2020
 */
public class DustDevil extends CommonCombatMethod {

    private static final int[] DRAIN = { Skills.ATTACK, Skills.STRENGTH, Skills.RANGED, Skills.MAGIC};

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        Player player = (Player) target;
        if ((player.getEquipment().getId(EquipSlot.HEAD) != ItemIdentifiers.FACEMASK && !player.getEquipment().wearingSlayerHelm())) {
            player.hit(entity, 16, CombatType.MELEE).submit();
            player.message("<col=ff0000>The devil's dust blinds and damages you!");
            player.message("<col=ff0000>A facemask can protect you from this attack.");
        }
        basicAttack(entity, target);
        if(Utils.rollDie(5, 1)) {
            for (int skill : DRAIN) {
                player.getSkills().alterSkill(skill, -5);
            }
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
