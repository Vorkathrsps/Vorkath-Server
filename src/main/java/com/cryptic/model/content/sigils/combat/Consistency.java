package com.cryptic.model.content.sigils.combat;

import com.cryptic.model.content.sigils.AbstractSigil;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.player.Player;

public class Consistency extends AbstractSigil {

    @Override
    public void damageModification(Player player, Hit hit) {
        final Entity target = hit.getTarget();
        if (!attuned(player)) return;
        if (hit.isImmune()) return;
        if (target == null) return;
        if (target instanceof Player) return;
        final HitMark previousMark = hit.getHitMark();
        final int previousDamage = hit.getDamage();
        hit.setDamage(previousDamage + 1, previousMark);
    }

    @Override
    public boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.CONSISTENCY);
    }

    @Override
    public boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType() != null && player.getCombat().getCombatType().equals(CombatType.RANGED) || player.getCombat().getCombatType().equals(CombatType.MELEE) || player.getCombat().getCombatType().equals(CombatType.MAGIC);
    }
}
