package com.cryptic.model.content.sigils;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public interface SigilListener {
    void processResistance(final Entity attacker, final Entity target, final Hit hit);
    void processDamage(final Player player, final Hit hit);
    void process(final Player player, final Entity target);
    double processAccuracy(final Player player, final Entity target, final AbstractAccuracy accuracy);
    void HandleLogin(final Player player);
    int processOffensiveEquipmentModification(final Player player);
    int processDefensiveEquipmentModification(final Player player);

}
