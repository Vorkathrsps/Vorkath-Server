package com.cryptic.model.content.sigils;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

public interface SigilListener {
    void processResistance(Entity attacker, Entity target, Hit hit);
    void processDamage(Player player, Hit hit);
    void process(Player player, Entity target);
    double processAccuracy(Player player, Entity target, AbstractAccuracy accuracy);
    void HandleLogin(Player player);

}
