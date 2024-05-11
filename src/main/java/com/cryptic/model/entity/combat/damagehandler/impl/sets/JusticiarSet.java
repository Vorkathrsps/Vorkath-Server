package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.items.container.equipment.EquipmentBonuses;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

public class JusticiarSet implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var damage = hit.getDamage();
        if (entity instanceof Player player) {
            if (!WildernessArea.inWilderness(player.tile())) {
                if (damage > 0 && Equipment.justiciarSet(player)) {
                    EquipmentBonuses attackerBonus = player.getBonuses().totalBonuses(player, World.getWorld().equipmentInfo());
                    final int bonus = attackerBonus.crushdef;
                    final int formula = bonus / 3000;
                    damage = damage - formula;
                    damage = damage - 1;
                    hit.setDamage(damage);
                    return true;
                }
            }
        }
        return false;
    }
}
