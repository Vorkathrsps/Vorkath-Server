package com.cryptic.model.entity.combat.damagehandler.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.cryptic.model.entity.combat.damagehandler.impl.armor.*;
import com.cryptic.model.entity.combat.damagehandler.impl.sets.*;
import com.cryptic.model.entity.combat.damagehandler.impl.typeless.PoisonDamageEffect;
import com.cryptic.model.entity.combat.damagehandler.impl.typeless.PrayerDamage;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDamageEffect implements DamageEffectListener {
    private static final Logger logger = LogManager.getLogger(EquipmentDamageEffect.class);
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
        listeners.add(new VirtusSet());
        listeners.add(new JusticiarSet());
        listeners.add(new DinhBulwark());
        listeners.add(new SalveAmulet());
        listeners.add(new LeafBladedBattleAxe());
        listeners.add(new PoisonDamageEffect());
        listeners.add(new ElysianSpiritShield());
        listeners.add(new BraceletOfEthereum());
        listeners.add(new ToxicStaffOfTheDead());
        return listeners;
    }

    private static List<DamageEffectListener> initializeDamageEffectListenersDefender() {
        List<DamageEffectListener> listeners = new ArrayList<>();
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
        listeners.add(new VirtusSet());
        return listeners;
    }

    private static List<DamageEffectListener> initializeRangeAccuracyModificationListenerAttacker() {
        List<DamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new CrystalSet());
        listeners.add(new TwistedBow());
        listeners.add(new SalveAmulet());
        listeners.add(new DragonHunterCrossbow());
        listeners.add(new VoidEquipment());
        listeners.add(new WildernessWeapon());
        listeners.add(new SlayerHelmets());
        return listeners;
    }

    private static List<DamageEffectListener> initializeMeleeAccuracyModificationListenerAttacker() {
        List<DamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new VoidEquipment());
        listeners.add(new VestaLongsword());
        listeners.add(new ObsidianArmor());
        listeners.add(new DragonMace());
        listeners.add(new SalveAmulet());
        listeners.add(new DragonHunterLance());
        listeners.add(new WildernessWeapon());
        listeners.add(new SlayerHelmets());
        return listeners;
    }

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var affectsApplied = false;
        for (DamageEffectListener listener : damageEffectListenersAttacker) {
            if (listener.prepareDamageEffectForAttacker(entity, combatType, hit)) {
                affectsApplied = true;
                logger.debug("Attack Effect {} Damage Effect {}", listener.getClass().getSimpleName(), hit.getDamage());
            }
        }
        return affectsApplied;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        var affectsApplied = false;
        for (DamageEffectListener listener : damageEffectListenersDefender) {
            if (listener.prepareDamageEffectForDefender(entity, combatType, hit)) {
                affectsApplied = true;
            }
        }
        return affectsApplied;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        var affectsApplied = false;
        for (DamageEffectListener listener : magicAccuracyModificationListenerAttacker) {
            if (listener.prepareMagicAccuracyModification(entity, combatType, magicAccuracy)) {
                affectsApplied = true;
            }
        }
        return affectsApplied;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        var affectsApplied = false;
        for (DamageEffectListener listener : meleeAccuracyModificationListenerAttacker) {
            if (listener.prepareMeleeAccuracyModification(entity, combatType, meleeAccuracy)) {
                affectsApplied = true;
            }
        }
        return affectsApplied;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        var affectsApplied = false;
        for (DamageEffectListener listener : rangeAccuracyModificationListenerAttacker) {
            if (listener.prepareRangeAccuracyModification(entity, combatType, rangeAccuracy)) {
                affectsApplied = true;
            }
        }
        return affectsApplied;
    }

}
