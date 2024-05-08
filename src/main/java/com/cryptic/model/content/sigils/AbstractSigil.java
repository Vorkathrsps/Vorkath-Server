package com.cryptic.model.content.sigils;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public abstract class AbstractSigil {
    public void onRemove(Player player) {

    }
    public void processMisc(Player player) {

    }
    public void processCombat(Player player, Entity target) {

    }
    public void damageModification(Player player, Hit hit) {

    }

    public void resistanceModification(Entity attacker, Entity target, Hit entity) {

    }
    public double accuracyModification(Player player, Entity target, AbstractAccuracy accuracy) {
        return 0D;
    }
    public boolean attuned(Player player) {
        return false;
    }
    public boolean activate(Player player) {
        return false;
    }
    public boolean validateCombatType(Player player) {
        return false;
    }
}
