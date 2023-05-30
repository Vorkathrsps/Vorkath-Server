package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.player.Player;

public class VirtusEquipment implements DamageEffectListener {

    private final int[] blood_spells = new int[]{
        12901,
        12919,
        12911,
        12929
    };

    private final int[] ice_spells = new int[]{
        12861,
        12881,
        12871,
        12891
    };

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (combatType != null) {
            if (!(entity instanceof Player player) || !combatType.isMagic() || !hit.isAccurate()) {
                return false;
            }
            if (!FormulaUtils.wearingFullVirtus(player)) {
                return false;
            }
            if (player.getCombat().getCastSpell() == null) {
                return false;
            }

            for (var s : blood_spells) {
                if (player.getCombat().getCastSpell().spellId() == s) {
                    player.heal(hit.getDamage() / 4, (int) (hit.getDamage() * 0.20));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        final Entity target = magicAccuracy.getDefender();
        boolean isFrozen = target.frozen();
        if (entity instanceof Player player) {
            if (player.getCombat().getCastSpell() != null) {
                if (FormulaUtils.wearingFullVirtus(player)) {
                    if (isFrozen) {
                        for (var s : ice_spells) {
                            if (player.getCombat().getCastSpell().spellId() == s) {
                                magicAccuracy.setModifier(0.010F);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        return false;
    }
}
