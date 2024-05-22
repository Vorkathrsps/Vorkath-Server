package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class BlisterwoodFlail implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            if (combatType == null) return false;
            if (!player.getEquipment().containsAny(ItemIdentifiers.BLISTERWOOD_FLAIL, ItemIdentifiers.BLISTERWOOD_SICKLE))
                return false;
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (ArrayUtils.contains(FormulaUtils.vampyres, npc.id())) {
                    System.out.println("applying damage");
                    int damage = hit.getDamage();
                    hit.setDamage((int) (damage * 1.25D));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            if (!player.getEquipment().containsAny(ItemIdentifiers.BLISTERWOOD_FLAIL, ItemIdentifiers.BLISTERWOOD_SICKLE)) return boost;
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (ArrayUtils.contains(FormulaUtils.vampyres, npc.id())) {
                    System.out.println("applying accuracy");
                    boost = 1.05D;
                    return boost;
                }
            }
        }
        return boost;
    }
}
