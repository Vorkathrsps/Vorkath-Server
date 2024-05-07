package com.cryptic.model.content.sigils.misc;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public class Exaggeration extends AbstractSigil {
    @Override
    protected void onRemove(Player player) {
        player.clearAttrib(AttributeKey.EXAGGERATION_BOOST);
        player.getSkills().resetStats();
    }

    @Override
    protected void processMisc(Player player) {
        if (!attuned(player)) return;
        player.putAttrib(AttributeKey.EXAGGERATION_BOOST, true);
        for (int index = 0; index < 23; index++) {
            if (index > 6) player.getSkills().alterSkill(index, 5);
        }
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
    protected double accuracyModification(Player player, Entity target, AbstractAccuracy accuracy) {
        return 0;
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_EXAGGERATION);
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
