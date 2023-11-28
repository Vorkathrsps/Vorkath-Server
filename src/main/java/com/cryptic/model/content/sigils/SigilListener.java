package com.cryptic.model.content.sigils;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.player.Player;

public interface SigilListener {
    void prepare(Player player, Entity target);
    void sigilAccuracyBonus(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy);
}
