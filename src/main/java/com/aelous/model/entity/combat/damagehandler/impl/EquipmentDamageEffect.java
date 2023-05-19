package com.aelous.model.entity.combat.damagehandler.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.impl.armor.*;
import com.aelous.model.entity.combat.damagehandler.impl.sets.*;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.damagehandler.impl.typeless.PrayerDamage;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDamageEffect implements DamageEffectListener {
    private static final List<DamageEffectListener> damageEffectListenersAttacker;
    private static final List<DamageEffectListener> damageEffectListenersDefender;
    private static final List<DamageEffectListener> magicAccuracyModificationListenerAttacker;

    private static final List<DamageEffectListener> rangeAccuracyModificationListenerAttacker;
    private static final List<DamageEffectListener> meleeAccuracyModificationListenerAttacker;

    static {
        damageEffectListenersAttacker = initializeDamageEffectListenersAttacker();
        damageEffectListenersDefender = initializeDamageEffectListenersDefender();
        magicAccuracyModificationListenerAttacker = initializeMagicAccuracyModificationListenerAttacker();
        rangeAccuracyModificationListenerAttacker = initializeRangeAccuracyModificationListenerAttacker();
        meleeAccuracyModificationListenerAttacker = initializeMeleeAccuracyModificationListenerAttacker();
    }

    private static List<DamageEffectListener> initializeDamageEffectListenersAttacker() {
        List<DamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new AmuletOfBloodFury());
        listeners.add(new VeracSet());
        listeners.add(new GuthanSet());
        listeners.add(new KarilSet());
        return listeners;
    }

    private static List<DamageEffectListener> initializeDamageEffectListenersDefender() {
        List<DamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new ToxicStaffOfTheDead());
        listeners.add(new ElysianSpiritShield());
        listeners.add(new PrayerDamage());
        return listeners;
    }

    private static List<DamageEffectListener> initializeMagicAccuracyModificationListenerAttacker() {
        List<DamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new BrimstoneRing());
        listeners.add(new TumekensShadow());
        listeners.add(new SalveAmulet());
        listeners.add(new WildernessWeapon());
        listeners.add(new VoidEquipment());
        listeners.add(new ZurielStaff());
        listeners.add(new SlayerHelmets());
        return listeners;
    }

    private static List<DamageEffectListener> initializeRangeAccuracyModificationListenerAttacker() {
        List<DamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new CrystalSet());
        //listeners.add(new TwistedBow());
        listeners.add(new DragonHunterCrossbow());
        return listeners;
    }

    private static List<DamageEffectListener> initializeMeleeAccuracyModificationListenerAttacker() {
        List<DamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new VestaLongsword());
        listeners.add(new ObsidianArmor());
        return listeners;
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
        for (DamageEffectListener listener : magicAccuracyModificationListenerAttacker) {
            if (listener.prepareMagicAccuracyModification(entity, combatType, magicAccuracy)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        for (DamageEffectListener listener : meleeAccuracyModificationListenerAttacker) {
            if (listener.prepareMeleeAccuracyModification(entity, combatType, meleeAccuracy)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        for (DamageEffectListener listener : rangeAccuracyModificationListenerAttacker) {
            if (listener.prepareRangeAccuracyModification(entity, combatType, rangeAccuracy)) {
                return true;
            }
        }
        return false;
    }

    public EquipmentDamageEffect() {
    }

}
