package com.cryptic.model.entity.combat.damagehandler.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.impl.equipment.*;
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
        return List.of
            (
                new AmuletOfBloodFury(),
                new VeracSet(),
                new GuthanSet(),
                new KarilSet(),
                new AhrimSet(),
                new VirtusSet(),
                new JusticiarSet(),
                new DinhBulwark(),
                new SalveAmulet(),
                new LeafBladedBattleAxe(),
                new PoisonDamageModifying(),
                new ElysianSpiritShield(),
                new BraceletOfEthereum(),
                new ToxicStaffOfTheDead(),
                new PrayerDamage(),
                new KerisPartisan(),
                new AmuletOfAvarice(),
                new BerserkerNecklace(),
                new Arclight(),
                new BlisterwoodFlail()
            );
    }

    private static List<DamageModifyingListener> accuracyModifications() {
        return List.of
            (
                new BrimstoneRing(),
                new TumekensShadow(),
                new SalveAmulet(),
                new WildernessWeapon(),
                new ZurielStaff(),
                new SlayerHelmets(),
                new VirtusSet(),
                new CrystalSet(),
                new TwistedBow(),
                new DragonHunterCrossbow(),
                new VoidEquipment(),
                new VestaLongsword(),
                new ObsidianArmor(),
                new DragonMace(),
                new DragonHunterLance(),
                new AmuletOfAvarice(),
                new Arclight(),
                new BlisterwoodFlail()
            );
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
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double modification = 0;
        for (DamageModifyingListener listener : accuracyModificationListeners) {
            if (listener.prepareAccuracyModification(entity, combatType, accuracy) <= 0) continue;
            modification += listener.prepareAccuracyModification(entity, combatType, accuracy);
            logger2.debug("Accuracy Effect {} Modification Effect {}", listener.getClass().getSimpleName(), modification);
        }
        return modification;
    }

}
