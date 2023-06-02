package com.aelous.model.entity.combat.damagehandler.impl.sets;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.equipment.Equipment;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;

public class JusticiarSet implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var damage = hit.getDamage();
        if (entity instanceof Player player) {
            if (!WildernessArea.inWilderness(player.tile())) {
                if (damage > 0 && Equipment.justiciarSet(player)) {
                    EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
                    int bonus = attackerBonus.crushdef;
                    int formula = bonus / 3000;
                    damage = damage - formula;
                    damage = damage - 1;
                    hit.setDamage(damage);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
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
