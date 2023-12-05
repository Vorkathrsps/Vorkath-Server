package com.cryptic.model.content.sigils.io;

import com.cryptic.model.content.sigils.AbstractSigilHandler;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.concurrent.atomic.AtomicInteger;

public class MenacingMage extends AbstractSigilHandler {
    @Override
    protected void process(Player player, Entity target) {
        if (!attuned(player)) return;
        if (player.getCombat().getTarget() instanceof Player) return;
        if (player.getCombat().getCombatType() != CombatType.MAGIC) return;
        var damage = 2;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER -> damage = 3;
            case SAPPHIRE_MEMBER -> damage = 4;
            case EMERALD_MEMBER -> damage = 5;
            case DIAMOND_MEMBER -> damage = 6;
            case DRAGONSTONE_MEMBER -> damage = 7;
            case ONYX_MEMBER -> damage = 8;
            case ZENYTE_MEMBER -> damage = 9;
        }
        if (!activated(player)) {
            if (Utils.rollDie(20, 1)) {
                player.animate(9158);
                player.graphic(1977);
                player.putAttrib(AttributeKey.MENACING_CURSE, true);
                AtomicInteger count = new AtomicInteger(6);
                int d = damage;
                Chain.noCtx().repeatingTask(1, curse -> {
                    count.getAndDecrement();
                    player.submitHit(target, 0, d, HitMark.CORRUPTION);
                    if (count.get() == 0) {
                        player.clearAttrib(AttributeKey.MENACING_CURSE);
                        curse.stop();
                    }
                });
            }
        }
    }

    @Override
    protected void applyBoost(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy) {
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.MENACING_MAGE);
    }

    @Override
    protected boolean activated(Player player) {
        return player.hasAttrib(AttributeKey.MENACING_CURSE);
    }

}
