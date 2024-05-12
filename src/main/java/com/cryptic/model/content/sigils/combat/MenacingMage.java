package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.World;
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

public class MenacingMage extends AbstractSigil {

    @Override
    public void processCombat(Player player, Entity target) {
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
        if (target == null) return;
        if (target instanceof Player) return;
        if (player.getCombat() == null) return;
        if (player.getCombat().getCombatType() == null) return;
        if (!activate(player) && CombatType.MAGIC.equals(player.getCombat().getCombatType())) {
            //if (Utils.rollDie(10, 1)) {
                player.animate(9158);
                player.graphic(1977);
                player.putAttrib(AttributeKey.MENACING_CURSE, true);
                AtomicInteger count = new AtomicInteger(6);
                final int d = damage;
                Chain.noCtx().repeatingTask(1, curse -> {
                    count.getAndDecrement();
                    new Hit(player, target, 0, CombatType.TYPELESS).checkAccuracy(false).setDamage(d).setHitMark(HitMark.CORRUPTION).submit();
                    if (count.get() == 0) {
                        player.clearAttrib(AttributeKey.MENACING_CURSE);
                        curse.stop();
                    }
                });
            }
        //}
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.MENACING_MAGE);
    }

    @Override
    public boolean activate(Player player) {
        return player.hasAttrib(AttributeKey.MENACING_CURSE);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType() != null && CombatType.MAGIC.equals(player.getCombat().getCombatType());
    }

}
