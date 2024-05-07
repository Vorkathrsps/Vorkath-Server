package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.concurrent.atomic.AtomicInteger;

public class MenacingMage extends AbstractSigil {
    @Override
    protected void onRemove(Player player) {

    }

    @Override
    protected void processMisc(Player player) {

    }

    @Override
    protected void processCombat(Player player, Entity target) {
        if (!attuned(player)) return;
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
        if (!activate(player)) {
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
        return 0;
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.MENACING_MAGE);
    }

    @Override
    protected boolean activate(Player player) {
        return player.hasAttrib(AttributeKey.MENACING_CURSE);
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return CombatType.MAGIC.equals(player.getCombat().getCombatType());
    }

}
