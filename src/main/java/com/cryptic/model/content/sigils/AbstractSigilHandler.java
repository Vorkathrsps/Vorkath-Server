package com.cryptic.model.content.sigils;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public abstract class AbstractSigilHandler {
    protected abstract void process(Player player, Entity target);
    protected abstract void handleDamageModification(Player player, Hit hit);
    protected abstract void applyBoost(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy);
    protected abstract boolean attuned(Player player);
    protected abstract boolean activated(Player player);
    protected abstract boolean validateCombatType(Player player);
}
