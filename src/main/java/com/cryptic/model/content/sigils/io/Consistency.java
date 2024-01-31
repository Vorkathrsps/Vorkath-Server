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

public class Consistency extends AbstractSigilHandler {
    @Override
    protected void process(Player player, Entity target) {

    }

    @Override
    protected void damageModification(Player player, Hit hit) {
        if (!attuned(player)) return;
        hit.postDamage(h -> {
            if (h.isImmune()) return;
            int damage = h.getDamage() + 1;
            h.setAccurate(true);
            h.setDamage(damage);
        });
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
        return player.hasAttrib(AttributeKey.CONSISTENCY);
    }

    @Override
    protected boolean activated(Player player) {
        return false;
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.RANGED) || player.getCombat().getCombatType().equals(CombatType.MELEE) || player.getCombat().getCombatType().equals(CombatType.MAGIC);
    }
}
