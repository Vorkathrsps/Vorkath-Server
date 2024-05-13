package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.World;
import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.EquipmentBonuses;
import com.cryptic.utility.Utils;

public class FormidableFighter extends AbstractSigil {

    @Override
    public int modifyOffensiveEquipmentBonuses(Player player) {
        if (!attuned(player)) return 0;
        return 30;
    }

    @Override
    public void damageModification(Player player, Hit hit) {
        if (!attuned(player)) return;
        if (Utils.rollDie(20, 1)) {
            int damage = hit.getDamage();
            hit.setDamage(damage + 5);
        }
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.FORMIDABLE_FIGHTER);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType() != null && player.getCombat().getCombatType().equals(CombatType.MELEE);
    }
}
