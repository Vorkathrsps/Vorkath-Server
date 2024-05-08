package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.player.Player;

public class MeticulousMage extends AbstractSigil {

    @Override
    public double accuracyModification(Player player, Entity target, AbstractAccuracy accuracy) {
        if (!attuned(player)) return 0;
        var boost = 1.20;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER -> boost = 1.21;
            case SAPPHIRE_MEMBER -> boost = 1.22;
            case EMERALD_MEMBER -> boost = 1.23;
            case DIAMOND_MEMBER -> boost = 1.24;
            case DRAGONSTONE_MEMBER -> boost = 1.25;
            case ONYX_MEMBER -> boost = 1.26;
            case ZENYTE_MEMBER -> boost = 1.27;
        }
        return boost;
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.METICULOUS_MAGE);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.MAGIC);
    }
}
