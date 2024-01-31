package com.cryptic.model.content.sigils.io;

import com.cryptic.model.content.sigils.AbstractSigilHandler;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

public class FeralFighter extends AbstractSigilHandler {
    @Override
    protected void process(Player player, Entity target) {
        if (!attuned(player)) return;
        var delay = 12;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER -> delay = 13;
            case SAPPHIRE_MEMBER -> delay = 14;
            case EMERALD_MEMBER -> delay = 15;
            case DIAMOND_MEMBER -> delay = 16;
            case DRAGONSTONE_MEMBER -> delay = 17;
            case ONYX_MEMBER -> delay = 18;
            case ZENYTE_MEMBER -> delay = 19;
        }
        if (!activated(player)) {
            if (Utils.rollDie(20, 1)) {
                player.animate(9158);
                player.graphic(1980);
                player.putAttrib(AttributeKey.FERAL_FIGHTER_ATTACKS_SPEED, player.getBaseAttackSpeed() - 1.2);
                Chain.noCtx().runFn(delay, () -> player.clearAttrib(AttributeKey.FERAL_FIGHTER_ATTACKS_SPEED));
            }
        }
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
        return player.hasAttrib(AttributeKey.FERAL_FIGHTER);
    }

    @Override
    protected boolean activated(Player player) {
        return player.hasAttrib(AttributeKey.FERAL_FIGHTER_ATTACKS_SPEED);
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.MELEE);
    }

}
