package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import lombok.Getter;

import java.util.List;

public class VirtusSet implements DamageModifyingListener {
    @Getter
    private final List<Integer> blood_spells = List.of(12901,
        12919,
        12911,
        12929);

    @Getter
    private final List<Integer> ice_spells = List.of(
        12861,
        12881,
        12871,
        12891);

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

            for (var s : this.getBlood_spells()) {
                if (player.getCombat().getCastSpell().spellId() == s) {
                    player.heal(hit.getDamage() / 4, (int) (hit.getDamage() * 0.20));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (combatType == null) return boost;
        if (!CombatType.MAGIC.equals(combatType)) return boost;
        if (entity instanceof Player player) {
            final Entity target = accuracy.defender();
            if (target == null) return boost;
            if (!target.frozen()) return boost;
            if (!FormulaUtils.wearingFullVirtus(player)) return boost;
            if (player.getCombat().getAutoCastSpell() == null && player.getCombat().getCastSpell() == null) return boost;
            for (var s : this.getIce_spells()) {
                if (player.getCombat().getCastSpell() != null && player.getCombat().getCastSpell().spellId() != s) continue;
                if (player.getCombat().getAutoCastSpell() != null && player.getCombat().getAutoCastSpell().spellId() != s) continue;
                boost = 1.010D;
                return boost;
            }
        }
        return boost;
    }
}
