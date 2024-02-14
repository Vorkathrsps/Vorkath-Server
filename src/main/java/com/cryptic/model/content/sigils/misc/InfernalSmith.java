package com.cryptic.model.content.sigils.misc;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public class InfernalSmith extends AbstractSigil {
    @Override
    protected void onRemove(Player player) {
        player.clearAttrib(AttributeKey.INFERNAL_SMITH);
    }

    @Override
    protected void processMisc(Player player) {
        if (!attuned(player)) return;
        player.putAttrib(AttributeKey.INFERNAL_SMITH, true);
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
    protected void resistanceModification(Entity attacker, Entity target, Hit entity) {

    }

    @Override
    protected void accuracyModification(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy) {

    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_INFERNAL_SMITH);
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
