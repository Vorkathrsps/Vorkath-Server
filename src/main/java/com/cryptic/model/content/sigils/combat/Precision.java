package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.player.Player;

public class Precision extends AbstractSigil {

    @Override
    public double accuracyModification(Player player, Entity target, AbstractAccuracy accuracy) {
        if (!attuned(player)) return 0.0D;
        return 1.50D;
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.PRECISION);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType() != null && player.getCombat().getCombatType().equals(CombatType.MELEE) || player.getCombat().getCombatType().equals(CombatType.MAGIC) || player.getCombat().getCombatType().equals(CombatType.RANGED);
    }
}
