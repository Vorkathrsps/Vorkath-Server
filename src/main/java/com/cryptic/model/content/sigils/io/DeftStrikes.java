package com.cryptic.model.content.sigils.io;

import com.cryptic.model.content.sigils.AbstractSigilHandler;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

public class DeftStrikes extends AbstractSigilHandler {
    @Override
    protected void process(Player player, Entity target) {

    }

    @Override
    protected void applyBoost(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy) {
        if (!attuned(player)) return;
        if (!(target instanceof NPC)) return;
        if (player.getCombat().getCombatType().isRanged()) rangeAccuracy.modifier += 1.20;
        if (player.getCombat().getCombatType().isMagic()) magicAccuracy.modifier += 1.20;
        if (player.getCombat().getCombatType().isMelee()) meleeAccuracy.modifier += 1.20;
    }

    @Override
    protected boolean attuned(Player player) {
        return player.hasAttrib(AttributeKey.DEFT_STRIKES);
    }

    @Override
    protected boolean activated(Player player) {
        return false;
    }
}
