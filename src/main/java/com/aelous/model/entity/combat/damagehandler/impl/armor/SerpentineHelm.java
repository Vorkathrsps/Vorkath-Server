package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;

public class SerpentineHelm implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var attacker = (Player) hit.getAttacker();
        var defender = (NPC) hit.getTarget();
        if (defender != null && defender.id() == NpcIdentifiers.NYLOCAS_ATHANATOS) {
            if (FormulaUtils.isWearingPoisonEquipmentOrWeapon(attacker)) {
                var hp = defender.maxHp();
                hit.setDamage(hp);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
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
