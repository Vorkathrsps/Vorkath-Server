package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public class Aggression extends AbstractSigil {
    @Override
    public void resistanceModification(Entity attacker, Entity target, Hit entity) {
        if (attacker instanceof NPC && target instanceof Player player) {
            if (!attuned(player)) return;
            int damage = entity.getDamage();
            entity.setDamage((int) (damage * 1.05D));
            System.out.println("aggression after: " + entity.getDamage());
        }
    }

    @Override
    public void damageModification(Player player, Hit hit) {
        if (!attuned(player)) return;
        int damage = hit.getDamage();
        hit.setDamage((int) (damage * 1.10D));
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_AGGRESSION);
    }

    @Override
    public boolean activate(Player player) {
        return player.hasAttrib(AttributeKey.AGRESSION_ACTIVE);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return !CombatType.TYPELESS.equals(player.getCombat().getCombatType());
    }

}
