package com.cryptic.model.entity.combat.damagehandler.impl.typeless;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.NYLOCAS_ATHANATOS;

public class PoisonDamageModifying implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (npc.id() == NYLOCAS_ATHANATOS) {
                    if (FormulaUtils.isWearingPoisonEquipmentOrWeapon(player)) {
                        var hp = npc.maxHp();
                        hit.setDamage(hp);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
