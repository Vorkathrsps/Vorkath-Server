package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.player.Player;

public class MeticulousMage extends AbstractSigil {

    @Override
    public int modifyOffensiveEquipmentBonuses(Player player) {
        if (!attuned(player)) return 0;
        return 20;
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.METICULOUS_MAGE);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType() != null && player.getCombat().getCombatType().equals(CombatType.MAGIC);
    }
}
