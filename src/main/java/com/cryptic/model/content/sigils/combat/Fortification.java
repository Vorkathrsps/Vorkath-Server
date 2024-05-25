package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.World;
import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.EquipmentBonuses;

public class Fortification extends AbstractSigil {

    @Override
    public int modifyDefensiveEquipmentBonuses(Player player) {
        if (!attuned(player)) return 0;
        return 50;
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_FORTIFICATION);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType() != null && player.getCombat().getCombatType().equals(CombatType.MELEE);
    }
}
