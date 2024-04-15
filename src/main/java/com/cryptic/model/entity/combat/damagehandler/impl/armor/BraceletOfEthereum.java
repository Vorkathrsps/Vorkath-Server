package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import org.apache.commons.lang.ArrayUtils;

import static com.cryptic.utility.ItemIdentifiers.BRACELET_OF_ETHEREUM;

public class BraceletOfEthereum implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof NPC npc) {
            var target = npc.getCombat().getTarget();
            if (target instanceof Player player) {
                if (player.getEquipment().hasAt(EquipSlot.HANDS, BRACELET_OF_ETHEREUM)) {
                    if (ArrayUtils.contains(FormulaUtils.isRevenant(), npc.id())) {
                        if (hit.isAccurate()) {
                            int damage = hit.getDamage();
                            damage = ((damage * 25) / 100);
                            hit.setDamage(damage);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
