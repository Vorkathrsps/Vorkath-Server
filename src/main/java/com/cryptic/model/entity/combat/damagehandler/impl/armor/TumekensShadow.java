package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.EquipmentBonuses;

import static com.cryptic.utility.ItemIdentifiers.CORRUPTED_TUMEKENS_SHADOW;
import static com.cryptic.utility.ItemIdentifiers.TUMEKENS_SHADOW;

public class TumekensShadow implements DamageModifyingListener {

    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        int bonus = 0;
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            EquipmentBonuses attackerBonus = player.getBonuses().totalBonuses(player, World.getWorld().equipmentInfo());
            if (target instanceof NPC) {
                if (combatType == CombatType.MAGIC) {
                    var modifier = accuracy.modifier();
                    if (equipment.containsAny(TUMEKENS_SHADOW, CORRUPTED_TUMEKENS_SHADOW)) {
                        if (player.getCombat().getCastSpell() != null && player.getCombat().getCastSpell().spellId() != 6) return 0;
                        bonus = attackerBonus.mage * 3;
                        bonus = Math.min(bonus, 100);
                        modifier += bonus;
                        return modifier;
                    }

                }
            }
        }
        return 0;
    }
}
