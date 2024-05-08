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
    public double accuracyModification(Player player, Entity target, AbstractAccuracy accuracy) { //TODO
        if (!attuned(player)) return 0;
        EquipmentBonuses attackerBonus = player.getBonuses().totalBonuses(player, World.getWorld().equipmentInfo());
        attackerBonus.stab += 30;
        attackerBonus.slash += 30;
        attackerBonus.crush += 30;
        return 0;
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_FORTIFICATION);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.MELEE);
    }
}
