package com.aelous.model.entity.combat.method.effects.equipment;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.equipment.impl.*;
import com.aelous.model.entity.combat.method.effects.equipment.impl.seteffects.GuthanSet;
import com.aelous.model.entity.combat.method.effects.equipment.impl.seteffects.VeracSet;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.combat.method.effects.typeless.PrayerDamage;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDamageEffect implements DamageEffectListener {
    private final List<DamageEffectListener> damageEffectListenersAttacker;
    private final List<DamageEffectListener> damageEffectListenersDefender;
    private final List<DamageEffectListener> accuracyModificationListenerAttacker;
    private final List<DamageEffectListener> accuracyModificationListenerDefender;

    public EquipmentDamageEffect() {
        damageEffectListenersAttacker = new ArrayList<>();
        damageEffectListenersDefender = new ArrayList<>();
        accuracyModificationListenerAttacker = new ArrayList<>();
        accuracyModificationListenerDefender = new ArrayList<>();

        //damage attacker
        damageEffectListenersAttacker.add(new AmuletOfBloodFury());
        damageEffectListenersDefender.add(new VeracSet());
        damageEffectListenersAttacker.add(new GuthanSet());

        //accuracy attacker
        accuracyModificationListenerAttacker.add(new BrimstoneRing());
        accuracyModificationListenerAttacker.add(new TumekensShadow());
        accuracyModificationListenerAttacker.add(new SalveAmulet());
        accuracyModificationListenerAttacker.add(new ThammaronSceptre());
        accuracyModificationListenerAttacker.add(new VoidEquipment());
        accuracyModificationListenerAttacker.add(new ZurielStaff());
        accuracyModificationListenerAttacker.add(new VoidEquipment());
        accuracyModificationListenerAttacker.add(new SlayerHelmets());

        //damage defender
        damageEffectListenersDefender.add(new ToxicStaffOfTheDead());
        damageEffectListenersDefender.add(new ElysianSpiritShield());
        damageEffectListenersDefender.add(new PrayerDamage());
    }

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        for (DamageEffectListener listener : damageEffectListenersAttacker) {
            if (listener.prepareDamageEffectForAttacker(entity, combatType, hit)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        for (DamageEffectListener listener : damageEffectListenersDefender) {
            if (listener.prepareDamageEffectForDefender(entity, combatType, hit)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        for (DamageEffectListener listener : accuracyModificationListenerAttacker) {
            if (listener.prepareMagicAccuracyModification(entity, combatType, magicAccuracy)) {
                return true;
            }
        }
        return false;
    }
}
