package com.cryptic.model.content.sigils;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public abstract class AbstractSigil {
    protected abstract void onRemove(Player player);
    protected abstract void processMisc(Player player);
    protected abstract void processCombat(Player player, Entity target);
    protected abstract void damageModification(Player player, Hit hit);
    protected abstract void skillModification(Player player);
    protected abstract void resistanceModification(Entity attacker, Entity target, Hit entity);
    protected abstract double accuracyModification(Player player, Entity target, AbstractAccuracy accuracy);
    protected abstract boolean attuned(Player player);
    protected abstract boolean activate(Player player);
    protected abstract boolean validateCombatType(Player player);
}
