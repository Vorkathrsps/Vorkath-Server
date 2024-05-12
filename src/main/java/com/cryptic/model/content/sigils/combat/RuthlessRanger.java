package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

public class RuthlessRanger extends AbstractSigil {

    @Override
    public void processCombat(Player player, Entity target) {
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
        if (target == null) return;
        if (target instanceof Player) return;
        if (player.getCombat() == null) return;
        if (player.getCombat().getCombatType() == null) return;
        if (!activate(player) && CombatType.RANGED.equals(player.getCombat().getCombatType())) {
            if (Utils.rollDie(10, 1)) {
                player.animate(9158);
                player.graphic(1981);
                player.putAttrib(AttributeKey.RUTHLESS_CRIPPLE, true);
                AtomicInteger count = new AtomicInteger(6);
                final int d = damage;
                Chain.noCtx().repeatingTask(1, cripple -> {
                    if (target.dead() || !target.isRegistered()) {
                        player.clearAttrib(AttributeKey.RUTHLESS_CRIPPLE);
                        cripple.stop();
                        return;
                    }
                    count.getAndDecrement();
                    new Hit(player, target, 0, CombatType.TYPELESS).checkAccuracy(false).setDamage(d).setHitMark(HitMark.CORRUPTION).submit();
                    if (count.get() == 0) {
                        player.clearAttrib(AttributeKey.RUTHLESS_CRIPPLE);
                        cripple.stop();
                    }
                });

            }
        }
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.RUTHLESS_RANGER);
    }

    @Override
    public boolean activate(Player player) {
        return player.hasAttrib(AttributeKey.RUTHLESS_CRIPPLE);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType() != null && player.getCombat().getCombatType().equals(CombatType.RANGED);
    }

}
