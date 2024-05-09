package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.DRAGON_MACE_BH;

public class DragonMace implements DamageModifyingListener {

    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        var attacker = (Player) entity;
        if ((attacker.getEquipment().contains(DRAGON_MACE_BH) && attacker.isSpecialActivated())) {
            //meleeAccuracy.getGearDefenceBonus(true);
            return 0;
        }
        return 0;
    }
}
