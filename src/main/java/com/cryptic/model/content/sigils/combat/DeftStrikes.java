package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public class DeftStrikes extends AbstractSigil {
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
    protected void resistanceModification(Entity attacker, Entity target, Hit entity) {

    }

    @Override
    protected double accuracyModification(Player player, Entity target, AbstractAccuracy accuracy) {
        if (!attuned(player)) return 0D;
        var boost = 1.20D;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER -> boost = 1.21D;
            case SAPPHIRE_MEMBER -> boost = 1.22D;
            case EMERALD_MEMBER -> boost = 1.23D;
            case DIAMOND_MEMBER -> boost = 1.24D;
            case DRAGONSTONE_MEMBER -> boost = 1.25D;
            case ONYX_MEMBER -> boost = 1.26D;
            case ZENYTE_MEMBER -> boost = 1.27D;
        }
        return boost;
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.DEFT_STRIKES);
    }

    @Override
    protected boolean activate(Player player) {
        return false;
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return true;
    }
}
