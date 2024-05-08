package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.player.Player;

public class Consistency extends AbstractSigil {
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
        if (!attuned(player)) return;
        if (hit.isImmune()) return;
        final HitMark previousMark = hit.getHitMark();
        final int previousDamage = hit.getDamage();
        hit.checkAccuracy(false).setHitMark(previousMark).setDamage(previousDamage + 1);
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
        return player.hasAttrib(AttributeKey.CONSISTENCY);
    }

    @Override
    protected boolean activate(Player player) {
        return false;
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.RANGED) || player.getCombat().getCombatType().equals(CombatType.MELEE) || player.getCombat().getCombatType().equals(CombatType.MAGIC);
    }
}
