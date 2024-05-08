package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.World;
import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

public class FeralFighter extends AbstractSigil {
    @Override
    public void processCombat(Player player, Entity target) {
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
        if (!activate(player)) {
            final int random = World.getWorld().random(100);
            if (random < 20) {
                player.animate(9158);
                player.graphic(1980);
                player.putAttrib(AttributeKey.FERAL_FIGHTER_ATTACKS_SPEED, player.getBaseAttackSpeed() - 1.2);
                Chain.noCtx().runFn(delay, () -> player.clearAttrib(AttributeKey.FERAL_FIGHTER_ATTACKS_SPEED));
            }
        }
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.FERAL_FIGHTER);
    }

    @Override
    public boolean activate(Player player) {
        return player.hasAttrib(AttributeKey.FERAL_FIGHTER_ATTACKS_SPEED);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.MELEE);
    }

}
