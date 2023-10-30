package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import com.cryptic.utility.ItemIdentifiers;

import static com.cryptic.utility.ItemIdentifiers.TUMEKENS_SHADOW;

public class TumekensShadow implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        int bonus;
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
            if (target instanceof NPC) {
                if (combatType == CombatType.MAGIC) {
                    if (equipment.contains(TUMEKENS_SHADOW)) {
                        bonus = attackerBonus.mage += Math.min(attackerBonus.mage * 3, attackerBonus.mage * attackerBonus.mage);
                        magicAccuracy.modifier += bonus;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        return false;
    }
}
