package com.cryptic.model.entity.combat.damagehandler.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.impl.armor.*;
import com.cryptic.model.entity.combat.damagehandler.impl.sets.*;
import com.cryptic.model.entity.combat.damagehandler.impl.typeless.PoisonDamageEffect;
import com.cryptic.model.entity.combat.damagehandler.impl.typeless.PrayerDamage;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDamageEffect implements DamageEffectListener {
    private static final Logger logger = LogManager.getLogger(EquipmentDamageEffect.class);
    private static final Logger logger2 = LogManager.getLogger(EquipmentDamageEffect.class);
    private static final List<DamageEffectListener> damageEffectListenersAttacker;
    private static final List<DamageEffectListener> accuracyModificationListeners;

    static {
        damageEffectListenersAttacker = initializeDamageEffects();
        accuracyModificationListeners = accuracyModifications();
    }

    private static List<DamageEffectListener> initializeDamageEffects() {
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
        listeners.add(new PrayerDamage());
        return listeners;
    }

    private static List<DamageEffectListener> accuracyModifications() {
        List<DamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new BrimstoneRing());
        listeners.add(new TumekensShadow());
        listeners.add(new SalveAmulet());
        listeners.add(new WildernessWeapon());
        listeners.add(new VoidEquipment());
        listeners.add(new ZurielStaff());
        listeners.add(new SlayerHelmets());
        listeners.add(new VirtusSet());
        listeners.add(new CrystalSet());
        listeners.add(new TwistedBow());
        listeners.add(new SalveAmulet());
        listeners.add(new DragonHunterCrossbow());
        listeners.add(new VoidEquipment());
        listeners.add(new WildernessWeapon());
        listeners.add(new SlayerHelmets());
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
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        int modification = 0;
        for (DamageEffectListener listener : accuracyModificationListeners) {
            if (listener.prepareAccuracyModification(entity, combatType, accuracy) <= 0.0D) continue;
            modification += listener.prepareAccuracyModification(entity, combatType, accuracy);
            logger2.debug("Accuracy Effect {} Modification Effect {}", listener.getClass().getSimpleName(), modification);
        }
        return modification;
    }

}
