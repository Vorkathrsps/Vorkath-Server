package com.aelous.model.entity.combat.method.effects.typeless;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.combat.method.effects.registery.ListenerRegistry;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.Nex;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.player.Player;

import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;

public class PrayerDamage implements DamageEffectListener {
    public PrayerDamage() {
        ListenerRegistry.registerListener(this);
    }

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var attacker = (Nex) entity;
        var damage = hit.getDamage();
        NpcDefinition def = attacker.getAsNpc().def();
        String name = def.name;
        if (attacker.isNpc() && name != null && name.equalsIgnoreCase("Nex") && attacker.<Boolean>getAttribOr(AttributeKey.TURMOIL_ACTIVE, false)) {
            damage = (int) Math.floor(damage * 1.10F);
            hit.setDamage(damage);
            return true;
        }
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        var player = (Player) entity;
        var damage = hit.getDamage();
        boolean meleePrayer = hit.getCombatType() == CombatType.MELEE && Prayers.usingPrayer(player, PROTECT_FROM_MELEE);
        boolean rangedPrayer = hit.getCombatType() == CombatType.RANGED && Prayers.usingPrayer(player, PROTECT_FROM_MISSILES);
        boolean magicPrayer = hit.getCombatType() == CombatType.MAGIC && Prayers.usingPrayer(player, PROTECT_FROM_MAGIC);
        if (hit.getDamage() > 0 && hit.getTarget().isNpc()) {
            if (!hit.prayerIgnored && (meleePrayer || rangedPrayer || magicPrayer)) {
                hit.setDamage(0);
                return true;
            } else if (hit.getTarget().isNpc() && hit.getTarget().getAsNpc().id() == NpcIdentifiers.CORPOREAL_BEAST && hit.getCombatType() == CombatType.MAGIC && Prayers.usingPrayer(player, PROTECT_FROM_MAGIC)) {
                damage = (int) Math.floor(damage * 0.66F);
                hit.setDamage(damage);
            }
        } else if (hit.getDamage() > 0 && hit.getTarget().isPlayer()) {
            if (!hit.prayerIgnored && (meleePrayer || rangedPrayer || magicPrayer)) {
                damage = (int) Math.floor(damage * 0.4F);
                hit.setDamage(damage);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        return false;
    }
}
