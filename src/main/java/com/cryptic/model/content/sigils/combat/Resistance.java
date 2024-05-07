package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public class Resistance extends AbstractSigil {
    @Override
    protected void onRemove(Player player) {

    }

    @Override
    protected void processMisc(Player player) {

    }

    @Override
    protected void processCombat(Player player, Entity target) {

    }

    @Override
    protected void damageModification(Player player, Hit hit) {

    }

    @Override
    protected void skillModification(Player player) {

    }

    @Override
    protected void resistanceModification(Entity attacker, Entity target, Hit hit) {
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
    protected double accuracyModification(Player player, Entity target, AbstractAccuracy accuracy) {
        return 0;
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.RESISTANCE);
    }

    @Override
    protected boolean activate(Player player) {
        return false;
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return false;
    }
}
