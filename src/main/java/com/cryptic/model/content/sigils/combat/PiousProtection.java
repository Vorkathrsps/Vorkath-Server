package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public class PiousProtection extends AbstractSigil {
    @Override
    public void resistanceModification(Entity attacker, Entity target, Hit entity) {
        if (attacker instanceof NPC && target instanceof Player player) {
            if (!attuned(player)) return;
            final CombatType combatType = entity.getCombatType();
            int damage = entity.getDamage();
            if (CombatType.MELEE.equals(combatType)) {
                if (!Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MELEE)) {
                    entity.setDamage((int) (damage * 1.05D));
                }
                entity.setDamage((int) (damage * 0.75D));
            }
            if (CombatType.MAGIC.equals(combatType)) {
                if (!Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC)) {
                    entity.setDamage((int) (damage * 1.05D));
                }
                entity.setDamage((int) (damage * 0.75D));
            }
            if (CombatType.RANGED.equals(combatType)) {
                if (!Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MISSILES)) {
                    entity.setDamage((int) (damage * 1.05D));
                }
                entity.setDamage((int) (damage * 0.75D));
            }
        }
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_PIOUS_PROTECTION);
    }

    @Override
    public boolean activate(Player player) {
        return player.hasAttrib(AttributeKey.PIOUS_PROTECTION_ACTIVE);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return true;
    }
}
