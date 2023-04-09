package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.ItemIdentifiers;

public class AberrantSpectre extends CommonCombatMethod {

    private static final int[] DRAIN = { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC};

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        new Projectile(entity, target, 336, 5, 45, 37, 38, 0,16, 0).sendProjectile();

        Player player = (Player) target;

        if(!player.getEquipment().contains(ItemIdentifiers.NOSE_PEG) && !player.getEquipment().wearingSlayerHelm()) {
            player.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC) + 3, CombatType.MAGIC).submit();
            for (int skill : DRAIN) {
                player.getSkills().alterSkill(skill, -6);
            }
            player.message("<col=ff0000>The aberrant spectre's stench disorients you!");
            player.message("<col=ff0000>A nose peg can protect you from this attack.");
        } else {
            player.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), CombatType.MAGIC).checkAccuracy().submit();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
