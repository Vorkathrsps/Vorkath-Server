package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

public class WildernessWeapon implements DamageEffectListener {
    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.MAGIC) {
                    if (FormulaUtils.hasMagicWildernessWeapon(player)) {
                        var modifier = accuracy.modifier();
                        if (WildernessArea.inWilderness(npc.tile())) {
                            modifier += 1.50F;
                            return modifier;
                        }
                    }
                }
            }
        }
        return 0;
    }
}
