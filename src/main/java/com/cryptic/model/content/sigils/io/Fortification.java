package com.cryptic.model.content.sigils.io;

import com.cryptic.model.World;
import com.cryptic.model.content.sigils.AbstractSigilHandler;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public class Fortification extends AbstractSigilHandler {
    @Override
    protected void process(Player player, Entity target) {

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
        if (!attuned(player)) return;
        if (player.getCombat().getCombatType().isRanged()) rangeAccuracy.modifier += 1.50;
        else if (player.getCombat().getCombatType().isMagic()) magicAccuracy.modifier += 1.50;
        else if (player.getCombat().getCombatType().isMelee()) meleeAccuracy.modifier += 1.50;
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.SIGIL_OF_FORTIFICATION);
    }

    @Override
    protected boolean activated(Player player) {
        return false;
    }

    @Override
    protected boolean validateCombatType(Player player) {
        return player.getCombat().getCombatType().equals(CombatType.MELEE) || player.getCombat().getCombatType().equals(CombatType.MAGIC) || player.getCombat().getCombatType().equals(CombatType.RANGED);
    }
}
