package com.cryptic.model.content.sigils.io;

import com.cryptic.model.content.sigils.AbstractSigilHandler;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.concurrent.atomic.AtomicInteger;

public class RuthlessRanger extends AbstractSigilHandler {
    @Override
    protected void process(Player player, Entity target) {
        if (!attuned(player)) return;
        var damage = 1;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER -> damage = 2;
            case SAPPHIRE_MEMBER -> damage = 3;
            case EMERALD_MEMBER -> damage = 4;
            case DIAMOND_MEMBER -> damage = 5;
            case DRAGONSTONE_MEMBER -> damage = 6;
            case ONYX_MEMBER -> damage = 7;
            case ZENYTE_MEMBER -> damage = 8;
        }
        if (!activated(player)) {
            if (Utils.rollDie(10, 1)) {
                player.animate(9158);
                player.graphic(1981);
                player.putAttrib(AttributeKey.RUTHLESS_CRIPPLE, true);
                AtomicInteger count = new AtomicInteger(6);
                int d = damage;
                Chain.noCtx().repeatingTask(1, cripple -> {
                    count.getAndDecrement();
                    player.submitHit(target, 0, d, HitMark.CORRUPTION);
                    if (count.get() == 0) {
                        player.clearAttrib(AttributeKey.RUTHLESS_CRIPPLE);
                        cripple.stop();
                    }
                });

            }
        }
    }

    @Override
    protected void handleDamageModification(Player player, Hit hit) {

    }

    @Override
    protected void applyBoost(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy) {

    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.RUTHLESS_RANGER);
    }

    @Override
    protected boolean activated(Player player) {
        return player.hasAttrib(AttributeKey.RUTHLESS_CRIPPLE);
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.RANGED);
    }

}
