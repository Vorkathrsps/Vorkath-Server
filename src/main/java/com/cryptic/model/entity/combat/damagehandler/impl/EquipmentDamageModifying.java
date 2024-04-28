package com.cryptic.model.entity.combat.damagehandler.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.impl.armor.*;
import com.cryptic.model.entity.combat.damagehandler.impl.sets.*;
import com.cryptic.model.entity.combat.damagehandler.impl.typeless.PoisonDamageModifying;
import com.cryptic.model.entity.combat.damagehandler.impl.typeless.PrayerDamage;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDamageModifying implements DamageModifyingListener {
    private static final Logger logger = LogManager.getLogger(EquipmentDamageModifying.class);
    private static final Logger logger2 = LogManager.getLogger(EquipmentDamageModifying.class);
    private static final List<DamageModifyingListener> DAMAGE_MODIFYING_LISTENERS_ATTACKER;
    private static final List<DamageModifyingListener> accuracyModificationListeners;

    static {
        DAMAGE_MODIFYING_LISTENERS_ATTACKER = initializeDamageEffects();
        accuracyModificationListeners = accuracyModifications();
    }

    private static List<DamageModifyingListener> initializeDamageEffects() {
        List<DamageModifyingListener> listeners = new ArrayList<>();
        listeners.add(new AmuletOfBloodFury());
        listeners.add(new VeracSet());
        listeners.add(new GuthanSet());
        listeners.add(new KarilSet());
        listeners.add(new VirtusSet());
        listeners.add(new JusticiarSet());
        listeners.add(new DinhBulwark());
        listeners.add(new SalveAmulet());
        listeners.add(new LeafBladedBattleAxe());
        listeners.add(new PoisonDamageModifying());
        listeners.add(new ElysianSpiritShield());
        listeners.add(new BraceletOfEthereum());
        listeners.add(new ToxicStaffOfTheDead());
        listeners.add(new PrayerDamage());
        listeners.add(new KerisPartisan());
        return listeners;
    }

    private static List<DamageModifyingListener> accuracyModifications() {
        List<DamageModifyingListener> listeners = new ArrayList<>();
        listeners.add(new BrimstoneRing());
        listeners.add(new TumekensShadow());
        listeners.add(new SalveAmulet());
        listeners.add(new WildernessWeapon());
        listeners.add(new ZurielStaff());
        listeners.add(new SlayerHelmets());
        listeners.add(new VirtusSet());
        listeners.add(new CrystalSet());
        listeners.add(new TwistedBow());
        listeners.add(new DragonHunterCrossbow());
        listeners.add(new VoidEquipment());
        listeners.add(new VestaLongsword());
        listeners.add(new ObsidianArmor());
        listeners.add(new DragonMace());
        listeners.add(new DragonHunterLance());
        return listeners;
    }

    @Override
    public boolean isModifyAccuracy(Entity entity, AbstractAccuracy accuracy, Hit hit) {
        var affectsApplied = false;
        for (DamageModifyingListener listener : DAMAGE_MODIFYING_LISTENERS_ATTACKER) {
            if (listener.isModifyAccuracy(entity, accuracy, hit)) {
                affectsApplied = true;
            }
        }
        return affectsApplied;
    }

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var affectsApplied = false;
        for (DamageModifyingListener listener : DAMAGE_MODIFYING_LISTENERS_ATTACKER) {
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
        for (DamageModifyingListener listener : accuracyModificationListeners) {
            if (listener.prepareAccuracyModification(entity, combatType, accuracy) <= 0.0D) continue;
            modification += listener.prepareAccuracyModification(entity, combatType, accuracy);
            logger2.debug("Accuracy Effect {} Modification Effect {}", listener.getClass().getSimpleName(), modification);
        }
        return modification;
    }

}
