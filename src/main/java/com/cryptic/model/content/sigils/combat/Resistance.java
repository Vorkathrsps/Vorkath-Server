package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public class Resistance extends AbstractSigil {
    @Override
    public void resistanceModification(Entity attacker, Entity target, Hit hit) {
        if (!(attacker instanceof NPC)) return;
        if (target instanceof Player player) {
            if (!attuned(player)) return;
            if (player.hasAttrib(AttributeKey.TITANIUM)) return;
            int damage = hit.getDamage();
            var reduced_value = damage - (damage * 0.25);
            hit.setDamage((int) reduced_value);
        }
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.RESISTANCE);
    }

}
