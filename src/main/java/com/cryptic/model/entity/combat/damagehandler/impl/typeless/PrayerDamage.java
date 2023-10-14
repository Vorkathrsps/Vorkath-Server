package com.cryptic.model.entity.combat.damagehandler.impl.typeless;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;

import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.utility.Color;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

public class PrayerDamage implements DamageEffectListener {
    private static final Logger logger = LogManager.getLogger(PrayerDamage.class);

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof NPC npc) {
            var damage = hit.getDamage();
            NpcDefinition def = npc.def();
            String name = def.name;
            if (name != null) {
                if (name.equalsIgnoreCase("Nex")) {
                    if (npc.<Boolean>getAttribOr(AttributeKey.TURMOIL_ACTIVE, false)) {
                        damage = (int) Math.floor(damage * 1.10F);
                        hit.setDamage(damage);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    int[] ignoreFullNegatedDamge = new int[]
        {
            CORPOREAL_BEAST,
            KING_BLACK_DRAGON,
            KING_BLACK_DRAGON_6502,
            KING_BLACK_DRAGON_2642,
            GENERAL_GRAARDOR,
            GENERAL_GRAARDOR_6494,
            KREEARRA,
            KREEARRA_6492,
            COMMANDER_ZILYANA,
            COMMANDER_ZILYANA_6493,
            KRIL_TSUTSAROTH,
            KRIL_TSUTSAROTH_6495
        };
    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        var damage = hit.getDamage();
        boolean meleePrayer;
        boolean rangedPrayer;
        boolean magicPrayer;
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            meleePrayer = hit.getCombatType() == CombatType.MELEE && Prayers.usingPrayer(player, PROTECT_FROM_MELEE);
            rangedPrayer = hit.getCombatType() == CombatType.RANGED && Prayers.usingPrayer(player, PROTECT_FROM_MISSILES);
            magicPrayer = hit.getCombatType() == CombatType.MAGIC && Prayers.usingPrayer(player, PROTECT_FROM_MAGIC);
            if (target instanceof NPC npc) {
                if (ArrayUtils.contains(ignoreFullNegatedDamge, npc.id())) {
                    if (hit.getCombatType() == CombatType.MAGIC) {
                        if (hit.isAccurate()) {
                            if (!hit.prayerIgnored && meleePrayer || !hit.prayerIgnored && rangedPrayer || !hit.prayerIgnored && magicPrayer) {
                                damage = (int) Math.floor(damage * 0.66F);
                                hit.setDamage(damage);
                                logger.info("[DamageHandler] " + " NPC: " + npc.getMobName() + " Damage Output: " + hit.getDamage() + " CombatType: " + hit.getCombatType() + " Accurate: " + hit.isAccurate());
                                return true;
                            }
                        }
                    }
                } else if (!hit.prayerIgnored && meleePrayer || !hit.prayerIgnored && rangedPrayer || !hit.prayerIgnored && magicPrayer) {
                    if (hit.isAccurate()) {
                        if (hit.getDamage() > 0) {
                            hit.setDamage(0);
                            logger.info("[DamageHandler] " + " NPC: " + npc.getMobName() + " Damage Output: " + hit.getDamage() + " CombatType: " + hit.getCombatType() + " Accurate: " + hit.isAccurate() + " Melee: " + meleePrayer + " Ranged: " + rangedPrayer + " Magic: " + magicPrayer);
                            return true;
                        }
                    }
                }
            } else if (target instanceof Player enemy) {
                if (hit.isAccurate()) {
                    if (hit.getDamage() > 0) {
                        if (!hit.prayerIgnored && meleePrayer || !hit.prayerIgnored && rangedPrayer || !hit.prayerIgnored && magicPrayer) {
                            damage = (int) Math.floor(damage * 0.4F);
                            hit.setDamage(damage);
                            logger.info("[DamageHandler] " +" [Player: " + enemy.getDisplayName() + "]" +  " [Damage Output: " + hit.getDamage() + "] [CombatType: " + hit.getCombatType() + "] [Accurate: " + hit.isAccurate() + "] [Melee: " + meleePrayer + "] [Ranged: " + rangedPrayer + "] [Magic: " + magicPrayer + "]");
                            return true;
                        }
                    }
                }
            }
        }
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
